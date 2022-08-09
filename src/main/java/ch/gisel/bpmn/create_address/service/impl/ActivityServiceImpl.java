package ch.gisel.bpmn.create_address.service.impl;

import ch.gisel.bpmn.create_address.camunda.dto.*;
import ch.gisel.bpmn.create_address.camunda.service.ProcessDefinitionService;
import ch.gisel.bpmn.create_address.camunda.service.TaskService;
import ch.gisel.bpmn.create_address.dto.*;
import ch.gisel.bpmn.create_address.dto.bpmn.Definitions;
import ch.gisel.bpmn.create_address.dto.bpmn.SequenceFlow;
import ch.gisel.bpmn.create_address.entity.Activity;
import ch.gisel.bpmn.create_address.entity.ActivityProperty;
import ch.gisel.bpmn.create_address.mapper.ActivityMapper;
import ch.gisel.bpmn.create_address.mapper.ActivityPropertyMapper;
import ch.gisel.bpmn.create_address.repository.ActivityPropertyRepository;
import ch.gisel.bpmn.create_address.repository.ActivityRepository;
import ch.gisel.bpmn.create_address.service.ActivityService;
import ch.gisel.bpmn.create_address.type.ActivityStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Inject
    private ActivityRepository activityRepository;

    @Inject
    private ActivityMapper activityMapper;

    @Inject
    private ActivityPropertyRepository activityPropertyRepository;

    @Inject
    private ActivityPropertyMapper activityPropertyMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Resource(name = "processDefinitionServiceClient")
    private ProcessDefinitionService processDefinitionService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Resource(name = "taskServiceClient")
    private TaskService taskService;

    @Override
    public ActivityDTO createActivity(String activityType, ActivityInDTO inDTO) {
        Activity activity = new Activity();
        activity.setActivityType(activityType);
        activity = activityRepository.save(activity);

        if (inDTO != null) {
            if (inDTO.getActivityProperties() != null) {
                for (ActivityPropertyDTO activityPropertyDTO : inDTO.getActivityProperties()) {
                    ActivityProperty activityProperty = activityPropertyMapper.dtoToEntity(activityPropertyDTO);
                    activityProperty.setActivity(activity);
                    activityPropertyRepository.save(activityProperty);
                }
            }
        }

        return activityMapper.entityToDTO(activity);
    }

    @Override
    public ActivityWorkContextOutDTO workActivity(long activityId) {
        Activity activity = activityRepository.findById(activityId).get();
        if (activity.getProcessReference() == null) {
            List<ActivityProperty> propertyList = activityPropertyRepository.findByActivity(activity);
            StartInstanceInDTO inDTO = null;
            if (propertyList != null && propertyList.size() > 0) {
                inDTO = new StartInstanceInDTO();
                inDTO.setVariables(new HashMap<>());
                for (ActivityProperty property : propertyList) {
                    inDTO.getVariables().put(property.getName(), new VariableDTO(property.getValue(), property.getType()));
                }
            }

            StartInstanceOutDTO startInstanceOutDTO = processDefinitionService.startInstance(activity.getActivityType(), inDTO);
            activity.setProcessReference(startInstanceOutDTO.getId());
            activity.setStatus(ActivityStatus.RUNNING);
            activity = activityRepository.save(activity);
        }

        List<TaskOutDTO> taskList = taskService.getTasksForProcessInstance(activity.getProcessReference());

        if (taskList == null || taskList.size() == 0) {
            return null;
        }
        TaskOutDTO firstTask = taskList.get(0);
        ActivityWorkContextOutDTO activityWorkContextDTO = new ActivityWorkContextOutDTO();
        activityWorkContextDTO.setActivityId(activityId);
        activityWorkContextDTO.setTaskId(firstTask.getId());
        activityWorkContextDTO.setProcessInstanceId(activity.getProcessReference());

        TaskDefinitionDTO taskDefinitionDTO = getTaskDefinition(firstTask);

        activityWorkContextDTO.setScreenId(taskDefinitionDTO.getScreenId());
        activityWorkContextDTO.setOutObjects(createOutObjects(firstTask.getId(), taskDefinitionDTO));
        activityWorkContextDTO.setInObjects(createInObjects(taskDefinitionDTO));
        activityWorkContextDTO.setNavigation(taskDefinitionDTO.getNavigation());
        return activityWorkContextDTO;
    }

    private TaskDefinitionDTO getTaskDefinition(TaskOutDTO task) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TaskDefinitionDTO taskDefinition = objectMapper.readValue(task.getDescription(), TaskDefinitionDTO.class);
            taskDefinition.setNavigation(getTaskNavigation(task));
            return taskDefinition;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private NavigationDTO getTaskNavigation(TaskOutDTO task) {
        Bpmn2OutDTO bpmn2OutDTO = processDefinitionService.getBpmn20Xml(task.getProcessDefinitionId());

        Definitions definitions;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            definitions = (Definitions) jaxbUnmarshaller.unmarshal(new StringReader(bpmn2OutDTO.getBpmn20Xml()));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        List<SequenceFlow> sequenceFlows = definitions.getProcess().getSequenceFlows().stream().filter(f -> f.getSourceRef().equals(task.getTaskDefinitionKey())).collect(Collectors.toList());
        NavigationDTO navigation = new NavigationDTO();
        for (SequenceFlow sequenceFlow : sequenceFlows) {
            if (sequenceFlow.getName() == null) {
                continue;
            }
            switch (sequenceFlow.getName()) {
                case "Back":
                    navigation.setBack(true);
                    break;
                case "Next":
                    navigation.setNext(true);
                    break;
                case "Cancel":
                    navigation.setCancel(true);
                    break;
                case "Finish":
                    navigation.setFinish(true);
                    break;
            }
        }
        return navigation;
    }

    private Map<String, ActivityVariableDTO> createOutObjects(String taskId, TaskDefinitionDTO taskDefinitionDTO) {
        if (taskDefinitionDTO.getOutVariables() == null || taskDefinitionDTO.getOutVariables().size() == 0) {
            return null;
        }
        String outVariableNames = taskDefinitionDTO.getOutVariables().stream().map(td -> td.getName()).collect(Collectors.joining(","));
        Map<String, VariableDTO> formVariables = taskService.getFormVariables(taskId, outVariableNames);
        Map<String, ActivityVariableDTO> outObjectMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (TaskDefinitionVariableDTO taskDefinitionVariable : taskDefinitionDTO.getOutVariables()) {
            VariableDTO variableDTO = formVariables.get(taskDefinitionVariable.getName());
            if (variableDTO != null) {
                try {
                    Object object = objectMapper.readValue(variableDTO.getValue().toString(), Class.forName(taskDefinitionVariable.getType()));
                    outObjectMap.put(taskDefinitionVariable.getName(), new ActivityVariableDTO(object, taskDefinitionVariable.getType()));
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("Cannot read task variable " + taskDefinitionVariable.getName() + "(" + variableDTO.getType() + "): " + variableDTO.getValue(), e);
                }
            } else {
                outObjectMap.put(taskDefinitionVariable.getName(), new ActivityVariableDTO(null, taskDefinitionVariable.getType()));
            }
        }
        return outObjectMap;
    }

    private Map<String, ActivityVariableDTO> createInObjects(TaskDefinitionDTO taskDefinitionDTO) {
        if (taskDefinitionDTO.getInVariables() == null || taskDefinitionDTO.getInVariables().size() == 0) {
            return null;
        }
        Map<String, ActivityVariableDTO> inObjectMap = new HashMap<>();
        for (TaskDefinitionVariableDTO taskDefinitionVariable : taskDefinitionDTO.getInVariables()) {
            inObjectMap.put(taskDefinitionVariable.getName(), new ActivityVariableDTO(null, taskDefinitionVariable.getType()));
        }
        return inObjectMap;
    }

    @Override
    public ActivityWorkContextOutDTO finishActivityTask(long activityId, ActivityWorkContextInDTO inDTO) {
        Activity activity = activityRepository.findById(activityId).get();
        if (activity.getProcessReference() == null) {
            throw new RuntimeException("Activity has no process reference: " + activity);
        }
        List<TaskOutDTO> taskList = taskService.getTasksForProcessInstance(activity.getProcessReference());

        if (taskList == null || taskList.size() == 0) {
            return null;
        }
        TaskOutDTO firstTask = taskList.get(0);
        if (!firstTask.getId().equals(inDTO.getTaskId())) {
            throw new RuntimeException("Given task is not the active task of activity");
        }

        TaskDefinitionDTO taskDefinitionDTO = getTaskDefinition(firstTask);
        Map<String, ActivityVariableDTO> expectedInObjects = createInObjects(taskDefinitionDTO);
        validateInObjects(expectedInObjects, inDTO.getInObjects());

        SubmitFormInDTO submitFormInDTO = createSubmitFormInDTO(inDTO.getInObjects());
        taskService.submitForm(inDTO.getTaskId(), submitFormInDTO);

        return workActivity(activityId);
    }

    private void validateInObjects(Map<String, ActivityVariableDTO> expectedInObjects, Map<String, ActivityVariableDTO> inObjects) {
        if ((expectedInObjects == null || expectedInObjects.size() == 0) && (inObjects == null || inObjects.size() == 0)) {
            return;
        }
        if (expectedInObjects.size() != inObjects.size()) {
            throw new RuntimeException("Size of inObjects is not the exptected size of inObjects");
        }
        if (!inObjects.keySet().containsAll(expectedInObjects.keySet())) {
            throw new RuntimeException("Not all expected variables are present");
        }
        for (String key : expectedInObjects.keySet()) {
            if (!expectedInObjects.get(key).getType().equals(inObjects.get(key).getType())) {
                throw new RuntimeException("Wrong type provided for property " + key + " Expected: " + expectedInObjects.get(key).getType() + " Provided: " + inObjects.get(key).getType());
            }
        }
    }

    private SubmitFormInDTO createSubmitFormInDTO(Map<String, ActivityVariableDTO> inObjects) {
        if (inObjects == null || inObjects.size() == 0) {
            return null;
        }
        Map<String, VariableDTO> variableMap = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<String, ActivityVariableDTO> entry : inObjects.entrySet()) {
            try {
                String jsonValue = objectMapper.writeValueAsString(entry.getValue().getValue());
                //Object object = objectMapper.readValue(jsonValue, Class.forName(entry.getValue().getType()));
                variableMap.put(entry.getKey(), new VariableDTO(jsonValue, getVariableType(entry.getValue().getType())));
            } catch (IOException e) {
                throw new RuntimeException("Cannot read variable: " + entry.getKey(), e);
            }
        }
        SubmitFormInDTO inDTO = new SubmitFormInDTO();
        inDTO.setVariables(variableMap);
        return inDTO;
    }

    private String getVariableType(String javaType) {
        switch (javaType) {
            case "java.lang.Long":
                return "long";
            case "java.lang.Boolean":
                return "Boolean";
            default:
                return "String";
        }
    }

    @Override
    public ActivityWorkContextOutDTO saveActivityTask(long activityId, ActivityWorkContextInDTO inDTO) {
        //TODO implement
        return null;
    }
}

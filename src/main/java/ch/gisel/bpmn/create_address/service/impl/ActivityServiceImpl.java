package ch.gisel.bpmn.create_address.service.impl;

import ch.gisel.bpmn.create_address.camunda.dto.*;
import ch.gisel.bpmn.create_address.camunda.service.ProcessDefinitionService;
import ch.gisel.bpmn.create_address.camunda.service.TaskService;
import ch.gisel.bpmn.create_address.dto.*;
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
import java.io.IOException;
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
                    inDTO.getVariables().put(property.getName(), createVariableDTO(property.getValue(), property.getType()));
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
        return activityWorkContextDTO;
    }

    private VariableDTO createVariableDTO(String value, String type) {
        Object valueObject;
        switch (type) {
            case "long":
                valueObject = Long.valueOf(value);
                break;
            case "Boolean":
                valueObject = Boolean.valueOf(value);
                break;
            default:
                valueObject = value;
        }
        return new VariableDTO(valueObject, type);
    }

    private TaskDefinitionDTO getTaskDefinition(TaskOutDTO task) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(task.getDescription(), TaskDefinitionDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        //TODO service call does not work yet
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
                Object object = objectMapper.readValue(jsonValue, Class.forName(entry.getValue().getType()));
                variableMap.put(entry.getKey(), new VariableDTO(object, entry.getValue().getType()));
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException("Cannot read variable: " + entry.getKey(), e);
            }
        }
        SubmitFormInDTO inDTO = new SubmitFormInDTO();
        inDTO.setVariables(variableMap);
        return inDTO;
    }

    @Override
    public ActivityWorkContextOutDTO saveActivityTask(long activityId, ActivityWorkContextInDTO inDTO) {
        //TODO implement
        return null;
    }
}

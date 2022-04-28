package ch.gisel.bpmn.create_address.service.impl;

import ch.gisel.bpmn.create_address.camunda.dto.StartInstanceInDTO;
import ch.gisel.bpmn.create_address.camunda.dto.StartInstanceOutDTO;
import ch.gisel.bpmn.create_address.camunda.dto.TaskOutDTO;
import ch.gisel.bpmn.create_address.camunda.dto.VariableDTO;
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

    @Resource(name = "processDefinitionServiceClient")
    private ProcessDefinitionService processDefinitionService;

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
    public ActivityWorkContextDTO workActivity(long activityId) {
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

        if (taskList != null && taskList.size() > 0) {
            TaskOutDTO firstTask = taskList.get(0);
            ActivityWorkContextDTO activityWorkContextDTO = new ActivityWorkContextDTO();
            activityWorkContextDTO.setTaskId(firstTask.getId());
            activityWorkContextDTO.setProcessInstanceId(activity.getProcessReference());

            ObjectMapper objectMapper = new ObjectMapper();
            TaskDefinitionDTO taskDefinitionDTO;
            try {
                taskDefinitionDTO = objectMapper.readValue(firstTask.getDescription(), TaskDefinitionDTO.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            activityWorkContextDTO.setScreenId(taskDefinitionDTO.getScreenId());
            activityWorkContextDTO.setOutObjects(createOutObjects(firstTask.getId(), taskDefinitionDTO));
            return activityWorkContextDTO;
        }
        return null;
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

    private Map<String, Object> createOutObjects(String taskId, TaskDefinitionDTO taskDefinitionDTO) {
        if (taskDefinitionDTO.getOutVariables() == null || taskDefinitionDTO.getOutVariables().size() == 0) {
            return null;
        }
        String outVariableNames = taskDefinitionDTO.getOutVariables().stream().map(td -> td.getName()).collect(Collectors.joining(","));
        Map<String, VariableDTO> formVariables = taskService.getFormVariables(taskId, outVariableNames);
        Map<String, Object> outObjectMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (TaskDefinitionVariableDTO taskDefinitionVariable : taskDefinitionDTO.getOutVariables()) {
            VariableDTO variableDTO = formVariables.get(taskDefinitionVariable.getName());
            if (variableDTO != null) {
                try {
                    Object object = objectMapper.readValue(variableDTO.getValue().toString(), Class.forName(taskDefinitionVariable.getType()));
                    outObjectMap.put(taskDefinitionVariable.getName(), object);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("Cannot read task variable " + taskDefinitionVariable.getName() + "(" + variableDTO.getType() + "): " + variableDTO.getValue(), e);
                }
            } else {
                outObjectMap.put(taskDefinitionVariable.getName(), null);
            }
        }
        return outObjectMap;
    }

    @Override
    public ActivityWorkContextDTO finishActivityTask(long activityId, ActivityWorkContextDTO inDTO) {
        //TODO implement
        return null;
    }

    @Override
    public ActivityWorkContextDTO saveActivityTask(long activityId, ActivityWorkContextDTO inDTO) {
        //TODO implement
        return null;
    }
}

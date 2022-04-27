package ch.gisel.bpmn.create_address.service.impl;

import ch.gisel.bpmn.create_address.camunda.dto.StartInstanceInDTO;
import ch.gisel.bpmn.create_address.camunda.dto.StartInstanceOutDTO;
import ch.gisel.bpmn.create_address.camunda.dto.TaskOutDTO;
import ch.gisel.bpmn.create_address.camunda.dto.VariableDTO;
import ch.gisel.bpmn.create_address.camunda.service.ProcessDefinitionService;
import ch.gisel.bpmn.create_address.camunda.service.TaskService;
import ch.gisel.bpmn.create_address.dto.ActivityDTO;
import ch.gisel.bpmn.create_address.dto.ActivityInDTO;
import ch.gisel.bpmn.create_address.dto.ActivityPropertyDTO;
import ch.gisel.bpmn.create_address.dto.ActivityWorkContextDTO;
import ch.gisel.bpmn.create_address.entity.Activity;
import ch.gisel.bpmn.create_address.entity.ActivityProperty;
import ch.gisel.bpmn.create_address.mapper.ActivityMapper;
import ch.gisel.bpmn.create_address.mapper.ActivityPropertyMapper;
import ch.gisel.bpmn.create_address.repository.ActivityPropertyRepository;
import ch.gisel.bpmn.create_address.repository.ActivityRepository;
import ch.gisel.bpmn.create_address.service.ActivityService;
import ch.gisel.bpmn.create_address.type.ActivityStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

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
            activityWorkContextDTO.setTaskName(firstTask.getName());
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

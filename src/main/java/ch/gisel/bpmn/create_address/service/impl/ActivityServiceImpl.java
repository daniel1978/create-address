package ch.gisel.bpmn.create_address.service.impl;

import ch.gisel.bpmn.create_address.camunda.dto.StartInstanceOutDTO;
import ch.gisel.bpmn.create_address.camunda.dto.TaskOutDTO;
import ch.gisel.bpmn.create_address.camunda.service.ProcessDefinitionService;
import ch.gisel.bpmn.create_address.camunda.service.TaskService;
import ch.gisel.bpmn.create_address.dto.ActivityDTO;
import ch.gisel.bpmn.create_address.dto.ActivityWorkContextDTO;
import ch.gisel.bpmn.create_address.entity.Activity;
import ch.gisel.bpmn.create_address.mapper.ActivityMapper;
import ch.gisel.bpmn.create_address.repository.ActivityRepository;
import ch.gisel.bpmn.create_address.service.ActivityService;
import ch.gisel.bpmn.create_address.type.ActivityStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Inject
    private ActivityRepository activityRepository;

    @Inject
    private ActivityMapper activityMapper;

    @Resource(name = "processDefinitionServiceClient")
    private ProcessDefinitionService processDefinitionService;

    @Resource(name = "taskServiceClient")
    private TaskService taskService;

    @Override
    public ActivityDTO createActivity(String activityType) {
        Activity activity = new Activity();
        activity.setActivityType(activityType);
        activity = activityRepository.save(activity);
        return activityMapper.entityToDTO(activity);
    }

    @Override
    public ActivityWorkContextDTO workActivity(long activityId) {
        Activity activity = activityRepository.findById(activityId).get();
        if (activity.getProcessReference() == null) {
            StartInstanceOutDTO startInstanceOutDTO = processDefinitionService.startInstance(activity.getActivityType());
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

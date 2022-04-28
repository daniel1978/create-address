package ch.gisel.bpmn.create_address.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ActivityWorkContextOutDTO {

    private long activityId;
    private String taskId;
    private String screenId;
    private Map<String, ActivityVariableDTO> outObjects;
    private Map<String, ActivityVariableDTO> inObjects;

    //only for debugging purpose -> remove later
    @Deprecated
    private String processInstanceId;


}

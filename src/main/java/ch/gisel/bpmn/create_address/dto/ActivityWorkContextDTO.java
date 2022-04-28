package ch.gisel.bpmn.create_address.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ActivityWorkContextDTO {


    //only for debugging purpose -> remove later
    @Deprecated
    private String processInstanceId;
    //only for debugging purpose -> remove later
    @Deprecated
    private String taskId;

    private String screenId;
    private Map<String, Object> outObjects;
}

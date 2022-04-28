package ch.gisel.bpmn.create_address.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ActivityWorkContextInDTO {

    private String taskId;
    private Map<String, ActivityVariableDTO> inObjects;
}

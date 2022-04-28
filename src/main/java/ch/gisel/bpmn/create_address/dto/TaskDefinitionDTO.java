package ch.gisel.bpmn.create_address.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskDefinitionDTO {

    private String screenId;
    private List<TaskDefinitionVariableDTO> outVariables;
    private List<TaskDefinitionVariableDTO> inVariables;

}

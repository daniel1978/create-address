package ch.gisel.bpmn.create_address.camunda.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StartInstanceInDTO {

    private Map<String, VariableDTO> variables;
}

package ch.gisel.bpmn.create_address.camunda.dto;

import lombok.Data;

import java.util.Map;

@Data
public abstract class VariablesBaseDTO {

    private Map<String, Object> variables;
}

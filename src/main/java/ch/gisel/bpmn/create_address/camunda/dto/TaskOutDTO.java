package ch.gisel.bpmn.create_address.camunda.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskOutDTO {

    private String id;
    private String name;
    private String description;

    private String processDefinitionId;
    private String taskDefinitionKey;
}

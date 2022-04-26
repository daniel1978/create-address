package ch.gisel.bpmn.create_address.camunda.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartInstanceOutDTO {

    private String id;
}

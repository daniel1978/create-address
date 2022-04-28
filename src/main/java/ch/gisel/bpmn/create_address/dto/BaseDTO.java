package ch.gisel.bpmn.create_address.dto;

import lombok.Data;

@Data
public abstract class BaseDTO {

    private ValidationResultDTO validationResult = new ValidationResultDTO();
}

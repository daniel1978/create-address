package ch.gisel.bpmn.create_address.dto;

import ch.gisel.bpmn.create_address.type.ValidationStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationResultDTO {

    private List<ValidationMessageDTO> validationMessages = new ArrayList<>();

    public ValidationStatus getValidationStatus() {
        ValidationStatus status = ValidationStatus.OK;
        for (ValidationMessageDTO message : validationMessages) {
            switch (message.getValidationStatus()) {
                case ERROR:
                    return ValidationStatus.ERROR;
                case WARNING:
                    status = ValidationStatus.WARNING;
                    break;
                case OK:
                    break;
            }
        }
        return status;
    }

    public void addValidationMessage(String field, String validationMessage, ValidationStatus status) {
        validationMessages.add(new ValidationMessageDTO(field, validationMessage, status));
    }

}

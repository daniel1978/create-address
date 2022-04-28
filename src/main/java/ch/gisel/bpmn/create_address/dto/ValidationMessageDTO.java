package ch.gisel.bpmn.create_address.dto;

import ch.gisel.bpmn.create_address.type.ValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationMessageDTO {

    private String field;
    private String validationMessage;
    private ValidationStatus validationStatus;
}

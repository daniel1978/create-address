package ch.gisel.bpmn.create_address.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityVariableDTO {

    private Object value;
    private String type;
}

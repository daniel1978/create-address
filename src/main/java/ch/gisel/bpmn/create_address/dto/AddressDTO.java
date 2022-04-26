package ch.gisel.bpmn.create_address.dto;

import ch.gisel.bpmn.create_address.type.AddressType;
import lombok.Data;

@Data
public class AddressDTO {

    private Long id;
    private String name;
    private String street;
    private AddressType addressType;
}

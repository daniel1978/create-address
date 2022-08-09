package ch.gisel.bpmn.create_address.dto;


import lombok.Data;

@Data
public class NavigationDTO {

    private boolean back;
    private boolean next;
    private boolean cancel;
    private boolean finish;
}

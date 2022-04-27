package ch.gisel.bpmn.create_address.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActivityInDTO {

    private List<ActivityPropertyDTO> activityProperties;
}

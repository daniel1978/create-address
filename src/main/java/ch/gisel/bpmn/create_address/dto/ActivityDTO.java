package ch.gisel.bpmn.create_address.dto;

import ch.gisel.bpmn.create_address.type.ActivityStatus;
import lombok.Data;

@Data
public class ActivityDTO {

    private Long id;
    private ActivityStatus status;
    private String activityType;
}

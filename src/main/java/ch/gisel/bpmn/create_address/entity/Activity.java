package ch.gisel.bpmn.create_address.entity;

import ch.gisel.bpmn.create_address.type.ActivityStatus;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_activity")
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActivityStatus status = ActivityStatus.CREATED;

    private String activityType;

    private String processReference;

}

package ch.gisel.bpmn.create_address.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_activity_property")
@Data
public class ActivityProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    private String name;
    private String value;
    private String type;
}

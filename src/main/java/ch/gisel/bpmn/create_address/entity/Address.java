package ch.gisel.bpmn.create_address.entity;

import ch.gisel.bpmn.create_address.type.AddressType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_address")
@Data
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String street;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;
}

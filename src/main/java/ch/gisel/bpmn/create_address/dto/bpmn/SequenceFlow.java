package ch.gisel.bpmn.create_address.dto.bpmn;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;

@Getter
public class SequenceFlow {

    @XmlAttribute
    private String sourceRef;

    @XmlAttribute
    private String name;

    @XmlAnyElement(lax = true)
    private List<Object> anything;
}

package ch.gisel.bpmn.create_address.dto.bpmn;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Process {

    @Getter
    @XmlElement(name = "sequenceFlow", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    List<SequenceFlow> sequenceFlows;

    @XmlAnyElement(lax = true)
    private List<Object> anything;
}

package ch.gisel.bpmn.create_address.dto.bpmn;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class Definitions {

    @Getter
    @XmlElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private Process process;

    @XmlAnyElement(lax = true)
    private List<Object> anything;
}

package ch.gisel.bpmn.create_address.camunda.dto;

import ch.gisel.bpmn.create_address.dto.ActivityInDTO;
import ch.gisel.bpmn.create_address.dto.ActivityPropertyDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestVariableJSON {

    @Test
    public void testVariables() throws JsonProcessingException {
        StartInstanceInDTO inDTO = new StartInstanceInDTO();
        Map<String, VariableDTO> variables = new HashMap<>();

        VariableDTO aVariable = new VariableDTO("aStringValue", "String");
        variables.put("aVariable", aVariable);

        VariableDTO anotherVariable = new VariableDTO("true", "Boolean");
        variables.put("anotherVariable", anotherVariable);
        inDTO.setVariables(variables);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(inDTO);
        System.out.println(json);
    }

    @Test
    public void testActivityInDTO() throws JsonProcessingException {
        ActivityInDTO inDTO = new ActivityInDTO();
        ActivityPropertyDTO propertyDTO = new ActivityPropertyDTO();
        propertyDTO.setName("addressId");
        propertyDTO.setType("long");
        propertyDTO.setValue("7");
        List<ActivityPropertyDTO> propertyDTOList = new ArrayList<>();
        propertyDTOList.add(propertyDTO);
        inDTO.setActivityProperties(propertyDTOList);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(inDTO);
        System.out.println(json);

    }
}

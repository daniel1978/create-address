package ch.gisel.bpmn.create_address.camunda.service;

import ch.gisel.bpmn.create_address.camunda.dto.SubmitFormInDTO;
import ch.gisel.bpmn.create_address.camunda.dto.TaskOutDTO;
import ch.gisel.bpmn.create_address.camunda.dto.VariableDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("task/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TaskService {

    @GET
    @Path("")
    List<TaskOutDTO> getTasksForProcessInstance(@QueryParam("processInstanceId") String processInstanceId);

    @GET
    @Path("{id}/form-variables")
    Map<String, VariableDTO> getFormVariables(@PathParam("id") String taskId, @QueryParam("variableNames") String variableNames);

    @POST
    @Path("{id}/submit-form")
    void submitForm(@PathParam("id") String taskId, SubmitFormInDTO inDTO);
}

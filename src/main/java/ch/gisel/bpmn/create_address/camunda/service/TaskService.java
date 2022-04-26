package ch.gisel.bpmn.create_address.camunda.service;

import ch.gisel.bpmn.create_address.camunda.dto.TaskOutDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("task/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TaskService {

    @GET
    @Path("")
    List<TaskOutDTO> getTasksForProcessInstance(@QueryParam("processInstanceId") String processInstanceId);
}

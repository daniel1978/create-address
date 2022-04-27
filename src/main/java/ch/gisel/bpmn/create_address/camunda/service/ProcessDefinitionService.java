package ch.gisel.bpmn.create_address.camunda.service;

import ch.gisel.bpmn.create_address.camunda.dto.StartInstanceInDTO;
import ch.gisel.bpmn.create_address.camunda.dto.StartInstanceOutDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("process-definition/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProcessDefinitionService {

    @POST
    @Path("key/{key}/start")
    StartInstanceOutDTO startInstance(@PathParam("key") String key, StartInstanceInDTO inDTO);
}

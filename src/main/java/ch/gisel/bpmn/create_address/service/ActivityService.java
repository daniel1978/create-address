package ch.gisel.bpmn.create_address.service;

import ch.gisel.bpmn.create_address.dto.ActivityDTO;
import ch.gisel.bpmn.create_address.dto.ActivityInDTO;
import ch.gisel.bpmn.create_address.dto.ActivityWorkContextInDTO;
import ch.gisel.bpmn.create_address.dto.ActivityWorkContextOutDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("activity/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ActivityService {

    @PUT
    @Path("create")
    ActivityDTO createActivity(@QueryParam("activityType") String activityType, ActivityInDTO inDTO);

    @PUT
    @Path("{id}/work")
    ActivityWorkContextOutDTO workActivity(@PathParam("id") long activityId);

    @PUT
    @Path("{id}/task-finish")
    ActivityWorkContextOutDTO finishActivityTask(@PathParam("id") long activityId, ActivityWorkContextInDTO inDTO);

    @PUT
    @Path("{id}/task-save")
    ActivityWorkContextOutDTO saveActivityTask(@PathParam("id") long activityId, ActivityWorkContextInDTO inDTO);
}

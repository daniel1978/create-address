package ch.gisel.bpmn.create_address.service;

import ch.gisel.bpmn.create_address.dto.AddressDTO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("address/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AddressService {

    @GET
    @Path("{id}")
    AddressDTO loadAddress(@PathParam("id") Long id);
}

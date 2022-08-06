package ch.gisel.bpmn.create_address.camunda.worker;

import ch.gisel.bpmn.create_address.dto.AddressDTO;
import ch.gisel.bpmn.create_address.service.AddressService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Log
public class AddressWorker {

    private AddressService addressService;

    @ZeebeWorker(type = "load-address-service")
    public void loadAddress(final JobClient client, final ActivatedJob job) {
        final long addressId = (long) job.getVariablesAsMap().get("addressId");
        log.info("Loading address with id: " + addressId);
        AddressDTO address = addressService.loadAddress(addressId);
        client.newCompleteCommand(job)
                .variables(Map.of("address", address))
                .send();
    }

    @ZeebeWorker(type = "validate-address-service")
    private void validateAddress(final JobClient client, final ActivatedJob job) {
        final AddressDTO address = (AddressDTO) job.getVariablesAsMap().get("address");
        log.info("Validate address with id: " + address.getId());
        AddressDTO validatedAddress = addressService.validateAddress(address);
        client.newCompleteCommand(job)
                .variables(Map.of("address", validatedAddress))
                .send();
    }
}

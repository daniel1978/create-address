package ch.gisel.bpmn.create_address.service.impl;

import ch.gisel.bpmn.create_address.dto.AddressDTO;
import ch.gisel.bpmn.create_address.service.AddressService;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {

    @Override
    public AddressDTO loadAddress(Long id) {
        AddressDTO addressDTO = new AddressDTO();
        return addressDTO;
    }
}

package ch.gisel.bpmn.create_address.service.impl;

import ch.gisel.bpmn.create_address.dto.AddressDTO;
import ch.gisel.bpmn.create_address.entity.Address;
import ch.gisel.bpmn.create_address.mapper.AddressMapper;
import ch.gisel.bpmn.create_address.repository.AddressRepository;
import ch.gisel.bpmn.create_address.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class AddressServiceImpl implements AddressService {

    @Inject
    private AddressMapper addressMapper;

    @Inject
    private AddressRepository addressRepository;

    @Override
    public AddressDTO loadAddress(Long id) {
        if (id == 0) {
            return new AddressDTO();
        }
        Address address = addressRepository.findById(id).get();
        AddressDTO addressDTO = addressMapper.entityToDTO(address);
        return addressDTO;
    }

    @Override
    public AddressDTO saveAddress(AddressDTO addressDTO) {
        Address address = addressMapper.dtoToEntity(addressDTO);
        Address savedEntity = addressRepository.save(address);
        return addressMapper.entityToDTO(savedEntity);
    }
}

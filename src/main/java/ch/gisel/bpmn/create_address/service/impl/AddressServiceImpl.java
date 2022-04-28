package ch.gisel.bpmn.create_address.service.impl;

import ch.gisel.bpmn.create_address.dto.AddressDTO;
import ch.gisel.bpmn.create_address.dto.ValidationResultDTO;
import ch.gisel.bpmn.create_address.entity.Address;
import ch.gisel.bpmn.create_address.mapper.AddressMapper;
import ch.gisel.bpmn.create_address.repository.AddressRepository;
import ch.gisel.bpmn.create_address.service.AddressService;
import ch.gisel.bpmn.create_address.type.ValidationStatus;
import org.apache.commons.lang3.StringUtils;
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
        addressDTO = validateAddress(addressDTO);
        return addressDTO;
    }

    @Override
    public AddressDTO saveAddress(AddressDTO addressDTO) {
        addressDTO = validateAddress(addressDTO);
        if (addressDTO.getValidationResult().getValidationStatus() == ValidationStatus.ERROR) {
            throw new RuntimeException("Cannot save invalid address");
        }
        Address address = addressMapper.dtoToEntity(addressDTO);
        Address savedEntity = addressRepository.save(address);
        return addressMapper.entityToDTO(savedEntity);
    }

    @Override
    public AddressDTO validateAddress(AddressDTO address) {
        address.setValidationResult(new ValidationResultDTO());
        if (StringUtils.isBlank(address.getName())) {
            address.getValidationResult().addValidationMessage("name", "No name defined", ValidationStatus.ERROR);
        }
        if (StringUtils.isBlank(address.getStreet())) {
            address.getValidationResult().addValidationMessage("street", "No street defined", ValidationStatus.WARNING);
        }
        if (address.getAddressType() == null) {
            address.getValidationResult().addValidationMessage("addressType", "No addressType defined", ValidationStatus.ERROR);
        }
        return address;
    }
}

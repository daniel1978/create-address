package ch.gisel.bpmn.create_address.mapper;

import ch.gisel.bpmn.create_address.dto.AddressDTO;
import ch.gisel.bpmn.create_address.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address dtoToEntity(AddressDTO dto);
    AddressDTO entityToDTO(Address entity);
}

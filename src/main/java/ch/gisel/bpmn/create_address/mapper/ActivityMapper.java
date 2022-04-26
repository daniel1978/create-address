package ch.gisel.bpmn.create_address.mapper;

import ch.gisel.bpmn.create_address.dto.ActivityDTO;
import ch.gisel.bpmn.create_address.entity.Activity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityDTO entityToDTO(Activity entity);
}

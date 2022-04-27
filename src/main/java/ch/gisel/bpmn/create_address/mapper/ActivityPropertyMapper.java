package ch.gisel.bpmn.create_address.mapper;

import ch.gisel.bpmn.create_address.dto.ActivityPropertyDTO;
import ch.gisel.bpmn.create_address.entity.ActivityProperty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityPropertyMapper {
    ActivityProperty dtoToEntity(ActivityPropertyDTO dto);
}

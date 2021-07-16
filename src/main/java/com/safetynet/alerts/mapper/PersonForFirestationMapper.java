package com.safetynet.alerts.mapper;

import com.safetynet.alerts.dto.PersonForFirestationDto;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonForFirestationMapper {
    PersonForFirestationMapper INSTANCE = Mappers.getMapper(PersonForFirestationMapper.class);

    PersonForFirestationDto toDto(Person person);
}

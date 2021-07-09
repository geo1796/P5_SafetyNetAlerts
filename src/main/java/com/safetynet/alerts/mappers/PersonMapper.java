package com.safetynet.alerts.mappers;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    PersonDto toDto(Person person);
}

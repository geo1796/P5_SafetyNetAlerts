package com.safetynet.alerts.mappers;

import com.safetynet.alerts.dto.PersonEmailDto;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface PersonEmailMapper {
    PersonEmailMapper INSTANCE = Mappers.getMapper(PersonEmailMapper.class);

    PersonEmailDto toDto(Person person);
}

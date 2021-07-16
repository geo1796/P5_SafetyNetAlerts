package com.safetynet.alerts.mapper;

import com.safetynet.alerts.dto.ChildDto;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChildMapper {
    ChildMapper INSTANCE = Mappers.getMapper(ChildMapper.class);

    ChildDto toDto(Person person);
}

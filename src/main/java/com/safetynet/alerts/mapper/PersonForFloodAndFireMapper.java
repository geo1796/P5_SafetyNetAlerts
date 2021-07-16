package com.safetynet.alerts.mapper;

import com.safetynet.alerts.dto.PersonForFloodAndFireDto;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonForFloodAndFireMapper {
    PersonForFloodAndFireMapper INSTANCE = Mappers.getMapper(PersonForFloodAndFireMapper.class);

    @Mapping(source = "person.lastName", target = "lastName")
    @Mapping(source = "person.firstName", target = "firstName")
    PersonForFloodAndFireDto toDto(Person person, MedicalRecord medicalRecord);
}

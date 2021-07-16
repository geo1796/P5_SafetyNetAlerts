package com.safetynet.alerts.mapper;

import com.safetynet.alerts.dto.PersonForPersonInfoDto;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonForPersonInfoMapper {
    PersonForPersonInfoMapper INSTANCE = Mappers.getMapper(PersonForPersonInfoMapper.class);

    @Mapping(source = "person.lastName", target = "lastName")
    @Mapping(source = "person.firstName", target = "firstName")
    PersonForPersonInfoDto toDto(Person person, MedicalRecord medicalRecord);
}

package com.safetynet.alerts.mappers;

import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonInfoMapper {
    PersonInfoMapper INSTANCE = Mappers.getMapper(PersonInfoMapper.class);

    @Mapping(source = "person.lastName", target = "lastName")
    @Mapping(source = "person.firstName", target = "firstName")
    PersonInfoDto toDto(Person person, MedicalRecord medicalRecord);
}

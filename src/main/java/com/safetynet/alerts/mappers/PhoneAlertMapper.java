package com.safetynet.alerts.mappers;

import com.safetynet.alerts.dto.PersonPhoneDto;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhoneAlertMapper {
    PhoneAlertMapper INSTANCE = Mappers.getMapper(PhoneAlertMapper.class);

    PersonPhoneDto toDto(Person person);
}

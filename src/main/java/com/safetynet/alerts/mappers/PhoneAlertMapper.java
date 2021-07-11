package com.safetynet.alerts.mappers;

import com.safetynet.alerts.dto.PhoneAlertDto;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhoneAlertMapper {
    PhoneAlertMapper INSTANCE = Mappers.getMapper(PhoneAlertMapper.class);

    PhoneAlertDto toDto(Person person);
}

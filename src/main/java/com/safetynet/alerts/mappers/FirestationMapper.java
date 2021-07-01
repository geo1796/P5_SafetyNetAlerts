package com.safetynet.alerts.mappers;

import com.safetynet.alerts.dto.FirestationDto;
import com.safetynet.alerts.model.Firestation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FirestationMapper {

    FirestationDto toDto(Firestation firestation);
}

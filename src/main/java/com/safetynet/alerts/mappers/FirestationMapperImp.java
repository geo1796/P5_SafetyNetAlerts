package com.safetynet.alerts.mappers;

import com.safetynet.alerts.dto.FirestationDto;
import com.safetynet.alerts.model.Firestation;
import org.springframework.stereotype.Component;

@Component
public class FirestationMapperImp implements FirestationMapper {

    @Override
    public FirestationDto toDto(Firestation firestation) {
        if ( firestation == null ) {
            return null;
        }
        FirestationDto firestationDto = new FirestationDto();

        firestationDto.setId(firestation.getId());
        firestationDto.setStation(firestation.getStation());
        firestationDto.setAdress(firestation.getAdress());

        return firestationDto;
    }
}

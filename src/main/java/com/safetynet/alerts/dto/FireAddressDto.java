package com.safetynet.alerts.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class FireAddressDto {

    private int stationNumber;
    private List<PersonForFloodAndFireDto> personForFloodAndFireDtoList;

}

package com.safetynet.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PersonsCoveredByThisFirestationDto {
    private int numberOfAdults;
    private int numberOfChildren;
    private List<PersonForFirestationDto> personCoveredByThisStation;

}

package com.safetynet.alerts.dto;

import lombok.Data;

import java.util.List;

@Data
public class PersonsCoveredByThisFirestationDto {
    private int numberOfAdults;
    private int numberOfChildren;
    private List<PersonDto> personCoveredByThisStation;

    public PersonsCoveredByThisFirestationDto(int numberOfAdults, int numberOfChildren, List<PersonDto> personDtoList){
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.personCoveredByThisStation = personDtoList;
    }
}

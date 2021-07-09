package com.safetynet.alerts.model;

import com.safetynet.alerts.dto.PersonDto;
import lombok.Data;

import java.util.List;

@Data
public class PersonListByFirestation {
    private int numberOfAdults;
    private int numberOfChildren;
    private List<PersonDto> personCoveredByThisStation;

    public PersonListByFirestation(int numberOfAdults, int numberOfChildren, List<PersonDto> personDtoList){
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.personCoveredByThisStation = personDtoList;
    }
}

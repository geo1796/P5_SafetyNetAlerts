package com.safetynet.alerts.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class PersonForFloodAndFireDto {

    private String lastName;
    private String firstName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;

}

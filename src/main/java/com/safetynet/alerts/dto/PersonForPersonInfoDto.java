package com.safetynet.alerts.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@RequiredArgsConstructor
public class PersonForPersonInfoDto {

    private String lastName;
    private String firstName;
    private String address;
    private String email;
    private int age;
    private ArrayList<String> medications;
    private ArrayList<String> allergies;

}

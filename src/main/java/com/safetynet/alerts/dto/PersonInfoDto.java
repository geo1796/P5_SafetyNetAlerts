package com.safetynet.alerts.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PersonInfoDto {

    private String lastName;
    private String firstName;
    private String address;
    private String email;
    private int age;
    private ArrayList<String> medications;
    private ArrayList<String> allergies;

}

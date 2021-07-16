package com.safetynet.alerts.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PersonForFirestationDto {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;

}

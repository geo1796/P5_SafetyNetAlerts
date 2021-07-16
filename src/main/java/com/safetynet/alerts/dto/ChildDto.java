package com.safetynet.alerts.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ChildDto{
    private String lastName;
    private String firstName;
    private int age;
    private List<HomeMemberDto> homeMemberDtoList;

}

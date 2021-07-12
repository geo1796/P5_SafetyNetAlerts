package com.safetynet.alerts.dto;

import com.safetynet.alerts.util.StringDateHandler;
import lombok.Data;

import java.util.List;

@Data
public class ChildAlertDto {
    private String lastName;
    private String firstName;
    private int age;
    private List<HomeMemberDto> homeMemberDtoList;

    public void fromChildDto(ChildDto childDto){
        this.firstName = childDto.getFirstName();
        this.lastName = childDto.getLastName();
        if(!(childDto.getBirthdate() == null)) {
            StringDateHandler stringDateHandler = new StringDateHandler("MM/dd/yyyy");
            this.age = stringDateHandler.getAgeFromStringDate(childDto.getBirthdate());
        }
    }
}

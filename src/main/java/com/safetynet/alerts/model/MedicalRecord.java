package com.safetynet.alerts.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MedicalRecord {
    private String firstName;
    private String lastName;
    private Date birthdate;
    private List<String> medications;
    private List<String> allergies;

    public Date getBirthdate() {
        Date copy = this.birthdate;
        return copy;
    }

    public void setBirthdate(Date birthdate) {
        if(birthdate != null)
            this.birthdate = new Date(birthdate.getTime());
    }
}

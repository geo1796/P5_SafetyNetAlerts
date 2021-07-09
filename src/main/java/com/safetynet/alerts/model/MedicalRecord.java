package com.safetynet.alerts.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "medicalRecords")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String birthdate = "01/01/2000";
    private ArrayList<String> medications;
    private ArrayList<String> allergies;

}

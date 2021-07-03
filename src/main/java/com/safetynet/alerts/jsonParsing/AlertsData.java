package com.safetynet.alerts.jsonParsing;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import lombok.Data;

import java.util.List;

@Data
public class AlertsData {

    private List<Firestation> firestations;
    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;

}

package com.safetynet.alerts.util;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;

import java.util.Optional;

public class PersonAndMedicalRecordConverter {

    public MedicalRecord findMedicalRecordFromPerson(Person person, MedicalRecordRepository medicalRecordRepository){
        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.findById(person.getId());
        return optionalMedicalRecord.orElse(null);
    }

    public Person findPersonFromMedicalRecord(MedicalRecord medicalRecord, PersonRepository personRepository){
        Optional<Person> optionalPerson = personRepository.findById(medicalRecord.getId());
        return optionalPerson.orElse(null);
    }

}

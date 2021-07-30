package com.safetynet.alerts.util;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;

import java.util.Optional;

/**
 * A util class to convert Person object to MedicalRecord object and the MedicalRecord object to Person object
 */
public class PersonAndMedicalRecordConverter {

    /**
     *
     * @param person the person for who you want the medicalRecord
     * @param medicalRecordRepository the repository to call to obtain the medicalRecord
     * @return the MedicalRecord object corresponding to this person or null if there is not one
     */

    public MedicalRecord findMedicalRecordFromPerson(Person person, MedicalRecordRepository medicalRecordRepository){
        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.findById(person.getId());
        return optionalMedicalRecord.orElse(null);
    }

    /**
     *
     * @param medicalRecord the medicalRecord of the person you want to find
     * @param personRepository the repository to call to obtain the person
     * @return the Person object corresponding to this medicalRecord or null if there is not one
     */

    public Person findPersonFromMedicalRecord(MedicalRecord medicalRecord, PersonRepository personRepository){
        Optional<Person> optionalPerson = personRepository.findById(medicalRecord.getId());
        return optionalPerson.orElse(null);
    }

    /**
     *
     * @param medicalRecord the medicalRecord from which you want to get the age
     * @return the age corresponding to the birthdate in the medicalRecord param
     */

    public int getAgeFromMedicalRecord(MedicalRecord medicalRecord){
        StringDateHandler stringDateHandler = new StringDateHandler();
        return stringDateHandler.getAgeFromStringDate(medicalRecord.getBirthdate());
    }
}

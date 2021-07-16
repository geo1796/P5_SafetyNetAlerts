package com.safetynet.alerts.integration;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.PersonAndMedicalRecordConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PersonAndMedicalRecordConverterIT {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private PersonRepository personRepository;
    private static PersonAndMedicalRecordConverter classUnderTest;
    private Person person;
    private MedicalRecord medicalRecord;

    @BeforeAll
    public static void initClassUnderTest(){
        classUnderTest = new PersonAndMedicalRecordConverter();
    }


    @ParameterizedTest()
    @ValueSource(longs = {1, 2, 3, 4, 5})
    public void testFindMedicalRecordFromPerson(long id){
        person = personRepository.findById(id).get();
        medicalRecord = classUnderTest.findMedicalRecordFromPerson(person, medicalRecordRepository);

        assertEquals(person.getFirstName(), medicalRecord.getFirstName());
        assertEquals(person.getLastName(), medicalRecord.getLastName());
    }

    @ParameterizedTest()
    @ValueSource(longs = {1, 2, 3, 4, 5})
    public void testFindPersonFromMedicalRecord(long id){
        medicalRecord = medicalRecordRepository.findById(id).get();
        person = classUnderTest.findPersonFromMedicalRecord(medicalRecord, personRepository);

        assertEquals(person.getFirstName(), medicalRecord.getFirstName());
        assertEquals(person.getLastName(), medicalRecord.getLastName());
    }

    @Test
    public void testGetAgeFromMedicalRecord(){
        medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("01/01/2000");
        assertEquals(21, classUnderTest.getAgeFromMedicalRecord(medicalRecord));
    }

}

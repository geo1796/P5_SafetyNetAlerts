package com.safetynet.alerts.integration;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.StringDateHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StringDateHandlerIT {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private PersonRepository personRepository;
    private StringDateHandler classUnderTest = new StringDateHandler("MM/dd/yyyy");
    private Person testCase = new Person();

    @Test
    public void testIsAdult() {
        assertTrue(classUnderTest.isAdult(personRepository.findById(1L).get(), medicalRecordRepository));// L'id 1 est porté par l'adulte John Boyd
        assertFalse(classUnderTest.isAdult(personRepository.findById(4L).get(), medicalRecordRepository)); // L'id 4 est porté par l'enfant Roger Boyd
    }

    @Test
    public void tesIsAdultCornerCase(){
        testCase.setId(1000L);
        testCase.setFirstName("John");
        testCase.setLastName("Doe");
        testCase.setCity("city");
        testCase.setEmail("email");
        testCase.setZip(1000);
        testCase.setAddress("address");
        testCase.setPhone("phone");

        assertThrows(IllegalArgumentException.class, () -> classUnderTest.isAdult(testCase, medicalRecordRepository));
    }

}

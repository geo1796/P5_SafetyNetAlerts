package com.safetynet.alerts;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.util.PersonAndMedicalRecordConverter;
import com.safetynet.alerts.util.StringDateHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StringDateHandlerTest {

    private static StringDateHandler stringDateHandler;
    private static String testCaseAdult;
    private static String testCaseChild;


    @Mock
    PersonAndMedicalRecordConverter personAndMedicalRecordConverter;

    @Mock
    Person person;

    @Mock
    MedicalRecordRepository
    medicalRecordRepository;

    @BeforeAll
    public static void init(){
        testCaseAdult = "01/29/1999";
        testCaseChild = "01/29/2021";
        stringDateHandler = new StringDateHandler("MM/dd/yyyy");
    }

    @Test
    public void testConvertStringToLocalDate(){
        LocalDate expectedDate = LocalDate.of(1999, 1, 29);
        LocalDate actualDate = stringDateHandler.convertStringToLocalDate(testCaseAdult);

        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testConvertIncorrectStringFormatToLocalDate(){
        assertThrows(DateTimeParseException.class, () -> stringDateHandler.convertStringToLocalDate(""));
    }

    @Test
    public void testGetAgeFromLocalDate(){
        assertEquals(22, stringDateHandler.getAgeFromStringDate(testCaseAdult));
    }

    @Test
    public void testGetAgeFromIncorrectLocalDate(){
        assertThrows(DateTimeParseException.class, () -> stringDateHandler.getAgeFromStringDate(""));
    }

    @Test
    public void testGetAgeWithFutureLocalDate(){
        assertThrows(IllegalArgumentException.class, () -> stringDateHandler.getAgeFromStringDate("01/01/3000"));
    }

    @Test
    public void testIsAdult(){
        MedicalRecord medicalRecord = new MedicalRecord();
        when(personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository))
                .thenReturn(medicalRecord);

        medicalRecord.setBirthdate((testCaseAdult));

        assertEquals(true, stringDateHandler.isAdult(person, medicalRecordRepository, personAndMedicalRecordConverter));


        medicalRecord.setBirthdate((testCaseChild));
        assertEquals(false, stringDateHandler.isAdult(person, medicalRecordRepository, personAndMedicalRecordConverter));
    }


}

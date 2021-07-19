package com.safetynet.alerts;


import com.safetynet.alerts.util.StringDateHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class StringDateHandlerTest {

    private static StringDateHandler stringDateHandler;
    private static String testCase;


    @BeforeAll
    public static void init(){
        testCase = "01/29/1999";
        stringDateHandler = new StringDateHandler("MM/dd/yyyy");
    }

    @Test
    public void testConvertStringToLocalDate(){
        LocalDate expectedDate = LocalDate.of(1999, 1, 29);
        LocalDate actualDate = stringDateHandler.convertStringToLocalDate(testCase);

        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testConvertIncorrectStringFormatToLocalDate(){
        assertThrows(DateTimeParseException.class, () -> stringDateHandler.convertStringToLocalDate(""));
    }

    @Test
    public void testGetAgeFromLocalDate(){
        assertEquals(22, stringDateHandler.getAgeFromStringDate(testCase));
    }

    @Test
    public void testGetAgeFromIncorrectLocalDate(){
        assertThrows(DateTimeParseException.class, () -> stringDateHandler.getAgeFromStringDate(""));
    }

    @Test
    public void testGetAgeWithFutureLocalDate(){
        assertThrows(IllegalArgumentException.class, () -> stringDateHandler.getAgeFromStringDate("01/01/3000"));
    }



}

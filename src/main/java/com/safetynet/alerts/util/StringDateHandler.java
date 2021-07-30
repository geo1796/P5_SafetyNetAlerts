package com.safetynet.alerts.util;


import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * A util class to convert date String in format "MM/dd/yyyy" to a year durating as an int
 */

public class StringDateHandler {

    /**
     *
     * @param birthDate the date you want to convert to LocalDate
     * @return
     */
    public LocalDate convertStringToLocalDate(String birthDate){
        String pattern = "MM/dd/yyyy";
        return LocalDate.parse(birthDate, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     *
     * @param birthDate the date you want to convert to years as an int
     * @return
     */
    public int getAgeFromStringDate(String birthDate){
        LocalDate birthDateToLocalDate = convertStringToLocalDate(birthDate);
        LocalDate now = LocalDate.now();
        Period period = Period.between(birthDateToLocalDate, now);
        int age = period.getYears();
        if(age >= 0)
            return age;
        throw new IllegalArgumentException("age négatif");
    }

    /**
     *
     * @param person the person you want to check weither he or she is an adult
     * @param medicalRecordRepository
     * @return true if the person is an adult, false if the person is a child
     */
    public Boolean isAdult(Person person, MedicalRecordRepository medicalRecordRepository){
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();
        MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);

        if(medicalRecord != null) {
            return getAgeFromStringDate(medicalRecord.getBirthdate()) > 18;
        }
        else
            throw new IllegalArgumentException("Impossible de déterminer l'âge de la personne ID = " + person.getId());
    }

}

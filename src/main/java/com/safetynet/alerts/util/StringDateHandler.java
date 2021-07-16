package com.safetynet.alerts.util;


import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class StringDateHandler {

    private final String pattern;

    public StringDateHandler(String pattern){
        this.pattern = pattern;
    }

    public LocalDate convertStringToLocalDate(String birthDate){
        return LocalDate.parse(birthDate, DateTimeFormatter.ofPattern(this.pattern));
    }

    public int getAgeFromStringDate(String birthDate){
        LocalDate birthDateToLocalDate = convertStringToLocalDate(birthDate);
        LocalDate now = LocalDate.now();
        Period period = Period.between(birthDateToLocalDate, now);
        int age = period.getYears();
        if(age >= 0)
            return age;
        throw new IllegalArgumentException("age négatif");
    }

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

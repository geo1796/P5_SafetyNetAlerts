package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.MedicalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;

@AllArgsConstructor
@RestController
public class MedicalRecordController {

    MedicalRecordService medicalRecordService;

    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            LocalDate.parse(medicalRecord.getBirthdate(), DateTimeFormatter.ofPattern("MM/dd/yyyy")); // va laver une DateTimeParseException si la date est invalide
            if(medicalRecord.getFirstName().equals("") || medicalRecord.getLastName().equals("")) {
                throw new DataIntegrityViolationException("not valid medicalRecord");
            }
            return new ResponseEntity<>(medicalRecordService.saveMedicalRecord(medicalRecord), HttpStatus.CREATED);
        }
        catch(DataIntegrityViolationException | DateTimeParseException e)
        {
            return new ResponseEntity<>(new MedicalRecord(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/medicalRecord/{id}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable("id") final Long id, @RequestBody MedicalRecord medicalRecord) {
        Optional<MedicalRecord> m = medicalRecordService.getMedicalRecord(id);
        String newBirthDate = medicalRecord.getBirthdate();

        if(m.isPresent()) {
            MedicalRecord currentMedicalRecord = m.get();

            if(!newBirthDate.equals(currentMedicalRecord.getBirthdate()) && !newBirthDate.equals("")) {
                try {
                    LocalDate.parse(newBirthDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                } catch (DateTimeParseException e) {
                    return new ResponseEntity<>(medicalRecord, HttpStatus.BAD_REQUEST);
                }
            }

            ArrayList<String> medications = medicalRecord.getMedications();
            if(medications != null) {
                currentMedicalRecord.setMedications(medications);
            }

            ArrayList<String> allergies = medicalRecord.getAllergies();
            if(allergies != null) {
                currentMedicalRecord.setAllergies(allergies);
            }

            String birthDate = medicalRecord.getBirthdate();
            if(!birthDate.equals("")) {
                currentMedicalRecord.setBirthdate(birthDate);
            }

            medicalRecordService.saveMedicalRecord(currentMedicalRecord);
            return new ResponseEntity<>(currentMedicalRecord, HttpStatus.OK);
        }
        else {
            return createMedicalRecord(medicalRecord);
        }
    }

    @GetMapping("/medicalRecord/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecord(@PathVariable("id") final Long id) {
        Optional<MedicalRecord> medicalRecord = medicalRecordService.getMedicalRecord(id);
        return medicalRecord.map(record -> new ResponseEntity<>(record, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(medicalRecord.orElse(null), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/medicalRecords")
    public Iterable<MedicalRecord> getMedicalRecords() {
        return medicalRecordService.getMedicalRecords();
    }

    @DeleteMapping("/medicalRecord/{lastName}/{firstName}")
    public ResponseEntity<MedicalRecord> deleteMedicalRecord(@PathVariable("lastName") final String lastName, @PathVariable("firstName") final String firstName) {

        try
        {
            return new ResponseEntity<>(medicalRecordService.deleteMedicalRecord(lastName, firstName), HttpStatus.NO_CONTENT);
        }
        catch(EmptyResultDataAccessException e)
        {
            return new ResponseEntity<>(new MedicalRecord(), HttpStatus.NOT_FOUND);
        }
        catch (IllegalArgumentException e)
        {
            return new ResponseEntity<>(new MedicalRecord(), HttpStatus.MULTIPLE_CHOICES);
        }
    }
}

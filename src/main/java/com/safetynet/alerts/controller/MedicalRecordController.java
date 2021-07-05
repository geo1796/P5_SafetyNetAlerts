package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@AllArgsConstructor
@RestController
public class MedicalRecordController {

    MedicalRecordService medicalRecordService;

    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            return new ResponseEntity<>(medicalRecordService.saveMedicalRecord(medicalRecord), HttpStatus.CREATED);
        }
        catch(DataIntegrityViolationException e)
        {
            return new ResponseEntity<>(new MedicalRecord(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/medicalRecord/{id}")
    public MedicalRecord updateMedicalRecord(@PathVariable("id") final Long id, @RequestBody MedicalRecord medicalRecord) {
        Optional<MedicalRecord> m = medicalRecordService.getMedicalRecord(id);
        if(m.isPresent()) {
            MedicalRecord currentMedicalRecord = m.get();

            ArrayList<String> medications = medicalRecord.getMedications();
            if(medications != null) {
                currentMedicalRecord.setMedications(medications);
            }

            ArrayList<String> allergies = medicalRecord.getAllergies();
            if(allergies != null) {
                currentMedicalRecord.setAllergies(allergies);
            }

            String birthDate = medicalRecord.getBirthdate();
            if(birthDate != null) {
                currentMedicalRecord.setBirthdate(birthDate);
            }

            medicalRecordService.saveMedicalRecord(currentMedicalRecord);
            return currentMedicalRecord;
        } else {
            return null;
        }
    }

    @GetMapping("/medicalRecord/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecord(@PathVariable("id") final Long id) {
        Optional<MedicalRecord> medicalRecord = medicalRecordService.getMedicalRecord(id);
        if(medicalRecord.isPresent())
            return new ResponseEntity<>(medicalRecord.get(), HttpStatus.OK);
        return new ResponseEntity<>(medicalRecord.orElse(null), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/medicalRecords")
    public Iterable<MedicalRecord> getMedicalRecords() {
        return medicalRecordService.getMedicalRecords();
    }

    @DeleteMapping("/medicalRecord/{id}")
    public ResponseEntity<MedicalRecord> deleteMedicalRecord(@PathVariable("id") final Long id) {

        try
        {
            medicalRecordService.deleteMedicalRecord(id);
        }
        catch(EmptyResultDataAccessException e)
        {
            return new ResponseEntity<>(new MedicalRecord(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new MedicalRecord(), HttpStatus.NO_CONTENT);

    }
}

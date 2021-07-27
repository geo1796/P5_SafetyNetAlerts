package com.safetynet.alerts.controller;

import com.safetynet.alerts.jsonParsing.Json;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import static com.safetynet.alerts.util.ResponseEntityAndLoggerHandler.badResponse;
import static com.safetynet.alerts.util.ResponseEntityAndLoggerHandler.goodResponse;

@AllArgsConstructor
@RestController
public class MedicalRecordController {

    private static final Logger logger = LogManager.getLogger("MedicalRecordController");
    private final MedicalRecordService medicalRecordService;

    @PostMapping("/medicalRecord")
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        logger.info("calling method : createMedicalRecord / body : " + Json.toJson(medicalRecord));
        try {
            LocalDate.parse(medicalRecord.getBirthdate(), DateTimeFormatter.ofPattern("MM/dd/yyyy")); // va lever une DateTimeParseException si la date est invalide
            if(medicalRecord.getFirstName().equals("") || medicalRecord.getLastName().equals(""))
                throw new DataIntegrityViolationException("not valid medicalRecord");
            else if(medicalRecord.getId() != null)
                throw new IllegalArgumentException("id is not null");
            return goodResponse(medicalRecordService.saveMedicalRecord(medicalRecord), HttpStatus.CREATED, logger);
        }
        catch(DataIntegrityViolationException | DateTimeParseException | IllegalArgumentException e)
        {
            return badResponse(new MedicalRecord(), HttpStatus.BAD_REQUEST, e, "error creating new medicalRecord", logger);
        }
    }

    @PutMapping("/medicalRecord/{id}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable("id") final Long id, @RequestBody MedicalRecord medicalRecord) {
        logger.info("calling method : updateMedicalRecord / id = " + id + " / body : " + Json.toJson(medicalRecord));
        Optional<MedicalRecord> m = medicalRecordService.getMedicalRecord(id);
        String newBirthDate = medicalRecord.getBirthdate();

        if(m.isPresent()) {
            MedicalRecord currentMedicalRecord = m.get();

            if(!newBirthDate.equals(currentMedicalRecord.getBirthdate()) && !newBirthDate.equals("")) {
                try {
                    LocalDate.parse(newBirthDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                } catch (DateTimeParseException e) {
                    return badResponse(medicalRecord, HttpStatus.BAD_REQUEST, e, "error updating medicalRecord", logger);
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
            return goodResponse(currentMedicalRecord, HttpStatus.OK, logger);
        }
        else {
            return createMedicalRecord(medicalRecord);
        }
    }

    @GetMapping("/medicalRecord/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecord(@PathVariable("id") final Long id) {
        logger.info("calling method : getMedicalRecord / id = " + id);
        Optional<MedicalRecord> medicalRecord = medicalRecordService.getMedicalRecord(id);
        ResponseEntity<MedicalRecord> result = medicalRecord.map(record -> new ResponseEntity<>(record, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(medicalRecord.orElse(null), HttpStatus.NOT_FOUND));
        HttpStatus httpStatus = result.getStatusCode();
        if(httpStatus == HttpStatus.OK)
            logger.info("HTTP response : " + result.getStatusCode());
        else{
            logger.error("No medicalRecord for id = " + id);
            logger.error("HTTP response : " + httpStatus);
        }
        return result;
    }

    @GetMapping("/medicalRecords")
    public ResponseEntity<Iterable<MedicalRecord>> getMedicalRecords() {
        logger.info("calling method : getMedicalRecords");
        ResponseEntity<Iterable<MedicalRecord>> result = new ResponseEntity<>(medicalRecordService.getMedicalRecords(), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    @DeleteMapping("/medicalRecord")
    public ResponseEntity<MedicalRecord> deleteMedicalRecord(@RequestParam("lastName") final String lastName, @RequestParam("firstName") final String firstName) {
        logger.info("calling method : deleteMedicalRecord / lastName = " + lastName + " / firstName = " + firstName);
        try
        {
            return goodResponse(medicalRecordService.deleteMedicalRecord(lastName, firstName), HttpStatus.NO_CONTENT, logger);
        }
        catch(EmptyResultDataAccessException e)
        {
            return badResponse(new MedicalRecord(), HttpStatus.NOT_FOUND, e, "error deleting medicalRecord", logger);
        }
        catch (IllegalArgumentException e)
        {
            return badResponse(new MedicalRecord(), HttpStatus.MULTIPLE_CHOICES, e, "error deleting medicalRecord", logger);
        }
    }
}

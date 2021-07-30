package com.safetynet.alerts.util;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * A util class to simplify the controllers coding
 */
public class ResponseEntityAndLoggerHandler {

    public static ResponseEntity<Firestation> goodResponse(Firestation firestation, HttpStatus httpStatus, Logger logger){
        ResponseEntity<Firestation> responseEntity = new ResponseEntity<>(firestation, httpStatus);
        logger.info("HTTP status : " + httpStatus);
        return responseEntity;
    }

    public static ResponseEntity<Firestation> badResponse(Firestation firestation, HttpStatus httpStatus, Exception e, String errorMsg, Logger logger){
        ResponseEntity<Firestation> responseEntity = new ResponseEntity<>(firestation, httpStatus);
        logger.error(errorMsg + " : " + e);
        logger.error("HTTP status : " + httpStatus);
        return responseEntity;
    }

    public static ResponseEntity<Iterable<Firestation>> goodResponse(Iterable<Firestation> firestations, HttpStatus httpStatus, Logger logger){
        ResponseEntity<Iterable<Firestation>> responseEntity = new ResponseEntity<>(firestations, httpStatus);
        logger.info("HTTP status : " + httpStatus);
        return responseEntity;
    }

    public static ResponseEntity<Person> goodResponse(Person person, HttpStatus httpStatus, Logger logger){
        ResponseEntity<Person> responseEntity = new ResponseEntity<>(person, httpStatus);
        logger.info("HTTP status : " + httpStatus);
        return responseEntity;
    }

    public static ResponseEntity<Person> badResponse(Person person, HttpStatus httpStatus, Exception e, String errorMsg, Logger logger){
        ResponseEntity<Person> responseEntity = new ResponseEntity<>(person, httpStatus);
        logger.error(errorMsg + " : " + e);
        logger.error("HTTP status : " + httpStatus);
        return responseEntity;
    }

    public static ResponseEntity<MedicalRecord> goodResponse(MedicalRecord medicalRecord, HttpStatus httpStatus, Logger logger){
        ResponseEntity<MedicalRecord> responseEntity = new ResponseEntity<>(medicalRecord, httpStatus);
        logger.info("HTTP status : " + httpStatus);
        return responseEntity;
    }

    public static ResponseEntity<MedicalRecord> badResponse(MedicalRecord medicalRecord, HttpStatus httpStatus, Exception e, String errorMsg, Logger logger){
        ResponseEntity<MedicalRecord> responseEntity = new ResponseEntity<>(medicalRecord, httpStatus);
        logger.error(errorMsg + " : " + e);
        logger.error("HTTP status : " + httpStatus);
        return responseEntity;
    }
}

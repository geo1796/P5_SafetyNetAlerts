package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.safetynet.alerts.dto.PersonPhoneDto;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.dto.PeopleCoveredByThisFirestationDto;
import com.safetynet.alerts.service.FirestationService;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static com.safetynet.alerts.jsonParsing.Json.toJson;
import static com.safetynet.alerts.util.ResponseEntityAndLoggerHandler.badResponse;
import static com.safetynet.alerts.util.ResponseEntityAndLoggerHandler.goodResponse;

@AllArgsConstructor
@RestController
public class FirestationController {

    private static final Logger logger = LogManager.getLogger("FirestationController");
    private final FirestationService firestationService;

    @PostMapping("/firestation")
    public ResponseEntity<Firestation> createFirestation(@RequestBody Firestation firestation) {
        JsonNode body = toJson(firestation);
        logger.info("calling method : createFirestation / body : " + body);
        try {
            if (firestation.getId() != null)
                throw new IllegalArgumentException("id is not null");
            return goodResponse(firestationService.saveFirestation(firestation), HttpStatus.CREATED, logger);
        }
        catch(DataIntegrityViolationException | IllegalArgumentException e)
        {
            return badResponse(new Firestation(), HttpStatus.BAD_REQUEST, e, "error creating firestation", logger);
        }
    }

    @GetMapping("/firestations")
    public ResponseEntity<Iterable<Firestation>> getFirestations() {
        return goodResponse(firestationService.getFirestations(), HttpStatus.OK, logger);
    }

    @GetMapping("/firestation/{id}")
    public ResponseEntity<Firestation> getFirestation(@PathVariable("id") final Long id) {
        logger.info("calling method : getFirestation / id = " + id);
        Optional<Firestation> firestation = firestationService.getFirestation(id);
        return firestation.map(value -> goodResponse(value, HttpStatus.OK, logger)).orElseGet(() -> badResponse(new Firestation(), HttpStatus.NOT_FOUND, new IllegalArgumentException(), "No firestation for id : " + id, logger));

    }

    @DeleteMapping("/firestation/{id}")
    public ResponseEntity<Firestation> deleteFirestation(@PathVariable("id") final Long id) {
        logger.info("calling method : deleteFirestation / id = " + id);
        try
        {
            firestationService.deleteFirestation(id);
        }
        catch(EmptyResultDataAccessException e)
        {
            return badResponse(new Firestation(), HttpStatus.NOT_FOUND, e, "error deleting firestation", logger);
        }

        return goodResponse(new Firestation(), HttpStatus.NO_CONTENT, logger);
    }


    @PutMapping("/firestation/{id}")
	public ResponseEntity<Firestation> updateFirestation(@PathVariable("id") final Long id, @RequestBody Firestation firestation) {
        logger.info("calling method updateFirestation / id = " + id + " / body : " + toJson(firestation));
		Optional<Firestation> f = firestationService.getFirestation(id);
		if(f.isPresent()) {
			Firestation currentFirestation = f.get();

			String adress = firestation.getAddress();
			if(adress != null) {
				currentFirestation.setAddress(adress);
			}
			int station;
			station = firestation.getStation();
			if(station != 0) {
				currentFirestation.setStation(station);
			}

			firestationService.saveFirestation(currentFirestation);
            return goodResponse(currentFirestation, HttpStatus.OK, logger);
		}
		else if (firestation.getAddress() != null && firestation.getStation() != 0)
		    return createFirestation(firestation);

        return badResponse(firestation, HttpStatus.BAD_REQUEST, new IllegalArgumentException(), "Not valid firestation", logger);
	}

    @GetMapping("/firestation")
    public ResponseEntity<PeopleCoveredByThisFirestationDto> getPeopleByFirestation(@RequestParam("stationNumber") final int stationNumber) {
        logger.info("calling method : getPersonByFirestation / stationNumber = " + stationNumber);
        try {
            ResponseEntity<PeopleCoveredByThisFirestationDto> result = new ResponseEntity<>(firestationService.getPeopleByFirestation(stationNumber),
                    HttpStatus.OK);
            logger.info("HTTP response : " + result.getStatusCode());
            return result;
        }
        catch(IllegalArgumentException e)
        {
            logger.error("error in method getPeopleByFirestation : " + e);
        }
        ResponseEntity<PeopleCoveredByThisFirestationDto> result = new ResponseEntity<>(new PeopleCoveredByThisFirestationDto(), HttpStatus.BAD_REQUEST);
        logger.error("HTTP response : " + result.getStatusCode());
        return result;
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<PersonPhoneDto>> getPhoneAlert(@RequestParam("firestation") final int firestationNumber) {
        logger.info("calling method : phoneAlert / firestationNumber = " + firestationNumber);
        ResponseEntity<List<PersonPhoneDto>> result = new ResponseEntity<>(firestationService.getPhoneAlert(firestationNumber), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }
}

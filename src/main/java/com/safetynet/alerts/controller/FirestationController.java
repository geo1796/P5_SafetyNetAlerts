package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PersonPhoneDto;
import com.safetynet.alerts.jsonParsing.Json;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.dto.PersonsCoveredByThisFirestationDto;
import com.safetynet.alerts.service.FirestationService;
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

@AllArgsConstructor
@RestController
public class FirestationController {

    private static final Logger logger = LogManager.getLogger("FirestationController");
    private final FirestationService firestationService;

    @PostMapping("/firestation")
    public ResponseEntity<Firestation> createFirestation(@RequestBody Firestation firestation) {
        logger.info("calling method : createFirestation / body : " + Json.toJson(firestation));
        try {
            return new ResponseEntity<>(firestationService.saveFirestation(firestation), HttpStatus.CREATED);
        }
        catch(DataIntegrityViolationException e)
        {
            logger.error("error creating firestation : " + e);
            return new ResponseEntity<>(new Firestation(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/firestations")
    public Iterable<Firestation> getFirestations() {
        logger.info("calling method : getFirestations");
        return firestationService.getFirestations();
    }

    @GetMapping("/firestation/{id}")
    public ResponseEntity<Firestation> getFirestation(@PathVariable("id") final Long id) {
        logger.info("calling method : getFirestation / id = " + id);
        Optional<Firestation> firestation = firestationService.getFirestation(id);
        return firestation.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(firestation.orElse(null), HttpStatus.NOT_FOUND));
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
            logger.error("error deleting firestation : " + e);
            return new ResponseEntity<>(new Firestation(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new Firestation(), HttpStatus.NO_CONTENT);

    }


    @PutMapping("/firestation/{id}")
	public ResponseEntity<Firestation> updateFirestation(@PathVariable("id") final Long id, @RequestBody Firestation firestation) {
        logger.info("calling method updateFirestation / id = " + id + " / body : " + Json.toJson(firestation));
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
			return new ResponseEntity<>(currentFirestation, HttpStatus.OK);
		}
		else if (firestation.getAddress() != null && firestation.getStation() != 0)
		    return createFirestation(firestation);

		return new ResponseEntity<>(firestation, HttpStatus.BAD_REQUEST);

	}

    @GetMapping("/firestation")
    public PersonsCoveredByThisFirestationDto getPersonByFirestation(@RequestParam("stationNumber") final int stationNumber) {
        logger.info("calling method : getPersonByFirestation / stationNumber = " + stationNumber);
        return firestationService.getPersonByFirestation(stationNumber);
    }

    @GetMapping("/phoneAlert")
    public List<PersonPhoneDto> getPhoneAlert(@RequestParam("firestation") final int firestationNumber) {
        logger.info("calling method : phoneAlert / firestationNumber = " + firestationNumber);
        return firestationService.getPhoneAlert(firestationNumber);
    }

}

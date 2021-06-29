package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.FirestationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
@RestController
public class FirestationController {

    //@Autowired
    private FirestationService firestationService;

    @PostMapping("/firestation")
    public ResponseEntity<Firestation> createFirestation(@RequestBody Firestation firestation) {
        return new ResponseEntity<>(firestationService.saveFirestation(firestation), HttpStatus.CREATED);
    }

    @GetMapping("/firestations")
    public Iterable<Firestation> getFirestations() {
        return firestationService.getFirestations();
    }

    @GetMapping("/firestation/{id}")
    public Firestation getFirestation(@PathVariable("id") final Long id) {
        Optional<Firestation> firestation = firestationService.getFirestation(id);
        return firestation.orElse(null);
    }

    @DeleteMapping("/firestation/{id}")
    public ResponseEntity<Firestation> deleteFirestation(@PathVariable("id") final Long id) {

        try
        {
            firestationService.deleteFirestation(id);
        }
        catch(EmptyResultDataAccessException e)
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

    }


    @PutMapping("/firestation/{id}")
	public Firestation updateFirestation(@PathVariable("id") final Long id, @RequestBody Firestation firestation) {
		Optional<Firestation> f = firestationService.getFirestation(id);
		if(f.isPresent()) {
			Firestation currentFirestation = f.get();

			String adress = firestation.getAdress();
			if(adress != null) {
				currentFirestation.setAdress(adress);
			}
			int station = 0;
			station = firestation.getStation();
			if(station != 0) {
				currentFirestation.setStation(station);;
			}

			firestationService.saveFirestation(currentFirestation);
			return currentFirestation;
		} else {
			return null;
		}
	}



}

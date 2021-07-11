package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PhoneAlertDto;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.PersonListByFirestation;
import com.safetynet.alerts.service.FirestationService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
public class FirestationController {

    //@Autowired
    private FirestationService firestationService;

    @PostMapping("/firestation")
    public ResponseEntity<Firestation> createFirestation(@RequestBody Firestation firestation) {
        try {
            return new ResponseEntity<>(firestationService.saveFirestation(firestation), HttpStatus.CREATED);
        }
        catch(DataIntegrityViolationException e)
        {
            return new ResponseEntity<>(new Firestation(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/firestations")
    public Iterable<Firestation> getFirestations() {
        return firestationService.getFirestations();
    }

    @GetMapping("/firestation/{id}")
    public ResponseEntity<Firestation> getFirestation(@PathVariable("id") final Long id) {
        Optional<Firestation> firestation = firestationService.getFirestation(id);
        return firestation.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(firestation.orElse(null), HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/firestation/{id}")
    public ResponseEntity<Firestation> deleteFirestation(@PathVariable("id") final Long id) {

        try
        {
            firestationService.deleteFirestation(id);
        }
        catch(EmptyResultDataAccessException e)
        {
            return new ResponseEntity<>(new Firestation(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new Firestation(), HttpStatus.NO_CONTENT);

    }


    @PutMapping("/firestation/{id}")
	public ResponseEntity<Firestation> updateFirestation(@PathVariable("id") final Long id, @RequestBody Firestation firestation) {
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
    public PersonListByFirestation getPersonByFirestation(@RequestParam("stationNumber") final int stationNumber) {
        return firestationService.getPersonByFirestation(stationNumber);
    }

    @GetMapping("/phoneAlert")
    public List<PhoneAlertDto> getPhoneAlert(@RequestParam("firestation") final int firestationNumber) {
        return firestationService.getPhoneAlert(firestationNumber);
    }

}

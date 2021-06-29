package com.safetynet.alerts.service;

import java.util.Optional;

import com.safetynet.alerts.model.Firestation;

import com.safetynet.alerts.repository.FirestationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.Data;


@Data
@AllArgsConstructor
@Service
public class FirestationService {

	//@Autowired
	private FirestationRepository firestationRepository;
	
	public Optional<Firestation> getFirestation(final Long id) {
		return firestationRepository.findById(id);
	}
	
	public Iterable<Firestation> getFirestations() {
		return firestationRepository.findAll();
	}
	
	public void deleteFirestation(final Long id) throws IllegalArgumentException {
		firestationRepository.deleteById(id);
	}
	
	public Firestation saveFirestation(Firestation firestation) {
		return firestationRepository.save(firestation);
	}

}

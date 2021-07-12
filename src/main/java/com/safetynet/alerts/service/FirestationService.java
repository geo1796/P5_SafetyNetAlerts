package com.safetynet.alerts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.dto.PersonPhoneDto;
import com.safetynet.alerts.mappers.PersonMapper;
import com.safetynet.alerts.mappers.PhoneAlertMapper;
import com.safetynet.alerts.model.Firestation;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.dto.PersonsCoveredByThisFirestationDto;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.StringDateHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import lombok.Data;


@Data
@AllArgsConstructor
@Service
public class FirestationService {

	private FirestationRepository firestationRepository;
	private PersonRepository personRepository;
	private MedicalRecordRepository medicalRecordRepository;
	private PersonMapper personMapper;
	private PhoneAlertMapper phoneAlertMapper;
	
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

	public List<Firestation> saveFirestations(List<Firestation> firestations) { return (List<Firestation>) firestationRepository.saveAll(firestations); }

	public PersonsCoveredByThisFirestationDto getPersonByFirestation(int stationNumber){
		List<Person> personList = new ArrayList<>();
		List<Firestation> firestations = firestationRepository.findAllByStation(stationNumber);
		StringDateHandler stringDateHandler = new StringDateHandler("MM/dd/yyyy");

		for(Firestation firestation : firestations){
			personList.addAll(personRepository.findAllByAddress(firestation.getAddress()));
		}

		ArrayList<PersonDto> personDtoList = new ArrayList<>();
		int numberOfAdults = 0;
		int numberOfChildren = 0;
		for(Person person : personList){

			if(stringDateHandler.isAdult(person, medicalRecordRepository))
				numberOfAdults++;
			else
				numberOfChildren++;
			PersonDto personDto = personMapper.toDto(person);
			personDtoList.add(personDto);
		}

		return new PersonsCoveredByThisFirestationDto(numberOfAdults, numberOfChildren, personDtoList);
	}

    public List<PersonPhoneDto> getPhoneAlert(int firestationNumber) {
		List<Firestation> firestationList = firestationRepository.findAllByStation(firestationNumber);
		List<PersonPhoneDto> personPhoneDtoList = new ArrayList<>();

		for(Firestation firestation : firestationList){
			for(Person person : personRepository.findAllByAddress(firestation.getAddress())) {
				PersonPhoneDto personPhoneDto = phoneAlertMapper.toDto(person);
				if(!personPhoneDtoList.contains(personPhoneDto))
					personPhoneDtoList.add(personPhoneDto);
			}
		}

		return personPhoneDtoList;
    }
}

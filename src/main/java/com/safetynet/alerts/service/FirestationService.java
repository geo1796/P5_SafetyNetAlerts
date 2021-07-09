package com.safetynet.alerts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.mappers.PersonMapper;
import com.safetynet.alerts.model.Firestation;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.PersonListByFirestation;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import lombok.Data;


@Data
@AllArgsConstructor
@Service
public class FirestationService {

	private FirestationRepository firestationRepository;
	private PersonRepository personRepository;
	private PersonService personService;
	private PersonMapper personMapper;
	
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

	public PersonListByFirestation getPersonByFirestation(int stationNumber){
		List<Person> personList = new ArrayList<>();
		List<Firestation> firestations = firestationRepository.findAllByStation(stationNumber);

		for(Firestation firestation : firestations){
			personList.addAll(personRepository.findAllByAddress(firestation.getAddress()));
		}

		ArrayList<PersonDto> personDtoList = new ArrayList<>();
		int numberOfAdults = 0;
		int numberOfChildren = 0;
		for(Person person : personList){

			if(personService.isAdult(person))
				numberOfAdults++;
			else
				numberOfChildren++;
			PersonDto personDto = personMapper.toDto(person);
			personDtoList.add(personDto);
		}

		return new PersonListByFirestation(numberOfAdults, numberOfChildren, personDtoList);
	}

}

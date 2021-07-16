package com.safetynet.alerts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.safetynet.alerts.dto.PersonForFirestationDto;
import com.safetynet.alerts.dto.PersonPhoneDto;
import com.safetynet.alerts.mapper.PersonForFirestationMapper;
import com.safetynet.alerts.mapper.PhoneAlertMapper;
import com.safetynet.alerts.model.Firestation;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.dto.PersonsCoveredByThisFirestationDto;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.StringDateHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class FirestationService {

	private final FirestationRepository firestationRepository;
	private final PersonRepository personRepository;
	private final MedicalRecordRepository medicalRecordRepository;
	private final PersonForFirestationMapper personForFirestationMapper;
	private final PhoneAlertMapper phoneAlertMapper;
	
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

		ArrayList<PersonForFirestationDto> personForFirestationDtoList = new ArrayList<>();
		int numberOfAdults = 0;
		int numberOfChildren = 0;
		for(Person person : personList){

			try {
				if (stringDateHandler.isAdult(person, medicalRecordRepository))
					numberOfAdults++;
				else
					numberOfChildren++;
			}
			catch(IllegalArgumentException e)
			{

			}
			PersonForFirestationDto personForFirestationDto = personForFirestationMapper.toDto(person);
			personForFirestationDtoList.add(personForFirestationDto);
		}

		return new PersonsCoveredByThisFirestationDto(numberOfAdults, numberOfChildren, personForFirestationDtoList);
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

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
import com.safetynet.alerts.dto.PeopleCoveredByThisFirestationDto;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.StringDateHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * the service corresponding to the entity Firestation
 */

@AllArgsConstructor
@Service
public class FirestationService {

	private final FirestationRepository firestationRepository;
	private final PersonRepository personRepository;
	private final MedicalRecordRepository medicalRecordRepository;
	private final PersonForFirestationMapper personForFirestationMapper;
	private final PhoneAlertMapper phoneAlertMapper;

	/**
	 *
	 * @param id the id of the firestation you want to get
	 * @return an Optional object containing the firestation for this id if there is one
	 */

	public Optional<Firestation> getFirestation(final Long id) {
		return firestationRepository.findById(id);
	}

	/**
	 *
	 * @return an Iterable (possibly empty) object containing all the firestation objects
	 */

	public Iterable<Firestation> getFirestations() {
		return firestationRepository.findAll();
	}

	/**
	 *
	 * @param id the id of the firestation to delete
	 * @throws IllegalArgumentException if there is no firestation for this id
	 */
	
	public void deleteFirestation(final Long id) throws IllegalArgumentException {
		firestationRepository.deleteById(id);
	}

	/**
	 *
	 * @param firestation the Firestation object you want to save
	 * @return the Firestation object that has been saved
	 */

	public Firestation saveFirestation(Firestation firestation) {
		return firestationRepository.save(firestation);
	}

	/**
	 *
	 * @param firestations a list of Firestations you want to save
	 * @return the list of Firestations that has been saved
	 */

	public List<Firestation> saveFirestations(List<Firestation> firestations) { return (List<Firestation>) firestationRepository.saveAll(firestations); }

	/**
	 *
	 * @param stationNumber the number of the station
	 * @return a PeopleCoveredByThisFirestationDto object corresponding to this number of station
	 */

	public PeopleCoveredByThisFirestationDto getPeopleByFirestation(int stationNumber){
		List<Person> personList = new ArrayList<>();
		List<Firestation> firestations = firestationRepository.findAllByStation(stationNumber);
		StringDateHandler stringDateHandler = new StringDateHandler();

		for(Firestation firestation : firestations){
			personList.addAll(personRepository.findAllByAddress(firestation.getAddress())); //getting the list of all the people covered by the stations
		}																					//corresponding to the stationNumber parameter

		ArrayList<PersonForFirestationDto> personForFirestationDtoList = new ArrayList<>();
		int numberOfAdults = 0;
		int numberOfChildren = 0;
		for(Person person : personList){
			if (stringDateHandler.isAdult(person, medicalRecordRepository)) //checking if this person is an adult or a child
				numberOfAdults++;
			else
				numberOfChildren++;

			PersonForFirestationDto personForFirestationDto = personForFirestationMapper.toDto(person);
			personForFirestationDtoList.add(personForFirestationDto);
		}

		return new PeopleCoveredByThisFirestationDto(numberOfAdults, numberOfChildren, personForFirestationDtoList);
	}

	/**
	 *
	 * @param firestationNumber the number of the station
	 * @return a List (possibly empty) of PersonPhoneDto objects corresponding to this station number
	 */

    public List<PersonPhoneDto> getPhoneAlert(int firestationNumber) {
		List<Firestation> firestationList = firestationRepository.findAllByStation(firestationNumber);
		List<PersonPhoneDto> personPhoneDtoList = new ArrayList<>();

		for(Firestation firestation : firestationList){
			for(Person person : personRepository.findAllByAddress(firestation.getAddress())) {
				PersonPhoneDto personPhoneDto = phoneAlertMapper.toDto(person);
				if(!personPhoneDtoList.contains(personPhoneDto)) //No need to return the same phone number more than once
					personPhoneDtoList.add(personPhoneDto);
			}
		}

		return personPhoneDtoList;
    }
}

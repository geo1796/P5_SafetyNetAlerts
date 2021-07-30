package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.*;
import com.safetynet.alerts.mapper.*;
import com.safetynet.alerts.mapper.PersonForPersonInfoMapper;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FirestationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.PersonAndMedicalRecordConverter;
import com.safetynet.alerts.util.StringDateHandler;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * the service corresponding to the entity Person
 */

@AllArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final FirestationRepository firestationRepository;
    private final PersonEmailMapper personEmailMapper;
    private final ChildMapper childMapper;
    private final HomeMemberMapper homeMemberMapper;
    private final PersonForPersonInfoMapper personForPersonInfoMapper;
    private final PersonForFloodAndFireMapper personForFloodAndFireMapper;

    /**
     *
     * @param person the Person object you want to get the DTO for
     * @return the PersonForFloodAndFireDto object corresponding to the person param
     */

     PersonForFloodAndFireDto getDtoForFloodAndFire(Person person){
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();
        MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);
        PersonForFloodAndFireDto personForFloodAndFireDto = personForFloodAndFireMapper.toDto(person, medicalRecord);
        personForFloodAndFireDto.setAge(personAndMedicalRecordConverter.getAgeFromMedicalRecord(medicalRecord)); //must convert the birthdate to age
        return personForFloodAndFireDto;
    }

    /**
     *
     * @param person the Person object you want to get the DTO for
     * @return the PersonForPersonInfoDto object corresponding to the person param
     */

    PersonForPersonInfoDto getDtoForPersonInfo(Person person){
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();
        MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);
        PersonForPersonInfoDto personForPersonInfoDto = this.personForPersonInfoMapper.toDto(person, medicalRecord);
        personForPersonInfoDto.setAge(personAndMedicalRecordConverter.getAgeFromMedicalRecord(medicalRecord)); //must convert the birthdate to age

        return personForPersonInfoDto;
    }

    /**
     *
     * @param person the Person object you want to get the DTO for
     * @return the getChildDto object corresponding to the person param
     */

    ChildDto getChildDto(Person person) {
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();
        StringDateHandler stringDateHandler = new StringDateHandler();
        MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);
        ChildDto childDto = null;
        if (medicalRecord != null) {
            int age = stringDateHandler.getAgeFromStringDate(medicalRecord.getBirthdate());
            if (age < 18) { // if this person is an adult then we don't want to get a dto from it
                childDto = childMapper.toDto(person);
                childDto.setAge(age);
            }
        }
        return childDto;
    }

    /**
     *
     * @param person the Person object you want to save
     * @return the Person object that has been saved
     */

    public Person savePerson(Person person) { return personRepository.save(person); }

    /**
     *
     * @param persons a List of Person objects that you want to save
     * @return the List of Person objects that has been saved
     */

    public List<Person> savePeople(List<Person> persons) { return (List<Person>) personRepository.saveAll(persons); }

    /**
     *
     * @param id the id of the Person object you want to get
     * @return an Optional of the Person object corresponding to this id if there is one
     */

    public Optional<Person> getPerson(final long id) { return personRepository.findById(id); }

    public Iterable<Person> getPeople(final String lastName, final String firstName){
        List<Person> personListByFirstName = personRepository.findAllByFirstName(firstName);
        List<Person> personListByLastName = personRepository.findAllByLastName(lastName);

        ArrayList<Person> result = new ArrayList<>();
        for(Person person : personListByLastName){
            if(personListByFirstName.contains(person))
                result.add(person);
        }
        return result;
    }

    /**
     *
     * @return and Iterable (possibly empty) of all people
     */
    public Iterable<Person> getPeople() {
        return personRepository.findAll();
    }

    /**
     *
     * @param lastName the last name of the person you want to delete
     * @param firstName the first name of the person you want to delete
     * @return the person that has been deleted
     * @throws IllegalArgumentException if there are many people for these params
     */

    public Person deletePerson(final String lastName, final String firstName) throws IllegalArgumentException {
        Person personToDelete = null;
        Iterable<Person> personIterable = getPeople(lastName, firstName);

        for(Person person : personIterable){
            if(personToDelete == null)
                personToDelete = person;
            else
                throw new IllegalArgumentException("plusieurs personnes portent ce nom"); //we don't want to delete the wrong person by mistake
        }

        if (personToDelete != null) { //checking if there is something to delete
            personRepository.deleteById(personToDelete.getId());
            return personToDelete;
        }
        else
            throw new EmptyResultDataAccessException(0);
    }

    /**
     *
     * @param city the city corresponding to the email list you want to get
     * @return an Iterable (possibly empty) of the emails of the people of this city
     */

    public Iterable<PersonEmailDto> getCommunityEmail(String city) {
        List<Person> personList = personRepository.findAllByCity(city);
        ArrayList<PersonEmailDto> listOfEmails = new ArrayList<>();
        for(Person person : personList){
            PersonEmailDto email = personEmailMapper.toDto(person);
            listOfEmails.add(email);
        }
        return listOfEmails;
    }

    /**
     *
     * @param address the address from which you want to get the children infos
     * @return an Iterable (possibly empty) of ChildDto objects
     */

    public Iterable<ChildDto> getChildAlert(String address) {
        List<Person> personList = personRepository.findAllByAddress(address);
        List<ChildDto> result = new ArrayList<>();

        for (Person person : personList) {
            ChildDto childDto = getChildDto(person);
            if (childDto != null) {
                List<HomeMemberDto> homeMembers = new ArrayList<>(); // if this person is a child, we want to know the people he/she lives with
                for (Person homeMember : personList) {                  // and add it to the DTO
                    if (homeMember != person)
                        homeMembers.add(homeMemberMapper.toDto(homeMember));
                }
                childDto.setHomeMemberDtoList(homeMembers);
                result.add(childDto);
            }
        }
        return result;
    }

    /**
     *
     * @param lastName the last name of the Person you want to get
     * @param firstName the first name of the Person you want to get
     * @return an Iterable (possibly empty) corresponding to these params
     */

    public Iterable<PersonForPersonInfoDto> getPersonInfo(String lastName, String firstName) {
        Iterable<Person> personIterable = getPeople(lastName, firstName);
        ArrayList<PersonForPersonInfoDto> result = new ArrayList<>();

        for(Person person : personIterable) {
            result.add(getDtoForPersonInfo(person));
        }
        return result;
    }

    /**
     *
     * @param stations the numbers of the stations you want to get the infos for
     * @return an Iterable (possibly empty) of FloodDto objects corresponding to the param stations
     */

    public Iterable<FloodDto> getFlood(int[] stations) {
        ArrayList<Firestation> firestationArrayList = new ArrayList<>();

        for(int station : stations)
            firestationArrayList.addAll(firestationRepository.findAllByStation(station));

        ArrayList<FloodDto> result = new ArrayList<>();

        for (Firestation firestation : firestationArrayList){ //getting a DTO for each person covered by the stations in this List
            ArrayList<PersonForFloodAndFireDto> personForFloodAndFireDtoArrayList = new ArrayList<>();
            String address = firestation.getAddress();
            for(Person person : personRepository.findAllByAddress(address)) {
                personForFloodAndFireDtoArrayList.add(getDtoForFloodAndFire(person));
            }
            FloodDto floodDto = new FloodDto(); //creating a DTO for this firestation containing the address and all the people it covers
            floodDto.setAddress(address);
            floodDto.setPersonForFloodAndFireDtoList(personForFloodAndFireDtoArrayList);
            result.add(floodDto);
        }

        return result;
    }

    /**
     *
     * @param address the address you want to get infos from
     * @return an Iterable (possibly empty) of FireAddressDto objects corresponding to this address
     */

    public Iterable<FireAddressDto> getFire(String address) {

        ArrayList<FireAddressDto> result = new ArrayList<>();

        for(Firestation firestation : firestationRepository.findAllByAddress(address)){
            List<Person> personList = personRepository.findAllByAddress(address);
            ArrayList<PersonForFloodAndFireDto> personForFloodAndFireDtoList = new ArrayList<>();
            for(Person person : personList){
                personForFloodAndFireDtoList.add(getDtoForFloodAndFire(person)); //getting a DTO for each people living at this address
            }

            FireAddressDto fireAddressDto = new FireAddressDto(); //getting a DTO for each station at this address containing the number of the station
            fireAddressDto.setStationNumber(firestation.getStation());                                                // and the people it covers
            fireAddressDto.setPersonForFloodAndFireDtoList(personForFloodAndFireDtoList);

            result.add(fireAddressDto);
        }
        return result;
    }

}

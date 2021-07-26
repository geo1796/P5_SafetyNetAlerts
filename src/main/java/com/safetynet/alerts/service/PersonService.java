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

     PersonForFloodAndFireDto getDtoForFloodAndFire(Person person){
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();
        MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);
        PersonForFloodAndFireDto personForFloodAndFireDto = personForFloodAndFireMapper.toDto(person, medicalRecord);
        personForFloodAndFireDto.setAge(personAndMedicalRecordConverter.getAgeFromMedicalRecord(medicalRecord));

        return personForFloodAndFireDto;
    }

    PersonForPersonInfoDto getDtoForPersonInfo(Person person){
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();
        MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);
        PersonForPersonInfoDto personForPersonInfoDto = this.personForPersonInfoMapper.toDto(person, medicalRecord);
        personForPersonInfoDto.setAge(personAndMedicalRecordConverter.getAgeFromMedicalRecord(medicalRecord));

        return personForPersonInfoDto;
    }

    ChildDto getChildDto(Person person) {
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();
        StringDateHandler stringDateHandler = new StringDateHandler();
        MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);
        ChildDto childDto = null;
        if (medicalRecord != null) {
            int age = stringDateHandler.getAgeFromStringDate(medicalRecord.getBirthdate());
            if (age < 18) {
                childDto = childMapper.toDto(person);
                childDto.setAge(age);
            }
        }
        return childDto;
    }


    public Person savePerson(Person person) { return personRepository.save(person); }

    public List<Person> savePeople(List<Person> persons) { return (List<Person>) personRepository.saveAll(persons); }

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

    public Iterable<Person> getPeople() {
        return personRepository.findAll();
    }

    public Person deletePerson(final String lastName, final String firstName) throws IllegalArgumentException {
        Person personToDelete = null;
        Iterable<Person> personIterable = getPeople(lastName, firstName);

        for(Person person : personIterable){
            if(personToDelete == null)
                personToDelete = person;
            else
                throw new IllegalArgumentException("plusieurs personnes portent ce nom");
        }

        if (personToDelete != null) {
            personRepository.deleteById(personToDelete.getId());
            return personToDelete;
        }
        else
            throw new EmptyResultDataAccessException(0);
    }

    public Iterable<PersonEmailDto> getCommunityEmail(String city) {
        List<Person> personList = personRepository.findAllByCity(city);
        ArrayList<PersonEmailDto> listOfEmails = new ArrayList<>();
        for(Person person : personList){
            PersonEmailDto email = personEmailMapper.toDto(person);
            listOfEmails.add(email);
        }
        return listOfEmails;
    }


    public Iterable<ChildDto> getChildAlert(String address) {
        List<Person> personList = personRepository.findAllByAddress(address);
        List<ChildDto> result = new ArrayList<>();

        for (Person person : personList) {
            ChildDto childDto = getChildDto(person);
            if (childDto != null) {
                List<HomeMemberDto> homeMembers = new ArrayList<>(); // liste des autres habitants du foyer
                for (Person homeMember : personList) {
                    if (homeMember != person)
                        homeMembers.add(homeMemberMapper.toDto(homeMember));
                }
                childDto.setHomeMemberDtoList(homeMembers);
                result.add(childDto);
            }
        }
        return result;
    }


    public Iterable<PersonForPersonInfoDto> getPersonInfo(String lastName, String firstName) {
        Iterable<Person> personIterable = getPeople(lastName, firstName);
        ArrayList<PersonForPersonInfoDto> result = new ArrayList<>();

        for(Person person : personIterable) {
            result.add(getDtoForPersonInfo(person));
        }
        return result;
    }

    public Iterable<FloodDto> getFlood(int[] stations) {
        ArrayList<Firestation> firestationArrayList = new ArrayList<>();

        for(int station : stations)
            firestationArrayList.addAll(firestationRepository.findAllByStation(station));

        ArrayList<FloodDto> result = new ArrayList<>();

        for (Firestation firestation : firestationArrayList){
            ArrayList<PersonForFloodAndFireDto> personForFloodAndFireDtoArrayList = new ArrayList<>();
            String address = firestation.getAddress();
            for(Person person : personRepository.findAllByAddress(address)) {
                personForFloodAndFireDtoArrayList.add(getDtoForFloodAndFire(person));
            }
            FloodDto floodDto = new FloodDto();
            floodDto.setAddress(address);
            floodDto.setPersonForFloodAndFireDtoList(personForFloodAndFireDtoArrayList);
            result.add(floodDto);
        }

        return result;
    }

    public Iterable<FireAddressDto> getFire(String address) {

        ArrayList<FireAddressDto> result = new ArrayList<>();

        for(Firestation firestation : firestationRepository.findAllByAddress(address)){
            List<Person> personList = personRepository.findAllByAddress(address);
            ArrayList<PersonForFloodAndFireDto> personForFloodAndFireDtoList = new ArrayList<>();
            for(Person person : personList){
                personForFloodAndFireDtoList.add(getDtoForFloodAndFire(person));
            }

            FireAddressDto fireAddressDto = new FireAddressDto();
            fireAddressDto.setStationNumber(firestation.getStation());
            fireAddressDto.setPersonForFloodAndFireDtoList(personForFloodAndFireDtoList);

            result.add(fireAddressDto);
        }
        return result;
    }

}

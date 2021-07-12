package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.HomeMemberDto;
import com.safetynet.alerts.dto.PersonEmailDto;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.mappers.*;
import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.PersonAndMedicalRecordConverter;
import com.safetynet.alerts.util.StringDateHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@Service
public class PersonService {

    private PersonRepository personRepository;
    private MedicalRecordRepository medicalRecordRepository;
    private PersonEmailMapper personEmailMapper;
    private PersonMapper personMapper;
    private ChildMapper childMapper;
    private HomeMemberMapper homeMemberMapper;
    private PersonInfoMapper personInfoMapper;

    public Person savePerson(Person person) { return personRepository.save(person); }

    public List<Person> savePersons(List<Person> persons) { return (List<Person>) personRepository.saveAll(persons); }

    public Optional<Person> getPerson(final long id) { return personRepository.findById(id); }

    public Iterable<Person> getPersons(final String lastName, final String firstName){
        List<Person> personListByFirstName = personRepository.findAllByFirstName(firstName);
        List<Person> personListByLastName = personRepository.findAllByLastName(lastName);

        ArrayList<Person> result = new ArrayList<>();
        for(Person person : personListByLastName){
            if(personListByFirstName.contains(person))
                result.add(person);
        }
        return result;
    }

    public Iterable<Person> getPersons() {
        return personRepository.findAll();
    }

    public Person deletePerson(final String lastName, final String firstName) throws IllegalArgumentException {
        Person personToDelete = null;
        Iterable<Person> personIterable = getPersons(lastName, firstName);

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


    public Iterable<ChildAlertDto> getChildAlert(String address) {
        List<Person> personList = personRepository.findAllByAddress(address);
        List<ChildAlertDto> result = new ArrayList<>();
        StringDateHandler stringDateHandler = new StringDateHandler("MM/dd/yyyy");
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();

        for (Person person : personList) {
            ChildAlertDto childAlertDto = new ChildAlertDto();
            MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);
            if (medicalRecord != null) {
                int age = stringDateHandler.getAgeFromStringDate(medicalRecord.getBirthdate());
                if (age < 18) {
                    childAlertDto.fromChildDto(childMapper.toDto(person));
                    childAlertDto.setAge(age);
                    List<HomeMemberDto> homeMembers = new ArrayList<>(); // liste des autres habitants du foyer
                    for (Person homeMember : personList) {
                        if (homeMember != person)
                            homeMembers.add(homeMemberMapper.toDto(homeMember));
                    }
                    childAlertDto.setHomeMemberDtoList(homeMembers);
                    result.add(childAlertDto);
                }
            }
        }
        return result;
    }


    public Iterable<PersonInfoDto> getPersonInfo(String lastName, String firstName) {
        Iterable<Person> personIterable = getPersons(lastName, firstName);
        ArrayList<PersonInfoDto> result = new ArrayList<>();

        for(Person person : personIterable)
            result.add(personInfoMapper.toDto(person, new PersonAndMedicalRecordConverter().findMedicalRecordFromPerson(person, medicalRecordRepository)));

        return result;
    }
}

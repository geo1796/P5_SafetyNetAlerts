package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.HomeMemberDto;
import com.safetynet.alerts.dto.PersonEmailDto;
import com.safetynet.alerts.mappers.ChildMapper;
import com.safetynet.alerts.mappers.HomeMemberMapper;
import com.safetynet.alerts.mappers.PersonMapper;
import com.safetynet.alerts.mappers.PersonEmailMapper;
import com.safetynet.alerts.model.ChildAlert;
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

    public Person savePerson(Person person) { return personRepository.save(person); }

    public List<Person> savePersons(List<Person> persons) { return (List<Person>) personRepository.saveAll(persons); }

    public Optional<Person> getPerson(final long id) { return personRepository.findById(id); }

    public Iterable<Person> getPersons() {
        return personRepository.findAll();
    }

    public void deletePerson(final String lastName, final String firstName) throws IllegalArgumentException {
        List<Person> personListByLastName = personRepository.findAllByLastName(lastName);
        List<Person> personListByFirstName = personRepository.findAllByFirstName(firstName);
        Person personToDelete = null;

        for(Person personByLastName : personListByLastName){
            if(personListByFirstName.contains(personByLastName))
                personToDelete = personByLastName;
        }
        if(personToDelete == null)
            throw new EmptyResultDataAccessException(0);
        else
            personRepository.deleteById(personToDelete.getId());
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


    public Iterable<ChildAlert> getChildAlert(String address) {
        List<Person> personList = personRepository.findAllByAddress(address);
        List<ChildAlert> result = new ArrayList<>();
        StringDateHandler stringDateHandler = new StringDateHandler("MM/dd/yyyy");
        PersonAndMedicalRecordConverter personAndMedicalRecordConverter = new PersonAndMedicalRecordConverter();

        for (Person person : personList) {
            ChildAlert childAlert = new ChildAlert();
            MedicalRecord medicalRecord = personAndMedicalRecordConverter.findMedicalRecordFromPerson(person, medicalRecordRepository);
            if (medicalRecord != null) {
                int age = stringDateHandler.getAgeFromStringDate(medicalRecord.getBirthdate());
                if (age < 18) {
                    childAlert.fromChildDto(childMapper.toDto(person));
                    childAlert.setAge(age);
                    List<HomeMemberDto> homeMembers = new ArrayList<>(); // liste des autres habitants du foyer
                    for (Person homeMember : personList) {
                        if (homeMember != person)
                            homeMembers.add(homeMemberMapper.toDto(homeMember));
                    }
                    childAlert.setHomeMemberDtoList(homeMembers);
                    result.add(childAlert);
                }
            }
        }
        return result;
    }

}

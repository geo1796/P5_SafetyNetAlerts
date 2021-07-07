package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonEmailDto;
import com.safetynet.alerts.mappers.PersonEmailMapper;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@Service
public class PersonService {

    private PersonRepository personRepository;

    private PersonEmailMapper personEmailMapper;

    public Person savePerson(Person person) { return personRepository.save(person); }

    public List<Person> savePersons(List<Person> persons) { return (List<Person>) personRepository.saveAll(persons); }

    public Optional<Person> getPerson(final long id) { return personRepository.findById(id); }

    public Iterable<Person> getPersons() {
        return personRepository.findAll();
    }

    public void deletePerson(final Long id) throws IllegalArgumentException {
        personRepository.deleteById(id);
    }

    public Iterable<PersonEmailDto> getEmails(String city) {
        List<Person> personList = personRepository.findAllByCity(city);
        ArrayList<PersonEmailDto> listofEmails = new ArrayList<PersonEmailDto>();
        for(Person person : personList){
            PersonEmailDto email = personEmailMapper.toDto(person);
            listofEmails.add(email);
        }
        return listofEmails;
    }
}

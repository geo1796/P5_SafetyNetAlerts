package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonEmailDto;
import com.safetynet.alerts.mappers.PersonMapper;
import com.safetynet.alerts.mappers.PersonEmailMapper;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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

    public Iterable<PersonEmailDto> getEmails(String city) {
        List<Person> personList = personRepository.findAllByCity(city);
        ArrayList<PersonEmailDto> listOfEmails = new ArrayList<>();
        for(Person person : personList){
            PersonEmailDto email = personEmailMapper.toDto(person);
            listOfEmails.add(email);
        }
        return listOfEmails;
    }

    public Boolean isAdult(Person person){
        final long personId = person.getId();
        Optional<MedicalRecord> optionalMedicalRecord = medicalRecordRepository.findById(personId);

        if(optionalMedicalRecord.isPresent()) {
            MedicalRecord medicalRecord = optionalMedicalRecord.get();
            LocalDate birthDate = convertBirthDateToLocalDate(medicalRecord.getBirthdate());
            LocalDate now = LocalDate.now();
            Period period = Period.between(birthDate, now);
            return period.getYears() >= 18;
        }
            else
                throw new IllegalArgumentException("Impossible de déterminer l'âge de la personne ID = " + personId);
    }

    public LocalDate convertBirthDateToLocalDate(String birthDate){
        return LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
}

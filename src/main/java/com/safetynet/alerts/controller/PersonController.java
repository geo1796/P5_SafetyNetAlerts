package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
@RestController
public class PersonController {

    PersonService personService;

    @PostMapping("/person")
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        try {
            return new ResponseEntity<>(personService.savePerson(person), HttpStatus.CREATED);
        }
        catch(DataIntegrityViolationException e)
        {
            return new ResponseEntity<>(new Person(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/person/{id}")
    public Person updatePerson(@PathVariable("id") final Long id, @RequestBody Person person) {
        Optional<Person> p = personService.getPerson(id);
        if(p.isPresent()) {
            Person currentPerson = p.get();

            String address = person.getAddress();
            if(address != null) {
                currentPerson.setAddress(address);
            }

            String city = person.getCity();
            if(city != null) {
                currentPerson.setCity(city);
            }

            int zip = person.getZip();
            if(zip != 0) {
                currentPerson.setZip(zip);
            }

            String phone = person.getPhone();
            if(phone != null) {
                currentPerson.setPhone(phone);
            }

            String email = person.getEmail();
            if(email != null) {
                currentPerson.setEmail(email);
            }

            personService.savePerson(currentPerson);
            return currentPerson;
        } else {
            return null;
        }
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable("id") final Long id) {
        Optional<Person> person = personService.getPerson(id);
        if(person.isPresent())
            return new ResponseEntity<>(person.get(), HttpStatus.OK);
        return new ResponseEntity<>(person.orElse(null), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/persons")
    public Iterable<Person> getPersons() {
        return personService.getPersons();
    }

    @DeleteMapping("/person/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable("id") final Long id) {

        try
        {
            personService.deletePerson(id);
        }
        catch(EmptyResultDataAccessException e)
        {
            return new ResponseEntity<>(new Person(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new Person(), HttpStatus.NO_CONTENT);

    }

}

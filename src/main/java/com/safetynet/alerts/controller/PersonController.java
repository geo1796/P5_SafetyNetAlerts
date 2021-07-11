package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PersonEmailDto;
import com.safetynet.alerts.model.ChildAlert;
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
            if(person.getZip() == 0)
                throw new DataIntegrityViolationException("zip code can't be null");
            return new ResponseEntity<>(personService.savePerson(person), HttpStatus.CREATED);
        }
        catch(DataIntegrityViolationException e)
        {
            return new ResponseEntity<>(new Person(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/person/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable("id") final Long id, @RequestBody Person person) {
        Optional<Person> p = personService.getPerson(id);
        if(p.isPresent()) {
            Person currentPerson = p.get();

            String personFirstName = person.getFirstName();
            String personLastName = person.getLastName();

            if( ( personFirstName != null && !personFirstName.equals(currentPerson.getFirstName())) || ( personLastName != null && !personLastName.equals(currentPerson.getLastName())))
                return new ResponseEntity<>(new Person(), HttpStatus.BAD_REQUEST);

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
            return new ResponseEntity<>(currentPerson, HttpStatus.OK);
        }
        else {
            return createPerson(person);
        }
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable("id") final Long id) {
        Optional<Person> person = personService.getPerson(id);
        return person.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(person.orElse(null), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/persons")
    public Iterable<Person> getPersons() {
        return personService.getPersons();
    }

    @DeleteMapping("/person/{lastName}/{firstName}")
    public ResponseEntity<Person> deletePerson(@PathVariable("lastName") final String lastName, @PathVariable("firstName") final String firstName) {

        try
        {
            personService.deletePerson(lastName, firstName);
        }
        catch(EmptyResultDataAccessException e)
        {
            return new ResponseEntity<>(new Person(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new Person(), HttpStatus.NO_CONTENT);

    }

    @GetMapping("/communityEmail")
    public Iterable<PersonEmailDto> getCommunityEmail(@RequestParam("city") final String city) { return personService.getCommunityEmail(city); }

    @GetMapping("/childAlert")
    public Iterable<ChildAlert> getChildAlert(@RequestParam("address") final String address) { return personService.getChildAlert(address); }
}

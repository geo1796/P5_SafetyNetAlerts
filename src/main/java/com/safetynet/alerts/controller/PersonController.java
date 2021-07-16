package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.*;
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

    @DeleteMapping("/person")
    public ResponseEntity<Person> deletePerson(@RequestParam("lastName") final String lastName, @RequestParam("firstName") final String firstName) {

        try
        {
            return new ResponseEntity<>(personService.deletePerson(lastName, firstName), HttpStatus.NO_CONTENT);
        }
        catch(EmptyResultDataAccessException e)
        {
            return new ResponseEntity<>(new Person(), HttpStatus.NOT_FOUND);
        }
        catch (IllegalArgumentException e)
        {
            return new ResponseEntity<>(new Person(), HttpStatus.MULTIPLE_CHOICES);
        }

    }

    @GetMapping("/communityEmail")
    public Iterable<PersonEmailDto> getCommunityEmailUrl(@RequestParam("city") final String city) { return personService.getCommunityEmailUrl(city); }

    @GetMapping("/childAlert")
    public Iterable<ChildDto> getChildAlertUrl(@RequestParam("address") final String address) { return personService.getChildAlertUrl(address); }

    @GetMapping("/personInfo")
    public Iterable<PersonForPersonInfoDto> getPersonInfoUrl(@RequestParam("firstName") final String firstName, @RequestParam("lastName") final String lastName){
        return personService.getPersonInfoUrl(lastName, firstName);
    }

    @GetMapping("/flood/stations")
    public Iterable<FloodDto> getFloodUrl(@RequestParam("station") final int[] stations) { return personService.getFloodUrl(stations); }

    @GetMapping("/fire")
    public Iterable<FireAddressDto> getFireUrl(@RequestParam("address") final String address) { return personService.getFireUrl(address); }
}

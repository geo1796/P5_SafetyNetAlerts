package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.*;
import com.safetynet.alerts.jsonParsing.Json;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

import static com.safetynet.alerts.util.ResponseEntityAndLoggerHandler.badResponse;
import static com.safetynet.alerts.util.ResponseEntityAndLoggerHandler.goodResponse;

@AllArgsConstructor
@RestController
public class PersonController {

    private static final Logger logger = LogManager.getLogger("PersonController");
    private final PersonService personService;

    @PostMapping("/person")
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        logger.info("calling method : createPerson / body : " + Json.toJson(person));
        try {
            if(person.getZip() == 0)
                throw new DataIntegrityViolationException("zip code can't be null");
            else if (person.getId() != null)
                throw new IllegalArgumentException("id is not null");
            return goodResponse(personService.savePerson(person), HttpStatus.CREATED, logger);
        }
        catch(DataIntegrityViolationException | IllegalArgumentException e)
        {
            return badResponse(new Person(), HttpStatus.BAD_REQUEST, e, "error creating new Person", logger);
        }
    }

    @PutMapping("/person/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable("id") final Long id, @RequestBody Person person) {
        logger.info("calling method : updatePerson / id = " + id + " / body : " + Json.toJson(person));
        Optional<Person> p = personService.getPerson(id);
        if(p.isPresent()) {
            Person currentPerson = p.get();

            String personFirstName = person.getFirstName();
            String personLastName = person.getLastName();

            if( ( personFirstName != null && !personFirstName.equals(currentPerson.getFirstName())) || ( personLastName != null && !personLastName.equals(currentPerson.getLastName())))
                return badResponse(new Person(), HttpStatus.BAD_REQUEST, new IllegalArgumentException(), "Not valid person", logger);

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
            return goodResponse(currentPerson, HttpStatus.OK, logger);
        }
        else {
            return createPerson(person);
        }
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable("id") final Long id) {
        logger.info("calling method : getPerson / id = " + id);
        Optional<Person> person = personService.getPerson(id);
        return person.map(value -> goodResponse(value, HttpStatus.OK, logger)).orElseGet(() ->
                badResponse(new Person(), HttpStatus.NOT_FOUND, new IllegalArgumentException(), "No Person for id = " + id, logger));
    }

    @GetMapping("/persons")
    public ResponseEntity<Iterable<Person>> getPersons() {
        logger.info("calling method : getPersons");
        ResponseEntity<Iterable<Person>> result = new ResponseEntity<>(personService.getPeople(), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    @DeleteMapping("/person")
    public ResponseEntity<Person> deletePerson(@RequestParam("lastName") final String lastName, @RequestParam("firstName") final String firstName) {
        logger.info("calling method : deletePerson / lastName = " + lastName + " / firstName = " + firstName);
        try
        {
            return goodResponse(personService.deletePerson(lastName, firstName), HttpStatus.NO_CONTENT, logger);
        }
        catch(EmptyResultDataAccessException e)
        {
            return badResponse(new Person(), HttpStatus.NOT_FOUND, e, "error deleting person", logger);
        }
        catch (IllegalArgumentException e)
        {
            return badResponse(new Person(), HttpStatus.MULTIPLE_CHOICES, e, "error deleting person", logger);
        }

    }

    @GetMapping("/communityEmail")
    public ResponseEntity<Iterable<PersonEmailDto>> getCommunityEmail(@RequestParam("city") final String city) {
        logger.info("calling method : getCommunityEmail / city = " + city);
        ResponseEntity<Iterable<PersonEmailDto>> result = new ResponseEntity<>(personService.getCommunityEmail(city), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    @GetMapping("/childAlert")
    public ResponseEntity<Iterable<ChildDto>> getChildAlert(@RequestParam("address") final String address) {
        logger.info("calling method : getChildAlert / address = " + address);
        ResponseEntity<Iterable<ChildDto>> result = new ResponseEntity<>(personService.getChildAlert(address), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    @GetMapping("/personInfo")
    public ResponseEntity<Iterable<PersonForPersonInfoDto>> getPersonInfo(@RequestParam("firstName") final String firstName, @RequestParam("lastName") final String lastName){
        logger.info("calling method : getPersonInfo / lastName = " + lastName + " / firstName = " + firstName);
        ResponseEntity<Iterable<PersonForPersonInfoDto>> result = new ResponseEntity<>(personService.getPersonInfo(lastName, firstName), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<Iterable<FloodDto>> getFlood(@RequestParam("station") final int[] stations) {
        logger.info("calling method : getFlood / stations = " + Arrays.toString(stations));
        ResponseEntity<Iterable<FloodDto>> result = new ResponseEntity<>(personService.getFlood(stations), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    @GetMapping("/fire")
    public ResponseEntity<Iterable<FireAddressDto>> getFire(@RequestParam("address") final String address) {
        logger.info("calling method : getFire / address = " + address);
        ResponseEntity<Iterable<FireAddressDto>> result = new ResponseEntity<>(personService.getFire(address), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }
}

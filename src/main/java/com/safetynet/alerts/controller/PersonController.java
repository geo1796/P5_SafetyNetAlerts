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

/**
 * the controller corresponding to the entity Person
 */

@AllArgsConstructor
@RestController
public class PersonController {

    private static final Logger logger = LogManager.getLogger("PersonController");
    private final PersonService personService;

    /**
     *
     * @param person the Person object you want to post
     * @return a ResponseEntity object containing the person you posted and http status 201 if the request was successfully handled,
     * if not the status will be 400
     */

    @PostMapping("/person")
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        logger.info("calling method : createPerson / body : " + Json.toJson(person));
        try {
            if(person.getZip() == 0 || person.getFirstName().equals("") || person.getLastName().equals("") ||
            person.getEmail().equals("") || person.getCity().equals("") )
                throw new DataIntegrityViolationException("invalid field(s)");
            else if (person.getId() != null)
                throw new IllegalArgumentException("id is not null");
            return goodResponse(personService.savePerson(person), HttpStatus.CREATED, logger);
        }
        catch(DataIntegrityViolationException | IllegalArgumentException e)
        {
            return badResponse(new Person(), HttpStatus.BAD_REQUEST, e, "error creating new Person", logger);
        }
    }

    /**
     *
     * @param id the id of the Person object you want to update
     * @param person the Person object carrying the infos you need for the update
     * @return a ResponseEntity object containing the updated Person object with https status 200 (or 201 if there was no object for this id) if the request was successfully handled,
     * if not the status will be 400
     */

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

    /**
     *
     * @param id the id of the Person object you want to get
     * @return a ResponseEntity object containing the Person object for this id with http status 200,
     * or http status 404 if nothing was found for this id
     */

    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable("id") final Long id) {
        logger.info("calling method : getPerson / id = " + id);
        Optional<Person> person = personService.getPerson(id);
        return person.map(value -> goodResponse(value, HttpStatus.OK, logger)).orElseGet(() ->
                badResponse(new Person(), HttpStatus.NOT_FOUND, new IllegalArgumentException(), "No Person for id = " + id, logger));
    }

    /**
     *
     * @return a ResponseEntity object containing an Iterable (possibly empty) of all the Person objects with http status 200
     */

    @GetMapping("/people")
    public ResponseEntity<Iterable<Person>> getPeople() {
        logger.info("calling method : getPeople");
        ResponseEntity<Iterable<Person>> result = new ResponseEntity<>(personService.getPeople(), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    /**
     *
     * @param lastName the last name of the Person object you want to delete
     * @param firstName the first name of the Person object you want to delete
     * @return a ResponseEntity object containing a new Person object with http status 204 if the request was successfully handled,
     * if nothing was found the status will be 404 and if there are many Person objects for these parameters the status will be 300
     */

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

    /**
     *
     * @param city the city corresponding to the emails you want to get
     * @return a ResponseEntity object containing an Iterable (possibly empty) of all the emails of the people in this city with http status 200
     */

    @GetMapping("/communityEmail")
    public ResponseEntity<Iterable<PersonEmailDto>> getCommunityEmail(@RequestParam("city") final String city) {
        logger.info("calling method : getCommunityEmail / city = " + city);
        ResponseEntity<Iterable<PersonEmailDto>> result = new ResponseEntity<>(personService.getCommunityEmail(city), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    /**
     *
     * @param address the address corresponding to the list of children that you want to get
     * @return a ResponseEntity object containing an Iterable (possibly empty) of the children who live at this address with http status 200
     */

    @GetMapping("/childAlert")
    public ResponseEntity<Iterable<ChildDto>> getChildAlert(@RequestParam("address") final String address) {
        logger.info("calling method : getChildAlert / address = " + address);
        ResponseEntity<Iterable<ChildDto>> result = new ResponseEntity<>(personService.getChildAlert(address), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    /**
     *
     * @param firstName the first name of the Person you want to get
     * @param lastName the last name of the Person you want to get
     * @return a ResponseEntity object containing an Iterable of PersonForPersonInfoDto (possibly empty) of all the Person with this first name and last name
     */

    @GetMapping("/personInfo")
    public ResponseEntity<Iterable<PersonForPersonInfoDto>> getPersonInfo(@RequestParam("firstName") final String firstName, @RequestParam("lastName") final String lastName){
        logger.info("calling method : getPersonInfo / lastName = " + lastName + " / firstName = " + firstName);
        ResponseEntity<Iterable<PersonForPersonInfoDto>> result = new ResponseEntity<>(personService.getPersonInfo(lastName, firstName), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    /**
     *
     * @param stations the stations that you want to get the people covered from
     * @return a ResponseEntity object containing an Iterable (possibly empty) of FloodDto objects with http status 200
     */

    @GetMapping("/flood/stations")
    public ResponseEntity<Iterable<FloodDto>> getFlood(@RequestParam("station") final int[] stations) {
        logger.info("calling method : getFlood / stations = " + Arrays.toString(stations));
        ResponseEntity<Iterable<FloodDto>> result = new ResponseEntity<>(personService.getFlood(stations), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }

    /**
     *
     * @param address the address of the stations you are interested in
     * @return a ResponseEntity object containing an Iterable (possibly empty) of FireAddressDto objects for this address with http status 200
     */

    @GetMapping("/fire")
    public ResponseEntity<Iterable<FireAddressDto>> getFire(@RequestParam("address") final String address) {
        logger.info("calling method : getFire / address = " + address);
        ResponseEntity<Iterable<FireAddressDto>> result = new ResponseEntity<>(personService.getFire(address), HttpStatus.OK);
        logger.info("HTTP response : " + result.getStatusCode());
        return result;
    }
}

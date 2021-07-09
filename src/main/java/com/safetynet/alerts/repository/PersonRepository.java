package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    List<Person> findAllByCity(String city);
    List<Person> findAllByAddress(String address);
    List<Person> findAllByLastName(String lastName);
    List<Person> findAllByFirstName(String firstName);
}

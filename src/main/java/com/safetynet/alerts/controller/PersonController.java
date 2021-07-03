package com.safetynet.alerts.controller;

import com.safetynet.alerts.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class PersonController {

    PersonService personService;

}

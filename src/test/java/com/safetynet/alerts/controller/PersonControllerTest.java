package com.safetynet.alerts.controller;

import static com.safetynet.alerts.jsonParsing.Json.stringify;
import static com.safetynet.alerts.jsonParsing.Json.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.controller.PersonController;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.FirestationService;
import com.safetynet.alerts.service.MedicalRecordService;
import com.safetynet.alerts.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FirestationService firestationService;

    @MockBean
    private PersonService personService;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @Test
    public void testGetPerson() throws Exception{
        mockMvc.perform(get("/person/1"));
    }

    @Test
    public void testGetPersons() throws Exception{
        mockMvc.perform(get("/persons"));
    }

    @Test
    public void testCreateNotValidPerson() throws Exception{
        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(new Person()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatePerson() throws Exception{
        Person p = new Person();
        p.setFirstName("firstName");
        p.setLastName("lastName");
        p.setCity("city");
        p.setAddress("address");
        p.setEmail("email");
        p.setPhone("phone");
        p.setZip(1);

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreatePersonWithId() throws Exception{
        Person p = new Person();
        p.setFirstName("firstName");
        p.setLastName("lastName");
        p.setCity("city");
        p.setAddress("address");
        p.setEmail("email");
        p.setPhone("phone");
        p.setZip(1);
        p.setId(1L);

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateNotExistingPerson() throws Exception{
        Person p = new Person();
        p.setFirstName("firstName");
        p.setLastName("lastName");
        p.setCity("city");
        p.setAddress("address");
        p.setEmail("email");
        p.setPhone("phone");
        p.setZip(1);

        mockMvc.perform(put("/person/1")
                .contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdatePersonWithoutInfos() throws Exception{
        Person p = new Person();
        when(personService.getPerson(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(put("/person/1")
                .contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePersonWithInfos() throws Exception{
        Person p = new Person();
        p.setFirstName("firstName");
        p.setLastName("lastName");
        p.setCity("city");
        p.setAddress("address");
        p.setEmail("email");
        p.setPhone("phone");
        p.setZip(1);

        when(personService.getPerson(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(put("/person/1")
                .contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletePerson() throws Exception{
        mockMvc.perform(delete("/person?lastName=Boyd&firstName=Jacob")).andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteNotExistingPerson() throws Exception{
        when(personService.deletePerson("Boyd", "Jacob")).thenThrow(EmptyResultDataAccessException.class);
        mockMvc.perform(delete("/person?lastName=Boyd&firstName=Jacob")).andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePersonsWithSameNames() throws Exception{
        when(personService.deletePerson("Boyd", "Jacob")).thenThrow(IllegalArgumentException.class);
        mockMvc.perform(delete("/person?lastName=Boyd&firstName=Jacob")).andExpect(status().isMultipleChoices());
    }

    @Test
    public void testGetCommunityEmail() throws Exception{
        mockMvc.perform(get("/communityEmail?city=Culver"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetChildAlert() throws Exception{
        mockMvc.perform(get("/childAlert?address=abcd")).andExpect(status().isOk());
    }

    @Test
    public void testGetPersonInfo() throws Exception{
        mockMvc.perform(get("/personInfo?firstName=John&lastName=Boyd")).andExpect(status().isOk());
    }

    @Test
    public void testGetFlood() throws Exception {
        mockMvc.perform(get("/flood/stations?station=1")).andExpect(status().isOk());
    }

    @Test
    public void testGetFireAddress() throws Exception {
        mockMvc.perform(get("/fire?address=112 Steppes Pl")).andExpect(status().isOk());
    }
}

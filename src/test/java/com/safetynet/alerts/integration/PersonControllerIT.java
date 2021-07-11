package com.safetynet.alerts.integration;


import static com.safetynet.alerts.jsonParsing.Json.stringify;
import static com.safetynet.alerts.jsonParsing.Json.toJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIT {

    @Autowired
    public MockMvc mockMvc;

    @Test
    public void testDeletePerson() throws Exception {
        mockMvc.perform(delete("/person/Stelzer/Kendrik")).andExpect(status().isNoContent());
        mockMvc.perform(get("/person/21")).andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNotExistingPerson() throws Exception {
        mockMvc.perform(delete("/person/Son/Goku")).andExpect(status().isNotFound());
    }

    @Test
    public void testCreatePerson() throws Exception {
        Person p = new Person();
        p.setFirstName("firstName");
        p.setLastName("lastName");
        p.setCity("city");
        p.setAddress("address");
        p.setEmail("email");
        p.setPhone("phone");
        p.setZip(1);
        mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateNotValidPerson() throws Exception {
        mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(new Person()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePerson() throws Exception {
        Person p = new Person();
        p.setAddress("address");
        mockMvc.perform(put("/person/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/person/1")).andExpect(jsonPath("$.address", is("address")))
        .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    public void testUpdateNotExistingPersonWithNotValidPerson() throws Exception{
        Person p = new Person();
        mockMvc.perform(put("/person/75").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateNotExistingPersonWithValidPerson() throws Exception {
        Person p = new Person();
        p.setFirstName("firstName");
        p.setLastName("lastName");
        p.setCity("city");
        p.setAddress("address");
        p.setEmail("email");
        p.setPhone("phone");
        p.setZip(1);
        mockMvc.perform(put("/person/75").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isCreated()).andDo(print());

    }

    @Test
    public void testUpdatePersonWithChangingName() throws Exception {
        Person p = new Person();
        p.setFirstName("firstName");

        mockMvc.perform(put("/person/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(p))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEmailsWithExistingCity() throws Exception {
        mockMvc.perform(get("/communityEmail?city=Culver")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(23)))
                .andExpect(jsonPath("$[0].email", is("jaboyd@email.com")));
    }

    @Test
    public void testGetEmailsWithNotExistingCity() throws Exception {
        mockMvc.perform(get("/communityEmail?city=randomCity")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Order(1)
    @Test
    public void testGetPersons() throws Exception {
        mockMvc.perform(get("/persons")).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(23)))
        .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    public void testGetPerson() throws Exception {
        mockMvc.perform(get("/person/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    public void testGetNotExistingPerson() throws Exception {
        mockMvc.perform(get("/person/75")).andExpect(status().isNotFound());
    }

    @Test
    public void testGetChildAlert() throws Exception{
        mockMvc.perform(get("/childAlert?address=1509 Culver St")).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetChildAlertWithNotExistingAddress() throws Exception{
        mockMvc.perform(get("/childAlert?address=namek")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}

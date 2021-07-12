package com.safetynet.alerts.integration;

import static com.safetynet.alerts.jsonParsing.Json.stringify;
import static com.safetynet.alerts.jsonParsing.Json.toJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.safetynet.alerts.model.MedicalRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Order(1)
    @Test
    public void testGetMedicalRecords() throws Exception {
        mockMvc.perform(get("/medicalRecords")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(23)));
    }

    @Test
    public void testGetMedicalRecord() throws Exception{
        mockMvc.perform(get("/medicalRecord/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    public void testGetNotExistingMedicalRecord() throws Exception {
        mockMvc.perform(get("/medicalRecord/75")).andExpect(status().isNotFound());
    }

    @Test
    public void testCreateMedicalRecord() throws Exception {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("01/01/2000");

        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
        .andExpect(status().isCreated());
    }

    @Test
    public void testCreateMedicalRecordWithoutName() throws Exception {
        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(new MedicalRecord()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateMedicalRecordWithoutBirthDate() throws Exception {
        MedicalRecord m = new MedicalRecord();
        m.setLastName("Doe");
        m.setFirstName("John");
        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(m))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateMedicalRecordWithNotValidBirthDate() throws Exception {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("abcdefg");

        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
        .andExpect(status().isBadRequest());
    }

    @Order(3)
    @Test
    public void testUpdateMedicalRecord() throws Exception {
        MedicalRecord m = new MedicalRecord();
        m.setBirthdate("04/29/1998");
        mockMvc.perform(put("/medicalRecord/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(m))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/medicalRecord/1")).andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.birthdate", is("04/29/1998")));
    }

    @Test
    public void testUpdateMedicalRecordWithNotValidBirthDate() throws Exception {
        MedicalRecord m = new MedicalRecord();
        m.setBirthdate("abcdefg");
        mockMvc.perform(put("/medicalRecord/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(m))))
                .andExpect(status().isBadRequest());
    }

    @Order(2)
    @Test
    public void testUpdateMedicalRecordWithChangingName() throws Exception {
        MedicalRecord m = new MedicalRecord();
        m.setFirstName("John");
        m.setLastName("Doe");
        mockMvc.perform(put("/medicalRecord/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(m))))
                .andExpect(status().isOk());
        mockMvc.perform(get("/medicalRecord/1")).andExpect(jsonPath("$.lastName", is("Boyd")));
    }

    @Test
    public void testUpdateNotExistingMedicalRecordWithValidMedicalRecord() throws Exception {
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("01/01/1900");

        mockMvc.perform(put("/medicalRecord/75").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateNotExistingMedicalRecordWithNotValidMedicalRecord() throws Exception {
        mockMvc.perform(put("/medicalRecord/75").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(new MedicalRecord()))))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testDeleteMedicalRecord() throws Exception {
        mockMvc.perform(delete("/medicalRecord/Boyd/Jacob")).andExpect(status().isNoContent());
        mockMvc.perform(get("/medicalRecord/2")).andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNotExistingMedicalRecord() throws Exception {
        mockMvc.perform(delete("/medicalRecord/Son/Goku")).andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePersonsWhichHaveSameNames() throws Exception{
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Boyd");
        medicalRecord.setBirthdate("01/01/2000");
        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/medicalRecord/Boyd/John")).andExpect(status().isMultipleChoices());
    }

}

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

import java.util.ArrayList;

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
        ArrayList<String> randomList = new ArrayList<>();
        medicalRecord.setFirstName("");
        medicalRecord.setLastName("");
        medicalRecord.setMedications(randomList);
        medicalRecord.setAllergies(randomList);

        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
        .andExpect(status().isCreated());
    }

    @Test
    public void testCreateMedicalRecordWithoutName() throws Exception {
        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(new MedicalRecord()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateMedicalRecordWithNotValidBirthDate() throws Exception {
        MedicalRecord medicalRecord = new MedicalRecord();
        ArrayList<String> randomList = new ArrayList<>();
        medicalRecord.setFirstName("");
        medicalRecord.setLastName("");
        medicalRecord.setBirthdate("");
        medicalRecord.setMedications(randomList);
        medicalRecord.setAllergies(randomList);

        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
        .andExpect(status().isBadRequest());
    }

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
        m.setBirthdate("");
        mockMvc.perform(put("/medicalRecord/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(m))))
                .andExpect(status().isBadRequest());
    }

}

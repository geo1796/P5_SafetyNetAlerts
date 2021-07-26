package com.safetynet.alerts.controller;

import static com.safetynet.alerts.jsonParsing.Json.stringify;
import static com.safetynet.alerts.jsonParsing.Json.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.controller.MedicalRecordController;
import com.safetynet.alerts.model.MedicalRecord;
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

import java.util.ArrayList;
import java.util.Optional;

@WebMvcTest(controllers = MedicalRecordController.class)
public class MedicalRecordControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FirestationService firestationService;

    @MockBean
    private PersonService personService;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @Test
    public void testGetMedicalRecord() throws Exception {
        mockMvc.perform(get("/medicalRecord/1")).andExpect(status().isNotFound());
    }

    @Test
    public void testGetMedicalRecords() throws Exception{
        mockMvc.perform(get("/medicalRecords"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateEmptyMedicalRecord() throws Exception{
        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(new MedicalRecord()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateMedicalRecordWithId() throws Exception{
        MedicalRecord m = new MedicalRecord();
        m.setFirstName("John");
        m.setLastName("Doe");
        m.setBirthdate("01/01/2000");
        m.setId(1L);
        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(m))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateMedicalRecordWithoutFirstName() throws Exception{
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setBirthdate("01/01/2000");
        medicalRecord.setLastName("Doe");

        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateMedicalRecordWithoutLastName() throws Exception{
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setBirthdate("01/01/2000");
        medicalRecord.setFirstName("John");

        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateMedicalRecord() throws Exception{
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("01/01/2000");

        mockMvc.perform(post("/medicalRecord").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateNotExistingMedicalRecord() throws Exception{
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("01/01/2000");

        mockMvc.perform(put("/medicalRecord/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateMedicalRecordWithInfos() throws Exception{
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setFirstName("John");
        medicalRecord.setLastName("Doe");
        medicalRecord.setBirthdate("01/01/2000");
        medicalRecord.setMedications(new ArrayList<>());
        medicalRecord.setAllergies(new ArrayList<>());

        when(medicalRecordService.getMedicalRecord(1L)).thenReturn(Optional.of(medicalRecord));

        mockMvc.perform(put("/medicalRecord/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateMedicalRecordWithNotValidBirthdate() throws Exception{
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setBirthdate("abcdef");

        when(medicalRecordService.getMedicalRecord(1L)).thenReturn(Optional.of(new MedicalRecord()));

        mockMvc.perform(put("/medicalRecord/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateMedicalRecordWithoutInfos() throws Exception{
        MedicalRecord medicalRecord = new MedicalRecord();

        when(medicalRecordService.getMedicalRecord(1L)).thenReturn(Optional.of(medicalRecord));

        mockMvc.perform(put("/medicalRecord/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(medicalRecord))))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteMedicalRecord() throws Exception{
        mockMvc.perform(delete("/medicalRecord?lastName=Boyd&firstName=Jacob")).andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteNotExistingMedicalRecord() throws Exception{
        when(medicalRecordService.deleteMedicalRecord("Boyd", "Jacob")).thenThrow(EmptyResultDataAccessException.class);
        mockMvc.perform(delete("/medicalRecord?lastName=Boyd&firstName=Jacob")).andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteMedicalRecordsWithSameNames() throws Exception{
        when(medicalRecordService.deleteMedicalRecord("Boyd", "Jacob")).thenThrow(IllegalArgumentException.class);
        mockMvc.perform(delete("/medicalRecord?lastName=Boyd&firstName=Jacob")).andExpect(status().isMultipleChoices());
    }
}

package com.safetynet.alerts.controller;

import static com.safetynet.alerts.jsonParsing.Json.stringify;
import static com.safetynet.alerts.jsonParsing.Json.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.controller.FirestationController;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.FirestationService;
import com.safetynet.alerts.service.MedicalRecordService;
import com.safetynet.alerts.service.PersonService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@WebMvcTest(controllers = FirestationController.class)
public class FirestationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FirestationService firestationService;

    @MockBean
    private PersonService personService;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @Test
    public void testCreateFirestation() throws Exception{
        mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(new Firestation()))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateFirestationWithId() throws Exception{
        Firestation f = new Firestation();
        f.setId(1L);
        mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
                .andExpect(status().isBadRequest());    }

    @Test
    public void testUpdateNotExistingFirestation() throws Exception{
        Firestation f = new Firestation();
        f.setAddress("adressTest");
        f.setStation(7);
        mockMvc.perform(put("/firestation/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateFirestationWithoutInfos() throws Exception{
        Firestation f = new Firestation();

        when(firestationService.getFirestation(1L)).thenReturn(Optional.of(f));
        mockMvc.perform(put("/firestation/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateFirestationWithInfos() throws Exception{
        Firestation f = new Firestation();
        f.setAddress("adressTest");
        f.setStation(7);

        when(firestationService.getFirestation(1L)).thenReturn(Optional.of(f));
        mockMvc.perform(put("/firestation/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFirestations() throws Exception{
        mockMvc.perform(get("/firestations"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFirestation() throws Exception{
        mockMvc.perform(get("/firestation/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteFirestation() throws Exception{
        mockMvc.perform(delete("/firestation/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteNotExistingFirestation() throws Exception{
        Mockito.doThrow(EmptyResultDataAccessException.class).when(firestationService).deleteFirestation(1L);
        mockMvc.perform(delete("/firestation/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPersonByFirestation() throws Exception{
        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPhoneAlert() throws Exception{
        mockMvc.perform(get("/phoneAlert?firestation=1"))
                .andExpect(status().isOk());
    }
}

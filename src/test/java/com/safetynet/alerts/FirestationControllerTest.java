package com.safetynet.alerts;

import static com.safetynet.alerts.jsonParsing.Json.stringify;
import static com.safetynet.alerts.jsonParsing.Json.toJson;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
        Firestation f = new Firestation();
        f.setAddress("adressTest");
        f.setStation(7);
        mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateFirestation() throws Exception{
        Firestation f = new Firestation();
        f.setAddress("adressTest");
        f.setStation(7);
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
}

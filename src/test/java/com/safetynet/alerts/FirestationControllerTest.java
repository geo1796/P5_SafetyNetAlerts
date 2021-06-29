package com.safetynet.alerts;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.controller.FirestationController;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.FirestationService;
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

    @Test
    public void testCreateFirestation() throws Exception{
        Firestation f = new Firestation();
        f.setAdress("adressTest");
        f.setStation(7);
        mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON).content(asJsonString(f)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testPutFirestation() throws Exception{
        Firestation f = new Firestation();
        f.setAdress("adressTest");
        f.setStation(7);
        mockMvc.perform(put("/firestation/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(f)))
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
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteFirestation() throws Exception{
        mockMvc.perform(delete("/firestation/1"))
                .andExpect(status().isNoContent());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

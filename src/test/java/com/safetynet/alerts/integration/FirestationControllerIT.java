package com.safetynet.alerts.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Firestation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FirestationControllerIT {

	@Autowired
	public MockMvc mockMvc;

	@Order(3)
	@Test
	public void testCreateFirestation() throws Exception{
		Firestation f = new Firestation();
		f.setAdress("adressTest");
		f.setStation(7);
		mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON).content(asJsonString(f)))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/firestations")).andExpect(status().isOk()).andExpect(jsonPath("$[3].adress", is("adressTest")));
	}

	@Order(4)
	@Test
	public void testPutFirestation() throws Exception{
		Firestation f = new Firestation();
		f.setAdress("adressTest");
		f.setStation(7);
		mockMvc.perform(put("/firestation/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(f)))
				.andExpect(status().isOk());

		mockMvc.perform(get("/firestations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].adress", is("adressTest")));
	}

	@Order(1)
	@Test
	public void testGetFirestations() throws Exception {
		
		mockMvc.perform(get("/firestations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].adress", is("adress1")));

	}

	@Order(2)
	@Test
	public void testGetFirestation() throws Exception{
		mockMvc.perform(get("/firestation/{id}", 1))
				.andExpect(status().isOk()).andExpect(jsonPath("$.adress", is("adress1")));
	}

	@Order(5)
	@Test
	public void testDeleteFirestation() throws Exception{

		mockMvc.perform(delete("/firestation/1")).andExpect(status().isNoContent());
		mockMvc.perform(get("/firestations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].adress", is("adress2")));

	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testDeleteNotExistingFirestation() throws Exception{
		mockMvc.perform(delete("/firestation/7"))
				.andExpect(status().isNotFound());
	}
}

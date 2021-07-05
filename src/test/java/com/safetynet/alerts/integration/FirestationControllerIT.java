package com.safetynet.alerts.integration;

import static com.safetynet.alerts.jsonParsing.Json.stringify;
import static com.safetynet.alerts.jsonParsing.Json.toJson;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
		f.setAddress("addressTest");
		f.setStation(7);
		mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/firestation/14")).andExpect(status().isOk()).andExpect(jsonPath("$.address", is("addressTest")))
		.andExpect(jsonPath("$.station", is(7)));
	}

	@Test
	public void testCreateNotValidFirestation() throws Exception{
		Firestation f = new Firestation();
		mockMvc.perform(post("/firestation").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
				.andExpect(status().isBadRequest());
	}

	@Order(5)
	@Test
	public void testUpdateFirestation() throws Exception{
		Firestation f = new Firestation();
		f.setAddress("addressTest");
		f.setStation(7);
		mockMvc.perform(put("/firestation/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
				.andExpect(status().isOk());

		mockMvc.perform(get("/firestations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].address", is("addressTest")));
	}

	@Order(4)
	@Test
	public void testUpdateFirestationWithNullAttributes() throws Exception{
		Firestation f = new Firestation();
		mockMvc.perform(put("/firestation/1").contentType(MediaType.APPLICATION_JSON).content(stringify(toJson(f))))
				.andExpect(status().isOk());

		mockMvc.perform(get("/firestations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].address", is("1509 Culver St")))
				.andExpect(jsonPath("$[0].station", is(3)));
	}

	@Order(1)
	@Test
	public void testGetFirestations() throws Exception {
		
		mockMvc.perform(get("/firestations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].address", is("1509 Culver St")));

	}

	@Order(2)
	@Test
	public void testGetFirestation() throws Exception{
		mockMvc.perform(get("/firestation/{id}", 1))
				.andExpect(status().isOk()).andExpect(jsonPath("$.address", is("1509 Culver St")));
	}

	@Test
	public void testGetNotExistingFirestation() throws Exception{
		mockMvc.perform(get("/firestation/{id}", 18))
				.andExpect(status().isNotFound());
	}

	@Order(6)
	@Test
	public void testDeleteFirestation() throws Exception{

		mockMvc.perform(delete("/firestation/1")).andExpect(status().isNoContent());
		mockMvc.perform(get("/firestations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].address", is("29 15th St")));

	}

	@Test
	public void testDeleteNotExistingFirestation() throws Exception{
		mockMvc.perform(delete("/firestation/20"))
				.andExpect(status().isNotFound());
	}
}

package com.safetynet.alerts;

import com.fasterxml.jackson.databind.JsonNode;
import com.safetynet.alerts.jsonParsing.AlertsData;
import com.safetynet.alerts.jsonParsing.Json;
import com.safetynet.alerts.service.FirestationService;
import com.safetynet.alerts.service.MedicalRecordService;
import com.safetynet.alerts.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@AllArgsConstructor
@SpringBootApplication
public class AlertsApplication implements CommandLineRunner {

	FirestationService firestationService;
	PersonService personService;
	MedicalRecordService medicalRecordService;

	public static void main(String[] args) {
		SpringApplication.run(AlertsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{

		String jsonData = new String(Files.readAllBytes(Paths.get("data.json")), StandardCharsets.UTF_8);
		JsonNode node = Json.parse(jsonData);
		AlertsData data = Json.fromJson(node, AlertsData.class);

		firestationService.saveFirestations(data.getFirestations());
		personService.savePersons(data.getPersons());
		medicalRecordService.saveMedicalRecords(data.getMedicalRecords());

	}

}

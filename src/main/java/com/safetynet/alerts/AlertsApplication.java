package com.safetynet.alerts;

import com.fasterxml.jackson.databind.JsonNode;
import com.safetynet.alerts.jsonParsing.AlertsData;
import com.safetynet.alerts.jsonParsing.Json;
import com.safetynet.alerts.service.FirestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class AlertsApplication implements CommandLineRunner {

	@Autowired
	FirestationService firestationService;

	public static void main(String[] args) {
		SpringApplication.run(AlertsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{

		String jsonData = new String(Files.readAllBytes(Paths.get("data.json")), StandardCharsets.UTF_8);
		JsonNode node = Json.parse(jsonData);
		AlertsData data = Json.fromJson(node, AlertsData.class);

		firestationService.saveFirestations(data.getFirestations());

	}

}

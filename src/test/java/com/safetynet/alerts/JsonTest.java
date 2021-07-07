package com.safetynet.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.safetynet.alerts.jsonParsing.Json;
import com.safetynet.alerts.model.Firestation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonTest {

    private String firestationString = "{ \"address\":\"1509 Culver St\", \"station\":\"3\" }";

    @Test
    public void parseTest() throws JsonProcessingException {
        JsonNode node = Json.parse(firestationString);
        assertEquals("1509 Culver St", node.get("address").asText());
        assertEquals(3, node.get("station").asInt());
    }

    @Test
    public void fromJsonTest() throws JsonProcessingException {
        Firestation f = Json.fromJson(Json.parse(firestationString), Firestation.class);
        assertEquals("1509 Culver St", f.getAddress());
        assertEquals(3, f.getStation());
    }

    @Test
    public void toJsonTest() {
        Firestation f = new Firestation();
        f.setStation(98);
        f.setAddress("addressTest");
        JsonNode node = Json.toJson(f);
        assertEquals("addressTest", node.get("address").asText());
        assertEquals(98, node.get("station").asInt());
    }

}

package com.safetynet.alerts.jsonParsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class Json {
    private static ObjectMapper objectMapper = getDefaultObjectMapper();

    /**
     *
     * @return an ObjectMapper with FAIL_ON_UNKNOWN_PROPERTIES property disabled
     */

    private static ObjectMapper getDefaultObjectMapper(){
        ObjectMapper defaultObjectMapper = new ObjectMapper();
        defaultObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return defaultObjectMapper;
    }

    /**
     *
     * @param src the Json String you want to parse
     * @return a JsonNode corresponding to this String
     * @throws JsonProcessingException if the String couldn't be parsed
     */

    public static JsonNode parse(String src) throws JsonProcessingException {
        return objectMapper.readTree(src);
    }

    /**
     *
     * @param node the JsonNode you want to convert
     * @param <A> the class you want the node param you want to be converted to
     * @return an instance of the class corresponding to the node param
     * @throws JsonProcessingException if the conversion failed
     */

    public static <A> A fromJson(JsonNode node, Class<A> clazz) throws JsonProcessingException {
        return objectMapper.treeToValue(node, clazz);
    }

    /**
     *
     * @param obj the object you want to convert to a JsonNode
     * @return a JsonNode corresponding to the param obj
     */

    public static JsonNode toJson(Object obj){
        return objectMapper.valueToTree(obj);
    }

    /**
     *
     * @param node the JsonNode object you want to convert to string
     * @return a String corresponding to the param node
     * @throws JsonProcessingException if the conversion failed
     */

    public static String stringify(JsonNode node) throws JsonProcessingException {
        ObjectWriter objectWriter = objectMapper.writer();

        return objectWriter.writeValueAsString(node);
    }
}

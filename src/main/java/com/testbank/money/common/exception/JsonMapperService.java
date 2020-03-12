package com.testbank.money.common.exception;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbank.money.di.Service;

public class JsonMapperService implements Service {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public <T> T fromJson(String json, Class<?> clazz)
      throws JsonParseException, JsonMappingException, IOException {
    return (T) objectMapper.readValue(json, clazz);
  }

  public <T> String toJson(T entity) throws JsonParseException, JsonMappingException, IOException {
    return objectMapper.writeValueAsString(entity);
  }
}

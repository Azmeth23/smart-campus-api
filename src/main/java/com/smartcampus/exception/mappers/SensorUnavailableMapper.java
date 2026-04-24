package com.smartcampus.exception.mappers;

import com.smartcampus.exception.SensorUnavailableException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException>{

  @Override
  public Response toResponse(SensorUnavailableException ex){
    return Response.status(403).type(MediaType.APPLICATION_JSON).entity(Map.of(
        "status", 403,
        "error", "Forbidden",
        "message", "Sensor is in MAINTENANCE — readings rejected."
      )).build();
  }
}
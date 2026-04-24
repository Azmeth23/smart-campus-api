package com.smartcampus.exception.mappers;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException>{

  @Override
  public Response toResponse(LinkedResourceNotFoundException ex) {
    return Response.status(422).type(MediaType.APPLICATION_JSON).entity(Map.of(
        "status", 422,
        "error", "Unprocessable Entity",
        "message", "roomId '" + ex.getResourceId() +
          "' does not exist in the system."
      )).build();
  }
}
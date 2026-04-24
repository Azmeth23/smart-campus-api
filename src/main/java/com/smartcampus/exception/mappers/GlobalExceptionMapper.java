package com.smartcampus.exception.mappers;
 
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
 
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable>{
    @Override
    public Response toResponse(Throwable t) {
        // Prevents exposing internal stack traces to API consumers
        return Response.status(500).entity("Internal Server Error").build();
    }
}
 


package com.smartcampus.exception.mappers;

import com.smartcampus.exception.RoomNotEmptyException;
import jakarta.ws.rs.ext.*;
import jakarta.ws.rs.core.*;
import java.util.*;

// tells JAX-RS catch to return 409
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException>{
    @Override
    public Response toResponse(RoomNotEmptyException e){
        Map<String, Object> error = new HashMap<>();
        error.put("status", 409);
        error.put("message", e.getMessage());

        return Response.status(Response.Status.CONFLICT).entity(error).type(MediaType.APPLICATION_JSON).build();
    }
}



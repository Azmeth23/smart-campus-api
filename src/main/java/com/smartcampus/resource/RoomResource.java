package com.smartcampus.resource;
 
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
 

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.*;
 
@Path("/rooms")
public class RoomResource{
    private DataStore dataStore = DataStore.getInstance();
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms(){
        return new ArrayList<Room>(dataStore.getRooms().values());
    }
 
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room){
        dataStore.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }
 
    // GET /{roomId}: Allow users t fetch detailed metadata for a specific room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId){
        Room room = dataStore.getRooms().get(roomId);
        if(room == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "message", "Room not found: " + roomId))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        return Response.ok(room).build();
    }
 
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRooms().get(roomId);
 
        // Check if room exists and if it has sensors
        if (room != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId);
        }
 
        dataStore.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}
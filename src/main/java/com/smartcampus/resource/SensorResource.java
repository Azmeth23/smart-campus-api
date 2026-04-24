package com.smartcampus.resource;
 
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
 
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.*;
 
@Path("/sensors")
public class SensorResource{
    private DataStore dataStore = DataStore.getInstance();
 
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSensor(Sensor sensor){
        // Check if room exists
        if (!dataStore.getRooms().containsKey(sensor.getRoomId())){
            throw new LinkedResourceNotFoundException(sensor.getRoomId());
        }
 
        dataStore.getSensors().put(sensor.getId(), sensor);
        dataStore.getRooms().get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
 
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getSensors(@QueryParam("type") String type){
        List<Sensor> result = new ArrayList<Sensor>();
        for (Sensor s : dataStore.getSensors().values()){
            if(type == null||s.getType().equalsIgnoreCase(type)){
                result.add(s);
            }
        }
        return result;
    }
 
    // Sub resource locator for nested readings
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId){
        return new SensorReadingResource(sensorId);
    }
}
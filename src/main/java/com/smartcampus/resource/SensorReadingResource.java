package com.smartcampus.resource;
 
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;
import com.smartcampus.exception.SensorUnavailableException;
 
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.*;
 
public class SensorReadingResource{
    private String sensorId;
    private DataStore dataStore = DataStore.getInstance();
 
    public SensorReadingResource(String sensorId){
        this.sensorId = sensorId;
    }
 
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addReading(SensorReading reading){
        Sensor sensor = dataStore.getSensors().get(sensorId);
 
        // Check for MAINTENANCE status
        if (sensor != null && "MAINTENANCE".equalsIgnoreCase(sensor.getStatus())){
            throw new SensorUnavailableException(sensorId);
        }
 
        if (!dataStore.getReadings().containsKey(sensorId)){
            dataStore.getReadings().put(sensorId, new ArrayList<SensorReading>());
        }
 
        dataStore.getReadings().get(sensorId).add(reading);
 
        // Side effect: update parent sensor's current value
        if (sensor != null){
            sensor.setCurrentValue(reading.getValue());
        }
    }
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getHistory(){
        List<SensorReading> list = dataStore.getReadings().get(sensorId);
        if(list != null){return list;}
        else{return new ArrayList<SensorReading>();}
    }
}
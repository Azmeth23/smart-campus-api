package com.smartcampus.store;

import com.smartcampus.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore{
    private static final DataStore INSTANCE = new DataStore();
    public static DataStore getInstance(){return INSTANCE;}

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    public void addReading(String sensorId, SensorReading reading){
        List<SensorReading> sensorReadings = readings.get(sensorId);
        
        // If no list exists for this sensor yet, creates one
        if (sensorReadings == null){
            sensorReadings = new ArrayList<SensorReading>();
            readings.put(sensorId, sensorReadings);
        }
        sensorReadings.add(reading);
    }

    public Map<String, Room> getRooms(){return rooms;}
    public Map<String, Sensor> getSensors(){return sensors;}
    public Map<String, List<SensorReading>> getReadings(){return readings;}
}
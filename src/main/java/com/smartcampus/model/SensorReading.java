package com.smartcampus.model;

public class SensorReading{
    private String id; // UUID
    private long timestamp; // Epoch time 
    private double value;

	//no-arg constructor for Jackson deserialisation
    public SensorReading(){}

    public String getId(){return id;}
    public void setId(String id){this.id = id;}
	
    public long getTimestamp(){return timestamp;}
    public void setTimestamp(long timestamp){this.timestamp = timestamp;}
	
    public double getValue(){return value;}
    public void setValue(double value){this.value = value;}
}
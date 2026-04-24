package com.smartcampus.exception;

public class LinkedResourceNotFoundException extends RuntimeException{
  private final String resourceId;
  
  public LinkedResourceNotFoundException(String id){
    super("Referenced resource not found: " + id);
    this.resourceId = id;
  }
  
  public String getResourceId(){return resourceId;}
}
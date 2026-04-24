package com.smartcampus.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.*;

@Path("/")
public class DiscoveryResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> getDiscovery(){
	Map<String, Object> info = new HashMap<String, Object>();
    info.put("version", "1.0");
	info.put("description", "Smart Campus API");
    info.put("contact", "admin@smartcampus.ac.lk");
	
	Map<String, String> links = new HashMap<String, String>();
    links.put("rooms", "/api/v1/rooms");
    links.put("sensors", "/api/v1/sensors");
    info.put("endpoints", links); // HATEOAS principle
    
	return info;
  }
}
       

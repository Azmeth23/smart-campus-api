package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.io.IOException;
import java.net.URI;

public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    public static HttpServer startServer(){
        // Scan for JAX-RS resources and providers in this package
        final ResourceConfig rc = new ResourceConfig().packages("com.smartcampus");
        
        // register Jackson for JSON processing
        rc.register(JacksonFeature.class);

        // Create and start a new instance of grizzly http server. appends "api/v1" to the context path here
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI + "api/v1/"), rc);
    }

    public static void main(String[] args) throws IOException{
        final HttpServer server = startServer();
        System.out.println(String.format("Smart Campus API started at %sapi/v1", BASE_URI));
        System.out.println("Press Enter to stop the server...");
        System.in.read();
        server.shutdownNow();
    }
}







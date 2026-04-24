package com.smartcampus.filter;
 

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;
 
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter{
    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());
 
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException{
        LOG.info("Request: " + requestContext.getMethod() + " " + requestContext.getUriInfo().getPath());
    }
 
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException{
        LOG.info("Response Status: " +responseContext.getStatus());
    }
}
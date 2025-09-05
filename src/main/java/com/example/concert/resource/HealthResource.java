package com.example.concert.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/health")
public class HealthResource {
    @GET @Path("/ping")
    public String ping() { return "pong"; }
}

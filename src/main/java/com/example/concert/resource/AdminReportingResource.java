package com.example.concert.resource;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;


@Path("/admin/reports")
@Produces(MediaType.APPLICATION_JSON)
public class AdminReportingResource {

    @GET
    @Path("/sales")
    public Response sales(@QueryParam("eventId") long eventId) {

        return Response.ok(Map.of(
                "eventId", eventId,
                "sold", 0,
                "available", 0,
                "revenue", "0.00"
        )).build();
    }
}

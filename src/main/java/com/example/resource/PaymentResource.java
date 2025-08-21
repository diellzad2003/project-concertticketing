package com.example.resource;

import com.example.domain.Payment;
import com.example.service.PaymentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private PaymentService paymentService;

    @GET
    public List<Payment> getAllPayments() {
        return paymentService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getPaymentById(@PathParam("id") Integer id) {
        Payment payment = paymentService.findById(id);
        if (payment != null) {
            return Response.ok(payment).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response processNewPayment(Payment payment) {
        Payment processed = paymentService.processPayment(payment);
        if ("SUCCESS".equals(processed.getStatus())) {
            return Response.status(Response.Status.CREATED).entity(processed).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(processed).build();
    }
}

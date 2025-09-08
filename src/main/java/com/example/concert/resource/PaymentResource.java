package com.example.concert.resource;

import com.example.common.AbstractResource;
import com.example.domain.Payment;
import com.example.service.PaymentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/payments")
@Produces("application/json")
@Consumes("application/json")
public class PaymentResource extends AbstractResource<Payment, Integer> {

    @Inject
    private PaymentService paymentService;

    @Override
    protected List<Payment> findAll() {
        return paymentService.findAll();
    }

    @Override
    protected Payment findById(Integer id) {
        return paymentService.findById(id);
    }

    @Override
    protected Payment create(Payment entity) {

        Payment processed = paymentService.processPayment(entity);
        if (!"SUCCESS".equals(processed.getStatus())) {
            throw new IllegalArgumentException("Payment failed or invalid.");
        }
        return processed;
    }

    @Override
    protected Payment update(Payment entity) {

        return paymentService.update(entity);
    }

    @Override
    protected void delete(Payment entity) {
        paymentService.delete(entity);
    }


    @POST
    @Path("/process")
    public Response processPayment(Payment payment) {
        try {
            Payment processed = paymentService.processPayment(payment);
            return Response.status(Response.Status.CREATED).entity(processed).build();
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 409) {
                return Response.status(409).entity(
                        Map.of("error", "Payment already exists for this booking")
                ).build();
            }
            throw e;
        }
    }

}

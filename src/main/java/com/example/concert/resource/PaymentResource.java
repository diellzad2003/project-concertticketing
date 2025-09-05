package com.example.concert.resource;

import com.example.common.AbstractResource;
import com.example.domain.Payment;
import com.example.service.PaymentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;

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
            Payment processed = create(payment);
            return Response.status(Response.Status.CREATED).entity(processed).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

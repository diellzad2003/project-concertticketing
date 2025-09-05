package com.example;

import com.example.config.EntityManagerFactoryHK2;

import com.example.repository.*;
import com.example.service.*;

import jakarta.ws.rs.ApplicationPath;
import jakarta.persistence.EntityManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() {

        packages("com.example.concert.resource");

        register(JacksonFeature.class);




        register(new AbstractBinder() {
            @Override protected void configure() {
                // EntityManager per HTTP request
                bindFactory(EntityManagerFactoryHK2.class)
                        .to(EntityManager.class)
                        .in(RequestScoped.class);

                // Repositories
                bindAsContract(UserRepository.class);
                bindAsContract(EventRepository.class);
                bindAsContract(VenueRepository.class);
                bindAsContract(SeatRepository.class);
                bindAsContract(TicketRepository.class);
                bindAsContract(BookingRepository.class);
                bindAsContract(BookingItemRepository.class);
                bindAsContract(PaymentRepository.class);

                // Services
                bindAsContract(UserService.class);
                bindAsContract(EventService.class);
                bindAsContract(VenueService.class);
                bindAsContract(SeatService.class);
                bindAsContract(TicketService.class);
                bindAsContract(BookingService.class);
                bindAsContract(BookingItemService.class);
                bindAsContract(PaymentService.class);
            }
        });
    }
}

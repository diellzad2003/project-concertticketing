package com.example.resource;

import com.example.domain.BookingItem;
import com.example.service.BookingItemService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/booking-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingItemResource {

    @Inject
    private BookingItemService bookingItemService;

    @GET
    public List<BookingItem> getAllItems() {
        return bookingItemService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getItemById(@PathParam("id") Integer id) {
        BookingItem item = bookingItemService.findById(id);
        if (item != null) {
            return Response.ok(item).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createItem(BookingItem item) {
        bookingItemService.create(item);
        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateItem(@PathParam("id") Integer id, BookingItem item) {
        item.setItemId(id);
        BookingItem updated = bookingItemService.update(item);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteItem(@PathParam("id") Integer id) {
        BookingItem item = bookingItemService.findById(id);
        if (item != null) {
            bookingItemService.delete(item);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}

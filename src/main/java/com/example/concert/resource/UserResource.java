package com.example.concert.resource;

import com.example.domain.User;
import com.example.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;


    @POST
    public Response create(User user) {
        User created = userService.create(user);
        return Response
                .created(URI.create("/api/users/" + created.getId()))
                .entity(created)
                .build();
    }


    @GET
    public List<User> list() {
        return userService.findAll();
    }


    @GET
    @Path("{id}")
    public User get(@PathParam("id") Integer id) {
        User u = userService.findById(id);
        if (u == null) throw new NotFoundException("User " + id + " not found");
        return u;
    }


    @PUT
    @Path("{id}")
    public User update(@PathParam("id") Integer id, User user) {
        user.setId(id);
        return userService.update(user);
    }


    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Integer id) {
        User u = userService.findById(id);
        if (u == null) throw new NotFoundException("User " + id + " not found");
        userService.delete(u);
        return Response.noContent().build();
    }


    @POST
    @Path("/login")
    public Response login(@QueryParam("email") String email,
                          @QueryParam("password") String password) {
        boolean ok = userService.login(email, password);
        return ok ? Response.ok().build()
                : Response.status(Response.Status.UNAUTHORIZED).build();
    }
}

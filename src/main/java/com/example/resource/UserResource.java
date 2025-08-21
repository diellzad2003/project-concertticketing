package com.example.resource;

import com.example.domain.User;
import com.example.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserService userService;

    @GET
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Integer id) {
        User user = userService.findById(id);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createUser(User user) {
        userService.create(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Integer id, User user) {
        user.setUserId(id);
        User updated = userService.update(user);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Integer id) {
        User user = userService.findById(id);
        if (user != null) {
            userService.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/login")
    public Response login(@QueryParam("email") String email,
                          @QueryParam("password") String password) {
        boolean ok = userService.login(email, password);
        return (ok ? Response.ok().build() :
                Response.status(Response.Status.UNAUTHORIZED).build());
    }
}

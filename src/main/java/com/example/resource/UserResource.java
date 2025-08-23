package com.example.resource;

import com.example.common.AbstractResource;
import com.example.domain.User;
import com.example.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
@Produces("application/json")
@Consumes("application/json")
public class UserResource extends AbstractResource<User, Integer> {

    @Inject
    private UserService userService;

    @Override
    protected List<User> findAll() {
        return userService.findAll();
    }

    @Override
    protected User findById(Integer id) {
        return userService.findById(id);
    }

    @Override
    protected User create(User entity) {
        userService.create(entity);
        return entity;
    }

    @Override
    protected User update(User entity) {
        return userService.update(entity);
    }

    @Override
    protected void delete(User entity) {
        userService.delete(entity);
    }


    @POST
    @Path("/login")
    public Response login(@QueryParam("email") String email,
                          @QueryParam("password") String password) {
        boolean ok = userService.login(email, password);
        return ok ? Response.ok().build() :
                Response.status(Response.Status.UNAUTHORIZED).build();
    }
}

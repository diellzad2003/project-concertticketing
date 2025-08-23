package com.example.common;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class AbstractResource<T, ID> {

    protected abstract List<T> findAll();
    protected abstract T findById(ID id);
    protected abstract T create(T entity);
    protected abstract T update(T entity);
    protected abstract void delete(T entity);

    @GET
    public List<T> getAll() {
        return findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") ID id) {
        T entity = findById(id);
        return (entity != null) ? Response.ok(entity).build() :
                Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createEntity(T entity) {
        T created = create(entity);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateEntity(@PathParam("id") ID id, T entity) {

        T updated = update(entity);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEntity(@PathParam("id") ID id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}

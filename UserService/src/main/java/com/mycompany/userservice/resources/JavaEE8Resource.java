package com.mycompany.userservice.resources;

import enteties.User;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author
 */
@Path("user")
public class JavaEE8Resource {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("my_persistence_unit");
    EntityManager em = emf.createEntityManager();

    @GET
    public Response ping() {
        return Response
                .ok("ping")
                .build();
    }

    @POST
    @Path("register/{username}/{password}/{address}")
    public Response register(@PathParam("username") String username, @PathParam("password") String password, @PathParam("address") String address) {
        try {
            try {
                User user = em.createQuery("Select user FROM User user Where user.username=:username ", User.class).setParameter("username", username).getSingleResult();
            } catch (Exception e) {
                UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                ut.begin();
                em.joinTransaction();
                em.createNativeQuery("INSERT INTO User(username,password,address, ringtone) VALUES (?1, ?2, ?3,?4)").setParameter(1, username).setParameter(2, password).setParameter(3, address).setParameter(4, "iphonealarm").executeUpdate();

                ut.commit();
                System.out.println("Registrovan");
               return Response.ok().entity("Registration Successful!").build();

            }

            return Response.ok().entity("Username already taken").build();

        } catch (Exception ex) {
            Logger.getLogger(JavaEE8Resource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    @POST
    @Path("login/{username}/{password}")
    public Response login(@PathParam("username") String username, @PathParam("password") String password) {
        User user;
        try {
            user = em.createQuery("Select user FROM User user Where user.username=:username ", User.class).setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            return Response.ok().entity("Unknown User").build();
        }
        if (!user.getPassword().equals(password)) {
            return Response.ok().entity("Invalid password").build();
        }

        return Response.ok().entity("Login successful").build();
    }
}

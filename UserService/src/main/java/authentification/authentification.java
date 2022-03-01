package authentification;

import enteties.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
//@Stateless
public class authentification implements ContainerRequestFilter{ 

    @PersistenceContext
    EntityManager manager;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        List<String> authHeaderValues = requestContext.getHeaders().get("Authorization");
        
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            System.out.println(decodedAuthHeaderValue);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();
            List<String> korisnici = manager.createQuery("SELECT user.password FROM User user WHERE user.username = :username",String.class).setParameter("username", username).getResultList();
            if(korisnici.isEmpty()) {
                requestContext.abortWith(Response.ok("Unknown user").build());
                return;
            }
            
            if(korisnici.get(0).equals(password)) return;
            System.out.println("Nema");
            Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Password").build();
            requestContext.abortWith(response);
            return;
        }
        
        Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Credentials required").build();
        requestContext.abortWith(response);
        return;
    }
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.userservice.resources;


import enteties.Song;
import enteties.User;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import messages.SongMessage;

/**
 *
 * @author Janko
 */
@Stateless
@Path("reproduct")
public class Reproducter {
    Context context;
    ConnectionFactory factory;
    Queue songQueue;

    public Reproducter() {
        try {
            this.context = new InitialContext();
            songQueue = (Queue) context.lookup("playSong");
            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
        } catch (NamingException ex) {
            Logger.getLogger(Reproducter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @POST
    @Path("{name}")
    public Response playSong(@PathParam("name") String name, @javax.ws.rs.core.Context HttpHeaders httpHeaders){
        
       
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        JMSContext contextJ = factory.createContext();
        JMSProducer producer = contextJ.createProducer();
        String username=stringTokenizer.nextToken();
         SongMessage msg=new SongMessage(name, username, "playSong");
        ObjectMessage msgObj = contextJ.createObjectMessage(msg);
        producer.send(songQueue,msgObj);
        System.out.println("Play song sent");
        
        
        return Response.ok().build();
    }
    
    @GET
    public Response getSongs(@javax.ws.rs.core.Context HttpHeaders httpHeaders){
        EntityManagerFactory emf=Persistence.createEntityManagerFactory("my_persistence_unit");
        EntityManager em=emf.createEntityManager();
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        JMSContext contextJ = factory.createContext();
        JMSProducer producer = contextJ.createProducer();
        String username=stringTokenizer.nextToken();
        User user = em.createQuery("SELECT user From User user Where user.username=:username", User.class).setParameter("username", username).getSingleResult();
        List<Song> resultList = em.createQuery("Select song from Song song where song.user=:user", Song.class).setParameter("user", user).getResultList();
           StringBuilder strBuilder=new StringBuilder();
           for(Song s:resultList){
               strBuilder.append("Title: ").append(s.getName()).append("\n");
           }
        
        return Response.ok(String.join("\n", strBuilder.toString())).build();
     
        
    }
    
    
}

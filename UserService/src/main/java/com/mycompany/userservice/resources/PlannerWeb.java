/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.userservice.resources;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import enteties.Planner;
import enteties.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
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
import messages.PlannerMessage;
import messages.alarmMessage;

/**
 * // * // * @author Janko
 */
@Path("planner")
@Stateless
public class PlannerWeb {

    private ConnectionFactory factory;

    private Queue plannerQueue;

    private Queue getSongs;

    @POST
    @Path("newPlanner/{name}/{date}/{duration}/{location}")
    public Response newPlanner(@PathParam("name") String name, @PathParam("date") String date, @PathParam("duration") String duration, @PathParam("location") String location, @javax.ws.rs.core.Context HttpHeaders httpHeaders) {
        try {
            Context context = new InitialContext();

            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            plannerQueue = (Queue) context.lookup("plannerQueue");
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            JMSContext contextJ = factory.createContext();
            JMSProducer producer = contextJ.createProducer();
            String username = stringTokenizer.nextToken();
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
            Date dateAlarm = null;
            try {
                dateAlarm = format.parse(date);
            } catch (ParseException ex) {
                Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
            }

            PlannerMessage msg = new PlannerMessage(dateAlarm, name, Integer.parseInt(duration), location, "newPlanner", username, null, null, null);
            ObjectMessage msgObj = contextJ.createObjectMessage(msg);
            producer.send(plannerQueue, msgObj);
            System.out.println("Poslato");

        } catch (NamingException ex) {
            Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    @POST
    @Path("changePlanner/{name}/{namePlanner}/{date}/{duration}/{location}")
    public Response changePlanner(@PathParam("namePlanner") String nameChange, @PathParam("name") String name, @PathParam("date") String date, @PathParam("duration") String duration, @PathParam("location") String location, @javax.ws.rs.core.Context HttpHeaders httpHeaders) {
        try {
            Context context = new InitialContext();

            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            plannerQueue = (Queue) context.lookup("plannerQueue");
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            JMSContext contextJ = factory.createContext();
            JMSProducer producer = contextJ.createProducer();
            String username = stringTokenizer.nextToken();

            PlannerMessage msg = new PlannerMessage(null, null, 0, null, "changePlanner", null, null, null, null);
            SimpleDateFormat format;
            if (!date.equals("n")) {
                format = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
                Date dateAlarm = null;
                try {
                    dateAlarm = format.parse(date);
                } catch (ParseException ex) {
                    Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
                }

                msg.setDate(dateAlarm);
            }
            if (!name.equals("n")) {
                msg.setName(name);
            }
            if (!duration.equals("n")) {
                msg.setDuration(Integer.parseInt(duration));
            }
            if (!location.equals("n")) {
                msg.setDestination(location);
            }
            msg.setUsername(username);
            msg.setNameChange(nameChange);
            ObjectMessage msgObj = contextJ.createObjectMessage(msg);
            producer.send(plannerQueue, msgObj);
            System.out.println("Change sent");

        } catch (NamingException ex) {
            Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    @POST
    @Path("deletePlanner/{namePlanner}")
    public Response deletePlanner(@PathParam("namePlanner") String name, @javax.ws.rs.core.Context HttpHeaders httpHeaders) {
        try {
            Context context = new InitialContext();

            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            plannerQueue = (Queue) context.lookup("plannerQueue");
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            JMSContext contextJ = factory.createContext();
            JMSProducer producer = contextJ.createProducer();
            String username = stringTokenizer.nextToken();
            PlannerMessage msg = new PlannerMessage(null, name, 0, null, "deletePlanner", username, null, null, null);
            ObjectMessage msgObj = contextJ.createObjectMessage(msg);
            producer.send(plannerQueue, msgObj);
            System.out.println("Delete sent");

        } catch (NamingException ex) {
            Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    @GET
    public Response getPlanner(@javax.ws.rs.core.Context HttpHeaders httpHeaders) {
        String mes = "";
        try {
            Context context = new InitialContext();
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("my_persistence_unit");
            EntityManager e = emf.createEntityManager();
//            getSongs = (Queue) context.lookup("getSongs");

            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            plannerQueue = (Queue) context.lookup("plannerQueue");
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            JMSContext contextJ = factory.createContext();
            JMSProducer producer = contextJ.createProducer();
            String username = stringTokenizer.nextToken();
            PlannerMessage msg = new PlannerMessage(null, null, 0, null, "getPlanner", username, null, null, null);
            ObjectMessage msgObj = contextJ.createObjectMessage(msg);
            producer.send(plannerQueue, msgObj);
            System.out.println("Get sent");
            User u = e.createQuery("SELECT user From User user Where user.username=:username", User.class).setParameter("username", username).getSingleResult();
            List<enteties.Planner> resultList = u.getPlannerList();
            StringBuilder strBuilder = new StringBuilder();
            for (enteties.Planner p : resultList) {
                strBuilder.append("Name: ").append(p.getName()).append(", Date: ").append(p.getStartDate()).append(", Destination: ").append(p.getDestination()).append("\n");
            }

            return Response.ok(String.join("\n", strBuilder.toString())).build();

        } catch (NamingException ex) {
            Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
//        catch (JMSException ex) {
//            Logger.getLogger(Planner.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return Response.ok().entity(mes).build();
    }

    @GET
    @Path("calculate/{locationA}/{locationB}")
    public Response calculate(@PathParam("locationA") String locationA, @PathParam("locationB") String locationB, @javax.ws.rs.core.Context HttpHeaders httpHeaders) {
        try {
            Context context = new InitialContext();

            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            plannerQueue = (Queue) context.lookup("plannerQueue");
            Queue calculate = (Queue) context.lookup("calculate");
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            JMSContext contextJ = factory.createContext();
            JMSProducer producer = contextJ.createProducer();
            JMSConsumer consumer = contextJ.createConsumer(plannerQueue);
            String username = stringTokenizer.nextToken();
            PlannerMessage msg = new PlannerMessage(null, null, 0, null, "calculate", username, null, locationA, locationB);
            ObjectMessage msgObj = contextJ.createObjectMessage(msg);
            producer.send(plannerQueue, msgObj);
            Long distance = calculate(locationA, locationB);
            return Response.ok().entity(distance.toString()).build();

        } catch (NamingException ex) {
            Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();

    }

    @GET
    @Path("calculate")
    public Response calculateOneLocation(@javax.ws.rs.core.Context HttpHeaders httpHeaders) {
   
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("my_persistence_unit");
            EntityManager em = emf.createEntityManager();

            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");

            String username = stringTokenizer.nextToken();
            User user = em.createQuery("Select user FROM User user Where user.username=:username", User.class).setParameter("username", username).getSingleResult();
            List<Planner> planners = em.createQuery("Select planner FROM Planner planner Where planner.idUser=:user", Planner.class).setParameter("user", user).getResultList();
            planners.sort(Comparator.comparing(Planner::getStartDate));
            Date now = new Date();
            System.out.println(now);
            Planner curr=null;
            for (Planner planner : planners) {
                if (planner.getStartDate().getTime() < now.getTime()) {
                    curr=planner;
                } else {
                    break;
                }
            }
            if(curr!=null){
                return Response.ok().entity(curr.getDestination()).build();
            }
            else
                return Response.ok().entity("All tasks are later!").build();

        
  

    }

    private static long calculate(String x, String y) {
        try {
            String key = "AIzaSyDxhFqG90rQHN_QGdubeSkyDHtivpgaZJg";
            GeoApiContext gc = new GeoApiContext.Builder().apiKey(key).build();
            DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(gc);

            DistanceMatrix result = request.origins(x)
                    .destinations(y)
                    .mode(TravelMode.DRIVING)
                    .language("en-US")
                    .await();

            return result.rows[0].elements[0].duration.inSeconds;

        } catch (ApiException ex) {
            Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlannerWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

}

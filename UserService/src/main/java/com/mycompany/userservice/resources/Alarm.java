/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.userservice.resources;

import enteties.Planner;
import enteties.User;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
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
import javax.persistence.PersistenceContext;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import messages.alarmMessage;

/**
 *
 * @author Janko
 */
@Path("alarm")
@Stateless
public class Alarm {

    JMSProducer producer;
    Queue ringtoneQueue;
    ConnectionFactory factory;

    @POST
    @Path("{ringtone}")
    public Response setRingtone(@PathParam("ringtone") String ringtone, @javax.ws.rs.core.Context HttpHeaders httpHeaders) {

        Context context;
        try {
            context = new InitialContext();

            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            ringtoneQueue = (Queue) context.lookup("setRingtoneQueue");
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            JMSContext contextJ = factory.createContext();
            JMSProducer producer = contextJ.createProducer();
            String username = stringTokenizer.nextToken();
            alarmMessage alarmMess = new alarmMessage(username, ringtone, "setRingtone", null, 0, "");
            ObjectMessage mess = contextJ.createObjectMessage(alarmMess);
            producer.send(ringtoneQueue, mess);
            System.out.println("Set ringtone sent");

            return Response.status(Response.Status.CREATED).build();
        } catch (NamingException ex) {
            Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("newAlarmTask/{name}")
    public Response newAlarmTask(@PathParam("name") String name, @javax.ws.rs.core.Context HttpHeaders httpHeaders) {
        try {
            

            Context context;
            context = new InitialContext();
           
            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            ringtoneQueue = (Queue) context.lookup("setRingtoneQueue");
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            JMSContext contextJ = factory.createContext();
            JMSProducer producer = contextJ.createProducer();
            String username = stringTokenizer.nextToken();
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
            
            alarmMessage alarmMess;

            alarmMess = new alarmMessage(username, null, "setTaskAlarm", null, 0, name);

            ObjectMessage mess = contextJ.createObjectMessage(alarmMess);
            producer.send(ringtoneQueue, mess);
            System.out.println("New alarm sent");
        } catch (NamingException ex) {
            Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok().build();

    }

    @POST
    @Path("newAlarm/{date}/{period}/{name}")
    public Response newPeriodicAlarm(@PathParam("date") String date, @PathParam("period") int period, @PathParam("name") String name, @javax.ws.rs.core.Context HttpHeaders httpHeaders) {

        try {
            Context context;
            context = new InitialContext();

            factory = (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");
            ringtoneQueue = (Queue) context.lookup("setRingtoneQueue");
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")), StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            JMSContext contextJ = factory.createContext();
            JMSProducer producer = contextJ.createProducer();
            String username = stringTokenizer.nextToken();
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
            Date dateAlarm;
            alarmMessage alarmMess;
            if (!date.equals("null")) {
                try {
                    dateAlarm = format.parse(date);
                    if (period != 0) {
                        alarmMess = new alarmMessage(username, null, "newPeriodicAlarm", dateAlarm, period, name);
                    } else {
                        alarmMess = new alarmMessage(username, null, "newAlarm", dateAlarm, period, name);
                    }
                    ObjectMessage mess = contextJ.createObjectMessage(alarmMess);
                    producer.send(ringtoneQueue, mess);
                    System.out.println("New alarm sent");
                } catch (ParseException ex) {
                    Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                alarmMess = new alarmMessage(username, null, "setTaskAlarm", null, period, name);
                ObjectMessage mess = contextJ.createObjectMessage(alarmMess);
                producer.send(ringtoneQueue, mess);
                System.out.println("Set Task alarm sent");
            }

            return Response.ok().build();
        } catch (NamingException ex) {
            Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }
}

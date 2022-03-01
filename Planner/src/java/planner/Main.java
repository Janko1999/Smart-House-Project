/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import enteties.Planner;
import enteties.User;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import javax.transaction.UserTransaction;
import messages.PlannerMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author Janko
 */
public class Main {

    @Resource(lookup = "cF")
    private static ConnectionFactory factory;

    @Resource(lookup = "plannerQueue")
    private static Queue plannerQueue;
    @Resource(lookup = "getSongs")
    private static Queue getSongs;
    @Resource(lookup = "calculate")
    private static Queue calculate;

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlannerPU");
        EntityManager em = emf.createEntityManager();
        JMSContext context = factory.createContext();
        JMSConsumer consumer = context.createConsumer(plannerQueue);
        JMSProducer producer = context.createProducer();
        while (true) {
            try {
                PlannerMessage receive = (PlannerMessage) ((ObjectMessage) consumer.receive()).getObject();
                System.out.println(receive.getOperation());
                switch (receive.getOperation()) {

                    case "newPlanner":
                        
                      
                          try {
                        EntityManagerFactory emf1 = Persistence.createEntityManagerFactory("PlannerPU");
                        EntityManager em1 = emf.createEntityManager();

                        User user = em1.createQuery("Select user FROM User user where user.username= :username", User.class).setParameter("username", receive.getUsername()).getSingleResult();
                        
                           
                        
                            String destination;
                            if (receive.getDestination().equals("n")) {
                                destination = user.getAddress();
                            } else {
                                destination = receive.getDestination();
                            }
                            Date date = receive.getDate();

                            List<Planner> plannerList = em1.createQuery("SELECT planer FROM Planner planer where planer.idUser=:user", Planner.class).setParameter("user", user).getResultList();
                           boolean flag=false;
                            for(Planner planner:plannerList){
                               if(planner.getName().equals(receive.getName())){
                                   Error error = new Error("Name of the Task already taken!");
                                   flag=true;
                               }
                           }
                            if(flag){
                                break;
                            }
                            plannerList.sort(Comparator.comparing(Planner::getStartDate));
                            Planner before = null;
                            Planner after = null;
                            int i = -1;
                            int j = -1;
                            for (Planner planer : plannerList) {
                                if (planer.getStartDate().getTime() < receive.getDate().getTime()) {
                                    i++;
                                } else {
                                    break;
                                }

                            }
                            if (i != -1) {
                                before = plannerList.get(i);
                            }
                            try {
                                after = plannerList.get(i + 1);
                            } catch (Exception e) {
                                after = null;
                            }

                            if (before != null && after != null) {
                                if ((before.getStartDate().getTime() + before.getDuration() * 60 * 1000 + calculate(before.getDestination(), destination)) < receive.getDate().getTime()
                                        && (receive.getDate().getTime() + receive.getDuration() * 60 * 1000 + calculate(destination, after.getDestination())) < after.getStartDate().getTime()) {
                                    UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                                    ut.begin();
                                    em.joinTransaction();
                                    em.createNativeQuery("INSERT INTO Planner(startDate,duration,destination,name, idUser) VALUES (?1, ?2, ?3,?5,?6)").setParameter(1, date).setParameter(2, receive.getDuration()).setParameter(3, destination).setParameter(5, receive.getName()).setParameter(6, user.getIduser()).executeUpdate();

                                    ut.commit();
                                    System.out.println("between");
                                    Error error = new Error("Task successfully added!");
                                    break;
                                }
                            }
                            if (before == null && after == null) {
                                UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                                ut.begin();
                                em.joinTransaction();
                                em.createNativeQuery("INSERT INTO Planner(startDate,duration,destination,name, idUser) VALUES (?1, ?2, ?3,?5,?6)").setParameter(1, date).setParameter(2, receive.getDuration()).setParameter(3, destination).setParameter(5, receive.getName()).setParameter(6, user.getIduser()).executeUpdate();

                                ut.commit();
                                System.out.println("empty");
                                Error error = new Error("Task successfully added!");
                                break;
                            }
                            if (before == null) {
                                if ((receive.getDate().getTime() + receive.getDuration() * 60 * 1000 + calculate(destination, after.getDestination())) < after.getStartDate().getTime()) {
                                    UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                                    ut.begin();
                                    em.joinTransaction();
                                    em.createNativeQuery("INSERT INTO Planner(startDate,duration,destination,name, idUser) VALUES (?1, ?2, ?3,?5,?6)").setParameter(1, date).setParameter(2, receive.getDuration()).setParameter(3, destination).setParameter(5, receive.getName()).setParameter(6, user.getIduser()).executeUpdate();

                                    ut.commit();
                                    System.out.println("first");
                                    Error error = new Error("Task successfully added!");
                                    break;
                                }

                            }
                            if (after == null) {
                                if ((before.getStartDate().getTime() + before.getDuration() * 60 * 1000 + calculate(before.getDestination(), destination)) < receive.getDate().getTime()) {
                                    UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                                    ut.begin();
                                    em.joinTransaction();
                                    em.createNativeQuery("INSERT INTO Planner(startDate,duration,destination,name, idUser) VALUES (?1, ?2, ?3,?5,?6)").setParameter(1, date).setParameter(2, receive.getDuration()).setParameter(3, destination).setParameter(5, receive.getName()).setParameter(6, user.getIduser()).executeUpdate();

                                    ut.commit();
                                    System.out.println("last");
                                    Error error = new Error("Task successfully added!");
                                    break;
                                }
                            }
                            Error error = new Error("Too busy for this Task!");
                            break;
                        
                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    break;
                    case "changePlanner":
                         try {
                        boolean something = false;
                        User user = em.createQuery("Select user FROM User user where user.username= :username", User.class).setParameter("username", receive.getUsername()).getSingleResult();
                        List<Planner> p = em.createQuery("SELECT planer FROM Planner planer where planer.idUser=:user", Planner.class).setParameter("user", user).getResultList();
                        boolean found=false;
                        for(Planner planner:p){
                               if(planner.getName().equals(receive.getNameChange())){
                                   found=true;
                                  
                               }
                           }
                        if(!found){
                            Error error = new Error("Can't find Task!");
                             break;
                        }
                        

                        String destination;
                        String query = "Update Planner SET ";
                        Date date = receive.getDate();
                        found = false;
                        if (date != null || receive.getDuration() != 0 || receive.getDestination() != null) {

                            EntityManagerFactory emf1 = Persistence.createEntityManagerFactory("PlannerPU");
                            EntityManager em1 = emf.createEntityManager();
                            user = em1.createQuery("Select user FROM User user where user.username= :username", User.class).setParameter("username", receive.getUsername()).getSingleResult();

                            List<Planner> plannerList = em1.createQuery("SELECT planer FROM Planner planer where planer.idUser=:user", Planner.class).setParameter("user", user).getResultList();
                            int k = 0;
                            for (Planner planer : plannerList) {
                                if (planer.getName().equals(receive.getNameChange())) {
                                    break;
                                } else {
                                    k++;
                                }

                            }
                            Planner x = plannerList.get(k);
                            int duration;
                            if (receive.getDuration() == 0) {
                                duration = x.getDuration();
                            } else {
                                duration = receive.getDuration();
                            }
                            if (receive.getDestination() == null) {
                                destination = x.getDestination();
                            } else {
                                destination = receive.getDestination();
                            }
                            if (date == null) {
                                date = x.getStartDate();
                            }

                            plannerList.remove(k);
                            plannerList.sort(Comparator.comparing(Planner::getStartDate));
                            Planner before = null;
                            Planner after = null;
                            int i = -1;
                            int j = -1;
                            for (Planner planer : plannerList) {
                                if (planer.getStartDate().getTime() < date.getTime()) {
                                    i++;
                                } else {
                                    break;
                                }

                            }
                            if (i != -1) {
                                before = plannerList.get(i);
                            }
                            try {
                                after = plannerList.get(i + 1);
                            } catch (Exception exep) {
                                after = null;
                            }

                            if (before != null && after != null) {
                                if ((before.getStartDate().getTime() + before.getDuration() * 60 * 1000 + calculate(before.getDestination(), destination)) < date.getTime()
                                        && (date.getTime() + duration * 60 * 1000 + calculate(destination, after.getDestination())) < after.getStartDate().getTime()) {
                                    UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                                    ut.begin();
                                    em.joinTransaction();
                                    em.createQuery("UPDATE Planner SET startDate=:date, duration=:duration, destination=:destination WHERE idUser= :user AND name=:nameChange").setParameter("destination", destination).setParameter("duration", duration).setParameter("nameChange", receive.getNameChange()).setParameter("date", date).setParameter("user", user).executeUpdate();
                                    ut.commit();
                                    System.out.println("between");
                                    Error error1 = new Error("Task successfully changed!");

                                }
                                else{
                                    Error error1 = new Error("Too busy for this Task!");
                                }
                            } else if (before == null && after == null) {
                                UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                                ut.begin();
                                em.joinTransaction();
                                em.createQuery("UPDATE Planner SET startDate=:date, duration=:duration, destination=:destination WHERE idUser= :user AND name=:nameChange").setParameter("destination", destination).setParameter("duration", duration).setParameter("nameChange", receive.getNameChange()).setParameter("date", date).setParameter("user", user).executeUpdate();
                                ut.commit();
                                System.out.println("empty");
                                Error error1 = new Error("Task successfully changed!");

                            } else if (before == null) {
                                if ((date.getTime() + duration * 60 * 1000 + calculate(destination, after.getDestination())) < after.getStartDate().getTime()) {
                                    UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                                    ut.begin();
                                    em.joinTransaction();
                                    em.createQuery("UPDATE Planner SET startDate=:date, duration=:duration, destination=:destination WHERE idUser= :user AND name=:nameChange").setParameter("destination", destination).setParameter("duration", duration).setParameter("nameChange", receive.getNameChange()).setParameter("date", date).setParameter("user", user).executeUpdate();
                                    ut.commit();
                                    System.out.println("first");
                                    Error error1 = new Error("Task successfully changed!");

                                }
                                else{
                                    Error error1 = new Error("Too busy for this Task!");
                                }

                            } else if (after == null) {
                                if ((before.getStartDate().getTime() + before.getDuration() * 60 * 1000 + calculate(before.getDestination(), destination)) < date.getTime()) {
                                    UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                                    ut.begin();
                                    em.joinTransaction();
                                    em.createQuery("UPDATE Planner SET startDate=:date, duration=:duration, destination=:destination WHERE idUser= :user AND name=:nameChange").setParameter("destination", destination).setParameter("duration", duration).setParameter("nameChange", receive.getNameChange()).setParameter("date", date).setParameter("user", user).executeUpdate();
                                    ut.commit();
                                    System.out.println("last");
                                    Error error1 = new Error("Task successfully changed!");

                                }
                                else{
                                    Error error1 = new Error("Too busy for this Task!");
                                }
                            }
                            

                        }

                        if (receive.getName() != null) {
                            query += "name='" + receive.getName() + "',";
                            something = true;
                        }
                        if (something) {
                            StringBuffer sb = new StringBuffer(query);
                            sb.deleteCharAt(sb.length() - 1);
                            sb.append(" WHERE idUser= :user AND name=:nameChange");
                            UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                            ut.begin();
                            em.joinTransaction();

                            em.createQuery(sb.toString()).setParameter("user", user).setParameter("nameChange", receive.getNameChange()).executeUpdate();

                            ut.commit();
                            System.out.println("planner.Main.main()");
                        }
                        break;

                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    break;
                    case "deletePlanner":
                        try {
                        User user = em.createQuery("Select user FROM User user where user.username= :username", User.class).setParameter("username", receive.getUsername()).getSingleResult();
                        String name = receive.getName();
                        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                        List<Planner> p = em.createQuery("SELECT planer FROM Planner planer where planer.idUser=:user", Planner.class).setParameter("user", user).getResultList();
                        boolean found=false;
                        for(Planner planner:p){
                               if(planner.getName().equals(receive.getName())){
                                   found=true;
                                  
                               }
                           }
                        if(!found){
                            Error error = new Error("Can't find Task!");
                             break;
                        }
                        ut.begin();
                        
                        em.joinTransaction();
                        
                            em.createQuery("DELETE  FROM Planner planner Where planner.name=:name and planner.idUser=:user").setParameter("name", name).setParameter("user", user).executeUpdate();
                        
                        Error error = new Error("Task successfully deleted!");
                        ut.commit();
                        System.out.println("planner.Main.main()");
                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    break;

                    case "getPlanner":
                        System.out.println("Tu sam");
                        try {
                            User user = em.createQuery("Select user FROM User user where user.username= :username", User.class).setParameter("username", receive.getUsername()).getSingleResult();
                            String name = receive.getName();
                            UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");

                            ut.begin();

                            em.joinTransaction();

                            List<Planner> listPlanner = em.createQuery("SELECT planner  FROM Planner planner Where planner.idUser=:user", Planner.class).setParameter("user", user).getResultList();

                            ut.commit();
                            System.out.println("planner.Main.main()");
                            StringBuilder str = new StringBuilder();
                            for (Planner p : listPlanner) {
                                str.append(p.toString() + "\n");
                            }

                            ObjectMessage m = context.createObjectMessage(str.toString());
                            producer.send(getSongs, m);
                            System.out.println("Sent");

                        } catch (Exception ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        break;
                    case "calculate":
//                        OkHttpClient client = new OkHttpClient().newBuilder().build();
//                        Request request = new Request.Builder()
//                                .url("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+receive.getLocationA()+"&destinations="+receive.getLocationB()+"&key=YOUR_API_KEY")
//                                .method("GET", null)
//                                .build();
                        Long res = calculate(receive.getLocationA(), receive.getLocationB());
                        ObjectMessage m = context.createObjectMessage(res);
                        producer.send(plannerQueue, m);
                        System.out.println("Sent");

                        break;
                }

            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static Long calculate(String x, String y) {
        try {
            String key = "AIzaSyDxhFqG90rQHN_QGdubeSkyDHtivpgaZJg";
            GeoApiContext gc = new GeoApiContext.Builder().apiKey(key).build();
            DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(gc);

            DistanceMatrix result = request.origins(x)
                    .destinations(y)
                    .mode(TravelMode.DRIVING)
                    .language("en-US")
                    .await();

            return result.rows[0].elements[0].duration.inSeconds * 1000;

        } catch (ApiException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}

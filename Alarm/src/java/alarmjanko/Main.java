/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmjanko;

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
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import messages.SongMessage;
import messages.alarmMessage;

/**
 *
 * @author Janko
 */
public class Main {

   @Resource(lookup="jms/__defaultConnectionFactory")
   private static ConnectionFactory connFact;
   @Resource(lookup="setRingtoneQueue")
   private static Queue ringtoneQueue;
   @Resource (lookup="playSong")
   private static Queue ring;
    
    public static void main(String[] args) {
       JMSContext context = connFact.createContext();
       EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmJankoPU");
    EntityManager em = emf.createEntityManager();
       JMSConsumer consumer = context.createConsumer(ringtoneQueue);
        JMSProducer producer=context.createProducer();
       while(true){
           try {
               alarmMessage receive = (alarmMessage)((ObjectMessage) consumer.receive()).getObject();
               switch(receive.getOperation()){
                    case "setRingtone":
                        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                        ut.begin();
                        em.joinTransaction();
                        Query query =em.createQuery("UPDATE User SET ringtone = :zvono WHERE username= :user ").setParameter("zvono", receive.getRingtone()).setParameter("user", receive.getUsername());
                        query.executeUpdate();
                        ut.commit();
                        System.out.println("Izmenjeno"+ receive.getRingtone());
                        break;
                    case "newAlarm":
                        User user=em.createQuery("Select u from User u where u.username=:username", User.class).setParameter("username", receive.getUsername()).getSingleResult();
                        ut = InitialContext.doLookup("java:comp/UserTransaction");
                        ut.begin();
                        em.joinTransaction();
                        em.createNativeQuery("INSERT INTO Alarm(period,date,idUser) VALUES (?1, ?2, ?3)").setParameter(1, 0).setParameter(2, receive.getDate()).setParameter(3, user.getIduser()).executeUpdate();
                       
                        ut.commit();
                        new Thread(){
                            public void run(){
                                Date date=new Date();
                                Date alarmDate=receive.getDate();
                                 try {
                                        sleep(alarmDate.getTime()-date.getTime());
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                SongMessage sm=new SongMessage(null, receive.getUsername(), "alarm");
                                ObjectMessage m = context.createObjectMessage(sm);
                                producer.send(ring, m);
                                System.out.println("Zazvonio alarm "+receive.getDate());
                            }
                        }.start();
                        System.out.println("Izmenjeno"+ receive.getRingtone());
                        break;
                    case "newPeriodicAlarm":
                        user=em.createQuery("Select u from User u where u.username=:username", User.class).setParameter("username", receive.getUsername()).getSingleResult();
                        ut = InitialContext.doLookup("java:comp/UserTransaction");
                        ut.begin();
                        em.joinTransaction();
                        em.createNativeQuery("INSERT INTO Alarm(period,date,idUser) VALUES (?1, ?2, ?3)").setParameter(1, receive.getPeriod()).setParameter(2, receive.getDate()).setParameter(3, user.getIduser()).executeUpdate();
                       
                        ut.commit();
                        new Thread(){
                            public void run(){
                                Date date=new Date();
                                Date alarmDate=receive.getDate();
                                try {
                                        sleep(alarmDate.getTime()-date.getTime());
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    
                                
                                SongMessage sm=new SongMessage(null, receive.getUsername(), "alarm");
                                ObjectMessage m = context.createObjectMessage(sm);
                                while(true){
                                System.out.println("Zazvonio Periodican alarm "+receive.getDate());
                                
                                producer.send(ring, m);
                                    try {
                                        sleep(receive.getPeriod()*60*1000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }.start();
                         
                        System.out.println("IzmenjenoPeriodicno "+ receive.getRingtone());
                        break;
                    case "setTaskAlarm":
                        
                        user=em.createQuery("Select u from User u where u.username=:username", User.class).setParameter("username", receive.getUsername()).getSingleResult();
                        List<Planner> planners = em.createQuery("Select plann FROM Planner plann WHERE plann.idUser=:user",Planner.class).setParameter("user", user).getResultList();
                        planners.sort(Comparator.comparing(Planner::getStartDate));
                        boolean found =false;
                        int i=0;
                        int index=-1;
                        for(Planner planner:planners){
                            if(planner.getName().equals(receive.getName())){
                                found=true;
                                index=i;
                            }
                            else
                                i++;
                        }
                        if(!found){
                            Error error=new Error("Can't find Task!");
                            break;
                        }
                        Date dat = em.createQuery("Select p.startDate from Planner p WHERE p.name=:name and p.idUser=:idUser", Date.class).setParameter("name", receive.getName()).setParameter("idUser", user).getSingleResult();
                        if(index>0){
                            Long tim=calculate(planners.get(index-1).getDestination(),planners.get(index).getDestination());
                            dat=new Date(dat.getTime()-tim);
                        }
                        Date alarm=dat;
                   ut = InitialContext.doLookup("java:comp/UserTransaction");
                        ut.begin();
                        em.joinTransaction();
                        em.createNativeQuery("INSERT INTO Alarm(period,date,idUser) VALUES (?1, ?2, ?3)").setParameter(1, 0).setParameter(2, dat).setParameter(3, user.getIduser()).executeUpdate();
                       
                        ut.commit();
                        Error error=new Error("Alarm added successfully!");
                        new Thread(){
                            public void run(){
                                Date date=new Date();
                                Date alarmDate=alarm;
                                 try {
                                        sleep(alarm.getTime()-date.getTime());
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                SongMessage sm=new SongMessage(null, receive.getUsername(), "alarm");
                                ObjectMessage m = context.createObjectMessage(sm);
                                producer.send(ring, m);
                                System.out.println("Zazvonio alarm "+receive.getDate());
                            }
                        }.start();
                        System.out.println("Izmenjeno"+ receive.getRingtone());
                        break;



                    
                        
                        
                    
               }
           } catch (Exception ex) {
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

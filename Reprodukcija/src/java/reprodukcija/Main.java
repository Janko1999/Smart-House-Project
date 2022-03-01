/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reprodukcija;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import enteties.User;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import messages.SongMessage;

/**
 *
 * @author Janko
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    @Resource(lookup="jms/__defaultConnectionFactory")
    private static ConnectionFactory connFact;
    @Resource(lookup="playSong")
    private static Queue queue;
    public static void main(String[] args) {
        
        JMSContext context = connFact.createContext();
        JMSConsumer consumer = context.createConsumer(queue);
        EntityManagerFactory emf=Persistence.createEntityManagerFactory("ReprodukcijaPU");
        EntityManager em=emf.createEntityManager();
        while(true){
            
            try {
                SongMessage msg = (SongMessage)((ObjectMessage)consumer.receive()).getObject();
                
                switch(msg.getOperation()){
                    
                    case "playSong":
                        String username=msg.getIdUser();
                        String name=msg.getName();
                        User user=em.createQuery("SELECT user FROM User user WHERE user.username=:idUser", User.class).setParameter("idUser",username).getSingleResult();
                        List<SearchResult> videos = Youtube.getVideos(name, 5);
                        ResourceId id = videos.get(0).getId();
                        String videoId = id.getVideoId();
                        String title = videos.get(0).getSnippet().getTitle();
                        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                        ut.begin();
                        em.joinTransaction();
                        em.createNativeQuery("INSERT INTO Song(name,idUrl,user) VALUES (?1, ?2, ?3)").setParameter(1,title ).setParameter(2, videoId).setParameter(3, user.getIduser()).executeUpdate();
                        ut.commit();
                        Youtube.runBrowser(name);
                        break;




                    case "alarm":
                        username=msg.getIdUser();
                         
                        user=em.createQuery("SELECT user FROM User user WHERE user.username=:idUser", User.class).setParameter("idUser",username).getSingleResult();
                        
                        Youtube.runBrowser(user.getRingtone());
                        
                        
                        break;
                    
                }
                
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
        }
    }
    
}

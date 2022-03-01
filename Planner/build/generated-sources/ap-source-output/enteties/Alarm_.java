package enteties;

import enteties.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-08-26T23:55:12")
@StaticMetamodel(Alarm.class)
public class Alarm_ { 

    public static volatile SingularAttribute<Alarm, Date> date;
    public static volatile SingularAttribute<Alarm, User> idUser;
    public static volatile SingularAttribute<Alarm, Integer> period;
    public static volatile SingularAttribute<Alarm, Integer> idalarm;

}
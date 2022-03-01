package enteties;

import enteties.Alarm;
import enteties.Planner;
import enteties.Song;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-08-26T23:55:12")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Integer> iduser;
    public static volatile SingularAttribute<User, String> password;
    public static volatile SingularAttribute<User, String> address;
    public static volatile SingularAttribute<User, String> ringtone;
    public static volatile ListAttribute<User, Alarm> alarmList;
    public static volatile ListAttribute<User, Planner> plannerList;
    public static volatile ListAttribute<User, Song> songList;
    public static volatile SingularAttribute<User, String> username;

}
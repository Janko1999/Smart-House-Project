package enteties;

import enteties.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-08-27T14:12:14")
@StaticMetamodel(Song.class)
public class Song_ { 

    public static volatile SingularAttribute<Song, String> idUrl;
    public static volatile SingularAttribute<Song, Integer> idsong;
    public static volatile SingularAttribute<Song, String> name;
    public static volatile SingularAttribute<Song, User> user;

}
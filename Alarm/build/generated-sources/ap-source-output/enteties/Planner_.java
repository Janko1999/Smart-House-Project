package enteties;

import enteties.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-08-26T23:55:15")
@StaticMetamodel(Planner.class)
public class Planner_ { 

    public static volatile SingularAttribute<Planner, Integer> duration;
    public static volatile SingularAttribute<Planner, User> idUser;
    public static volatile SingularAttribute<Planner, Integer> idplanner;
    public static volatile SingularAttribute<Planner, String> destination;
    public static volatile SingularAttribute<Planner, String> name;
    public static volatile SingularAttribute<Planner, Date> startDate;

}
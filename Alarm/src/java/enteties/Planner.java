/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enteties;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Janko
 */
@Entity
@Table(name = "planner")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Planner.findAll", query = "SELECT p FROM Planner p"),
    @NamedQuery(name = "Planner.findByIdplanner", query = "SELECT p FROM Planner p WHERE p.idplanner = :idplanner"),
    @NamedQuery(name = "Planner.findByStartDate", query = "SELECT p FROM Planner p WHERE p.startDate = :startDate"),
    @NamedQuery(name = "Planner.findByDuration", query = "SELECT p FROM Planner p WHERE p.duration = :duration"),
    @NamedQuery(name = "Planner.findByDestination", query = "SELECT p FROM Planner p WHERE p.destination = :destination"),
    @NamedQuery(name = "Planner.findByName", query = "SELECT p FROM Planner p WHERE p.name = :name")})
public class Planner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idplanner")
    private Integer idplanner;
    @Basic(optional = false)
    @NotNull
    @Column(name = "startDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duration")
    private int duration;
    @Size(max = 45)
    @Column(name = "destination")
    private String destination;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @JoinColumn(name = "idUser", referencedColumnName = "iduser")
    @ManyToOne(optional = false)
    private User idUser;

    public Planner() {
    }

    public Planner(Integer idplanner) {
        this.idplanner = idplanner;
    }

    public Planner(Integer idplanner, Date startDate, int duration, String name) {
        this.idplanner = idplanner;
        this.startDate = startDate;
        this.duration = duration;
        this.name = name;
    }

    public Integer getIdplanner() {
        return idplanner;
    }

    public void setIdplanner(Integer idplanner) {
        this.idplanner = idplanner;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getIdUser() {
        return idUser;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idplanner != null ? idplanner.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Planner)) {
            return false;
        }
        Planner other = (Planner) object;
        if ((this.idplanner == null && other.idplanner != null) || (this.idplanner != null && !this.idplanner.equals(other.idplanner))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "enteties.Planner[ idplanner=" + idplanner + " ]";
    }
    
}

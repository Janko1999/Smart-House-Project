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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Janko
 */
@Entity
@Table(name = "alarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alarm.findAll", query = "SELECT a FROM Alarm a"),
    @NamedQuery(name = "Alarm.findByIdalarm", query = "SELECT a FROM Alarm a WHERE a.idalarm = :idalarm"),
    @NamedQuery(name = "Alarm.findByPeriod", query = "SELECT a FROM Alarm a WHERE a.period = :period"),
    @NamedQuery(name = "Alarm.findByDate", query = "SELECT a FROM Alarm a WHERE a.date = :date")})
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idalarm")
    private Integer idalarm;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Period")
    private int period;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @JoinColumn(name = "idUser", referencedColumnName = "iduser")
    @ManyToOne(optional = false)
    private User idUser;

    public Alarm() {
    }

    public Alarm(Integer idalarm) {
        this.idalarm = idalarm;
    }

    public Alarm(Integer idalarm, int period, Date date) {
        this.idalarm = idalarm;
        this.period = period;
        this.date = date;
    }

    public Integer getIdalarm() {
        return idalarm;
    }

    public void setIdalarm(Integer idalarm) {
        this.idalarm = idalarm;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        hash += (idalarm != null ? idalarm.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alarm)) {
            return false;
        }
        Alarm other = (Alarm) object;
        if ((this.idalarm == null && other.idalarm != null) || (this.idalarm != null && !this.idalarm.equals(other.idalarm))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "enteties.Alarm[ idalarm=" + idalarm + " ]";
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enteties;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Janko
 */
@Entity
@Table(name = "song")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Song.findAll", query = "SELECT s FROM Song s"),
    @NamedQuery(name = "Song.findByIdsong", query = "SELECT s FROM Song s WHERE s.idsong = :idsong"),
    @NamedQuery(name = "Song.findByName", query = "SELECT s FROM Song s WHERE s.name = :name"),
    @NamedQuery(name = "Song.findByIdUrl", query = "SELECT s FROM Song s WHERE s.idUrl = :idUrl")})
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idsong")
    private Integer idsong;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "idUrl")
    private String idUrl;
    @JoinColumn(name = "user", referencedColumnName = "iduser")
    @ManyToOne(optional = false)
    private User user;

    public Song() {
    }

    public Song(Integer idsong) {
        this.idsong = idsong;
    }

    public Song(Integer idsong, String name, String idUrl) {
        this.idsong = idsong;
        this.name = name;
        this.idUrl = idUrl;
    }

    public Integer getIdsong() {
        return idsong;
    }

    public void setIdsong(Integer idsong) {
        this.idsong = idsong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUrl() {
        return idUrl;
    }

    public void setIdUrl(String idUrl) {
        this.idUrl = idUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idsong != null ? idsong.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Song)) {
            return false;
        }
        Song other = (Song) object;
        if ((this.idsong == null && other.idsong != null) || (this.idsong != null && !this.idsong.equals(other.idsong))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "enteties.Song[ idsong=" + idsong + " ]";
    }
    
}

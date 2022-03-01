/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 *
 * @author Janko
 */
public class PlannerMessage implements Serializable {
    
    private Date date;
    private String name;
    private int duration;
    private String destination;
    private String operation;
    private String username;
    private String nameChange;
    private String locationA;
    private String locationB;

    public String getLocationA() {
        return locationA;
    }

    public void setLocationA(String locationA) {
        this.locationA = locationA;
    }

    public String getLocationB() {
        return locationB;
    }

    public void setLocationB(String locationB) {
        this.locationB = locationB;
    }

    public String getNameChange() {
        return nameChange;
    }

    public void setNameChange(String nameChange) {
        this.nameChange = nameChange;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public PlannerMessage(Date date, String name, int duration, String destination, String operation, String username, String nameChange, String locationA, String locationB) {
        this.date = date;
        this.name = name;
        this.duration = duration;
        this.destination = destination;
        this.operation=operation;
        this.username=username;
        this.nameChange=nameChange;
        this.locationA=locationA;
        this.locationB=locationB;
    }

    public PlannerMessage() {
    }
    
    

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    
    
    
    
    
}

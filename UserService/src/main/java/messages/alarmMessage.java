/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Janko
 */
public class alarmMessage implements Serializable {
    
    private String username;
    private String ringtone;
    private String operation;
    private Date date;
    private int period;
    private String name;

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name=name;
    }

    public alarmMessage(String username, String ringtone, String operation, Date date, int period, String name) {
        this.operation=operation;
        this.username = username;
        this.ringtone = ringtone;
        this.date=date;
        this.period=period;
        this.name=name;
    }

    public String getUsername() {
        return username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }
    
    
}

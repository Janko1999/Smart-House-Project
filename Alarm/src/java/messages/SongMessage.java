/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.Serializable;

/**
 *
 * @author Janko
 */
public class SongMessage implements Serializable {
    
    private String name;
    private String username;
    private String operation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUser() {
        return username;
    }

    public void setIdUser(String idUser) {
        this.username = idUser;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public SongMessage(String name, String idUser, String operation) {
        this.name = name;
        this.username = idUser;
        this.operation=operation;
    }
    
    
}

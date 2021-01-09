/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmq.sensordata.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import org.bson.types.ObjectId;

@Entity
@Indexes({@Index(fields= {@Field("user")})})
public class UserPass {
    	@Id
	 private ObjectId id;
        
    public String user;
    public String password;
    public String name;
    public String emergencyNumber;

    public UserPass(){}

    public UserPass(String user, String password, String name, String emergencyNumber) {
        this.user = user;
        this.password = password;
        this.name = name;
        this.emergencyNumber = emergencyNumber;
    }
    
    public UserPass(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPass{" + "user=" + user + ", password=" + password + ", name=" + name + ", emergencyNumber=" + emergencyNumber + '}';
    }
        
}

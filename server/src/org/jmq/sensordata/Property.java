/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmq.sensordata;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity
public class Property {
    @Id
    String property;

	@Override
	public String toString() {
		return "Property [property=" + property + "]";
	}

    
}

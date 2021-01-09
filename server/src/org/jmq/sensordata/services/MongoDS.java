/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmq.sensordata.services;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

/**
 *
 * @author javie
 */
public class MongoDS {
            
   static  Datastore getMongoDataStore(String id) throws Exception{
         Datastore datastore = Morphia.createDatastore(MongoClients.create(),id+"new");
        datastore.getMapper().mapPackage("org.jmq.sensordata.model");
        datastore.ensureIndexes();
      
        return datastore;
    }
}

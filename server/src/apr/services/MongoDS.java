package apr.services;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class MongoDS {
            
   static  Datastore getMongoDataStore(String id) throws Exception{
         Datastore datastore = Morphia.createDatastore(MongoClients.create(),id+"new");
        datastore.getMapper().mapPackage("apr.model");
        datastore.ensureIndexes();
      
        return datastore;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jmq.sensordata;

import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import static dev.morphia.aggregation.experimental.expressions.AccumulatorExpressions.*;
import static dev.morphia.aggregation.experimental.expressions.Expressions.*;
import dev.morphia.aggregation.experimental.stages.Group;

import dev.morphia.aggregation.experimental.stages.Unset;
import dev.morphia.aggregation.experimental.stages.Unwind;

import dev.morphia.query.experimental.filters.Filters;

import static dev.morphia.aggregation.Group.*;
import static dev.morphia.aggregation.experimental.stages.Group.*;
import dev.morphia.query.experimental.filters.Filter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;



@Path("MyGeoServlet")
public class MyGeoServlet {
    
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() {        
        return "Hi from rest services in geo data sensor \n";
    }

    
    static long offTimeLocation=24L*60L*60L*1000L;
    
    @GET
    @Path("/insert_location/{user}/{lat}/{lon}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response inserSensorData(
            @PathParam("user") String user,
            @PathParam("lat") Float lat,
            @PathParam("lon") Float lon
            ) {
    			
                try {
                        TimeLocation data = new TimeLocation(user, System.currentTimeMillis(), lat, lon);
                       
                        Datastore ds=getMongoDataStore(user);
                        ds.save(data);
                        System.out.println("Inserted data "+data);
                        
                         List<TimeLocation> sensors=   ds
             		.find(TimeLocation.class)
             		.filter(
             				Filters.gt("timestamp", System.currentTimeMillis()-offTimeLocation)
             		)           		
       //                 .filter(
         //    				Filters.eq("property", "p1")
         //    		)
             		.iterator()
             		.toList();
                return Response.ok()
                    .entity(sensors)
                    .build();
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return Response.status(Response.Status.NOT_FOUND)
            .build();
    }    

 @GET
    @Path("/get_location_resources/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocationResources(
            @PathParam("user") String user
            ) {
    			
                try {

                        Datastore ds=getMongoDataStore(user);
                        
                         List<ResourceLocation> resources=   ds
             		.find(ResourceLocation.class)
             		
             		.iterator()
             		.toList();
                return Response.ok()
                    .entity(resources)
                    .build();
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return Response.status(Response.Status.NOT_FOUND)
            .build();
    }    
    
@GET
    @Path("/get_cash_movement/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCashMovement(
                @PathParam("user") String user
            ) {
                try {
                        Datastore ds=getMongoDataStore(user);
                        
                         List<CashMovement> resources=   ds
             		.find(CashMovement.class)
             		
             		.iterator()
             		.toList();
                return Response.ok()
                    .entity(resources)
                    .build();
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return Response.status(Response.Status.NOT_FOUND)
            .build();
    }    
    
    
@GET
    @Path("/insert_cash_movement/{user}/{money}/{concept}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCashMovement(
                @PathParam("user") String user,
                @PathParam("money") float money,
                @PathParam("concept") String concept
            ) {
                try {
                        Datastore ds=getMongoDataStore(user);
                        CashMovement data = new CashMovement(user,System.currentTimeMillis(), money, concept);
                        ds.save(data);
                         List<CashMovement> resources=   ds
             		.find(CashMovement.class)
             		
             		.iterator()
             		.toList();
                return Response.ok()
                    .entity(resources)
                    .build();
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return Response.status(Response.Status.NOT_FOUND)
            .build();
    }        
    
    @GET
    @Path("/insert_task/{user}/{name}/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertTask(
            @PathParam("user") String user, 
            @PathParam("name") String name,
            @PathParam("date") long date) {

            try {
                Datastore ds=getMongoDataStore(user);
                Task task = new Task(user,name,date);
                ds.save(task);
                 List<Task> resources=   ds
                .find(Task.class)

                .iterator()
                .toList();
                 
            return Response.ok()
                    .entity(resources)
                    .build();
            } catch (Exception e) {
                    e.printStackTrace();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    @GET
    @Path("/modify_task/{user}/{name}/{date}/{check}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyTask(
            @PathParam("user") String user, 
            @PathParam("name") String name,
            @PathParam("date") long date,
            @PathParam("check") boolean check) {

            try {
                Datastore ds=getMongoDataStore(user);
                
                Task task = getMongoDataStore(user).find(Task.class)
					.filter(Filters.eq("user", user), Filters.eq("name", name), Filters.eq("date", date)).first();
                
                task.check=check;
                ds.save(task);
                
                 List<Task> resources=ds
                .find(Task.class)

                .iterator()
                .toList();
                 
            return Response.ok()
                    .entity(resources)
                    .build();
            } catch (Exception e) {
                    e.printStackTrace();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
    }
        
        
    @GET
    @Path("/get_task/{user}/{currentDay}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTask(
            @PathParam("user") String user,
            @PathParam("currentDay") Long currentDay
            ) {
    			
                try {

                        Datastore ds=getMongoDataStore(user);
                        
                        List<Task> tasks = getMongoDataStore(user).find(Task.class)
					.filter(Filters.gte("date", currentDay), Filters.eq("user", user))
					.iterator().toList();
                return Response.ok()
                    .entity(tasks)
                    .build();
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return Response.status(Response.Status.NOT_FOUND)
            .build();
    }
    
        
        
   static  Datastore getMongoDataStore(String id) throws Exception{
         Datastore datastore = Morphia.createDatastore(MongoClients.create(),id+"new");
        datastore.getMapper().mapPackage("org.jmq.sensordata");
        datastore.ensureIndexes();
      
        return datastore;
    }
    
  
  
   static public void main(String [] args) throws Exception{
        
        String user="aurora";

         Datastore datastore = getMongoDataStore(user);
       {
        	 CashMovement data = new CashMovement("aurora",System.currentTimeMillis(), +23.98832f, " saludo ");
        	 datastore.save(data);
         }   
       
       {
        	 ResourceLocation data = new ResourceLocation("casa Rafa", 37.7797222f,-3.7943167f);
        	 datastore.save(data);
         }   
         {
                ResourceLocation data = new ResourceLocation("adetem", 37.77315f,-3.7875653f);
        	datastore.save(data);
         }
         {
        	 ResourceLocation data = new ResourceLocation("Inturjoven", 37.7735034f,-3.7972788f);
        	 datastore.save(data);
         }
        { 
                Date date = new Date("December 17, 1995 03:24:00");
        	Task data = new Task("aurora", "Matarse",date.getTime());
        	 datastore.save(data);
         } 
         
     
      
}
}
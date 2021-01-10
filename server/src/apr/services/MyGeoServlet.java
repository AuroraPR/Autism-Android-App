package apr.services;


import dev.morphia.Datastore;

import dev.morphia.query.experimental.filters.Filters;

import java.util.*;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import apr.model.CashMovement;
import apr.model.ResourceLocation;
import apr.model.Task;
import apr.model.TimeLocation;
import apr.model.security.Secured;
import static apr.services.MongoDS.getMongoDataStore;

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
    @Secured
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
    @Secured
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
    
        

    
  
  
   static public void main(String [] args) throws Exception{
        
        String user="aurora";

         Datastore datastore = getMongoDataStore(user);
       {
        	 CashMovement data = new CashMovement("aurora",System.currentTimeMillis(), +23.98832f, " saludo ");
        	 datastore.save(data);
         }   
       
       {
        	 ResourceLocation data = new ResourceLocation("casa", 37.7797222f,-3.7943167f);
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
        	Task data = new Task("aurora", "no lo sé",date.getTime());
        	 datastore.save(data);
         } 
         
     
      
}
}
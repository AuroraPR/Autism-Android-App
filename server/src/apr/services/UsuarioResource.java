package apr.services;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import apr.model.security.RestSecurityFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import apr.model.UserPass;
import static apr.services.MongoDS.getMongoDataStore;

@Path("/usuario")
public class UsuarioResource {
    
    static public String server_salt="isla";
 
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(@QueryParam("user") String user,
    		@QueryParam("password") String password) {
        System.out.println("Done");
        try {
 
            password=getHash(server_salt+password);
            boolean ok= authenticate(user, password);
        	if(!ok)
                    return Response.status(Response.Status.UNAUTHORIZED).build();
            // Si todo es correcto, generamos el token
            String token = issueToken(user);
 
            // Devolvemos el token en la cabecera "Authorization". 
            // Se podría devolver también en la respuesta directamente.
            System.out.println("ok");
            UserPass userFound = getMongoDataStore(user).find(UserPass.class)
					.filter(Filters.eq("user", user), Filters.eq("password", password)).first();
           return Response.ok().entity(new UserPass(user,token, userFound.name, userFound.emergencyNumber)).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
 
        } catch (Exception e) {
             System.out.println("ko");
        //    e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(@QueryParam("user") String user,
                            @QueryParam("password") String password,
                            @QueryParam("name") String name,
                            @QueryParam("emergencyNumber") String emergencyNumber){
        System.out.println("Adding user");
        try {
 
          password=getHash(server_salt+password);
          Datastore datastore = getMongoDataStore(user);
          datastore.save(new UserPass(user,password,name,emergencyNumber));
           return Response.ok().entity(new UserPass(user,"",name,emergencyNumber)).build();
 
        } catch (Exception e) {
             System.out.println("ko");
        //    e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    private String issueToken(String login) {
    	//Calculamos la fecha de expiración del token
    	Date issueDate = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(issueDate);
    	calendar.add(Calendar.MINUTE, 60);
        Date expireDate = calendar.getTime();
        
		//Creamos el token
        String jwtToken = Jwts.builder()
        		.claim("roles", login)
                .setSubject(login)
               .setIssuedAt(issueDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, RestSecurityFilter.KEY)
                .compact();
        return jwtToken;
    }
    
    private boolean authenticate(String user, String password) throws  Exception{
                            List<UserPass> users = getMongoDataStore(user).find(UserPass.class)
					.filter(Filters.eq("user", user), Filters.eq("password", password))
					.iterator().toList();
                            System.out.println("Users:"+users);
                           return !users.isEmpty();
    
    }
    
    private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
}
    
    
    private String getHash(String label) throws NoSuchAlgorithmException{
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return bytesToHex(digest.digest(
          label.getBytes(StandardCharsets.UTF_8)));


    }
    
}

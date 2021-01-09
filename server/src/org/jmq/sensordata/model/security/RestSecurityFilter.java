package org.jmq.sensordata.model.security;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.jmq.sensordata.model.security.Secured;
import org.jmq.sensordata.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class RestSecurityFilter implements ContainerRequestFilter {
	
	public static final Key KEY = MacProvider.generateKey();
 
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
 
        // Recupera la cabecera HTTP Authorization de la petici�n
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        String path=requestContext.getUriInfo().getRequestUri().toURL().toString();
        System.out.println("Authorization:"+authorizationHeader);
        System.out.println("path:"+path);
        try {
        	// Extrae el token de la cabecera
            String token = authorizationHeader.substring("Bearer".length()).trim();
            System.out.println("Token:"+authorizationHeader);
 
            // Valida el token utilizando la cadena secreta
            Jws<Claims> claims = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
            
            //Creamos el usuario a partir de la informaci�n del token
            User usuario = new User();
            usuario.setUsername(claims.getBody().getSubject());
            String roles = (String) claims.getBody().get("roles");
            System.out.println("Roles:"+roles);
            usuario.setRoles(Arrays.asList(roles.split(",")));
            
            // Creamos el SecurityContext
            MyApplicationSecurityContext secContext = new MyApplicationSecurityContext(usuario, requestContext.getSecurityContext().isSecure());
            
            //Seteamos el contexto de seguridad
            requestContext.setSecurityContext(secContext);
            
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}

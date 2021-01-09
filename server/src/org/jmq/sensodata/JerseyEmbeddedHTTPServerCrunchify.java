package org.jmq.sensodata;

import org.jmq.sensordata.model.security.Application;
import org.jmq.sensordata.services.UsuarioResource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;


import javax.ws.rs.core.UriBuilder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

@SuppressWarnings("restriction")
public class JerseyEmbeddedHTTPServerCrunchify {
 

	    static public void main(String [] args) throws Exception {
	    	
	    	
	        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8092).build();
	        ResourceConfig config = new Application();
	        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
	        //http://localhost:8092/usuario/login?user=test&password=test
                //http://localhost:8092/pelicula/1
	        System.out.println("Server is active");
	        server.start();
	    }


	
}
package org.jmq.sensordata;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;


import javax.ws.rs.core.UriBuilder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

@SuppressWarnings("restriction")
public class JerseyEmbeddedHTTPServerCrunchify {
 

	    static public void main(String [] args) throws Exception {
	    	
	    	
	        URI baseUri = UriBuilder.fromUri("http://192.168.1.23/").port(8092).build();
	        ResourceConfig config = new ResourceConfig(MyGeoServlet.class);
	        config.register(JacksonFeature.class);
	        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
	        
	        System.out.println("Server is active");
	        server.start();
	    }

    /**        
            static public void main2(String [] args) throws Exception{
Server server = new Server(8092);
  ServletContextHandler context=new ServletContextHandler(ServletContextHandler.SESSIONS);
  context.setContextPath("/");
  DefaultServlet defaultServlet = new DefaultServlet();
  ServletHolder holderPwd = new ServletHolder("default", defaultServlet);
context.addServlet(holderPwd, "/*");
  server.setHandler(context);
  server.start();
  server.join();
            }

	*/
}
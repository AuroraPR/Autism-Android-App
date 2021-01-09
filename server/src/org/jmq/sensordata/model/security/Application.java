package org.jmq.sensordata.model.security;


import org.jmq.sensordata.services.UsuarioResource;
import org.jmq.sensordata.model.security.Secured;
import org.jmq.sensordata.model.User;
import org.jmq.sensordata.model.security.MyApplicationSecurityContext;
import org.jmq.sensordata.model.security.RestSecurityFilter;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jmq.sensordata.services.MyGeoServlet;


@ApplicationPath("rest")
public class Application extends ResourceConfig {
    public Application() {
        super( MyGeoServlet.class, User.class, UsuarioResource.class, RestSecurityFilter.class, MyApplicationSecurityContext.class, Secured.class);

        register(JacksonFeature.class);
        register(RolesAllowedDynamicFeature.class);
    }
}

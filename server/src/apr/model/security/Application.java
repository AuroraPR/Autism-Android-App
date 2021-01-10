package apr.model.security;


import apr.services.UsuarioResource;
import apr.model.User;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import apr.services.MyGeoServlet;


@ApplicationPath("rest")
public class Application extends ResourceConfig {
    public Application() {
        super( MyGeoServlet.class, User.class, UsuarioResource.class, RestSecurityFilter.class, MyApplicationSecurityContext.class, Secured.class);

        register(JacksonFeature.class);
        register(RolesAllowedDynamicFeature.class);
    }
}

package com.github.gv2011.jerseyrestex;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;


public class Main {

	public static void main(String[] args) throws Exception {
		final Server server = new Server(8080);

        final ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
                
        ctx.setContextPath("/");
        server.setHandler(ctx);

        final ServletHolder rest = ctx.addServlet(ServletContainer.class, "/*");
        rest.setInitOrder(1);
        //rest.setInitParameter(ServerProperties.PROVIDER_PACKAGES, RestApi.class.getPackageName());
        rest.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JaxrsApplication.class.getName());
        rest.setInitParameter(ServerProperties.WADL_FEATURE_DISABLE, Boolean.TRUE.toString());
        

        final ObjectMapper jsonMapper = Json.mapper();
		ctx.addServlet(
    		new ServletHolder(
	    		new OpenApiServlet(
	        		()->new OpenApiBuilder().buildApi(), jsonMapper, Yaml.mapper()
	    		)
    		),
    		"/api/*"
		);
        
		//ctx.addServlet(new ServletHolder(new io.swagger.v3.jaxrs2.integration.OpenApiServlet()), "/api/*");
        
        final ServletHolder swaggerUi = ctx.addServlet(SwaggerUiServlet.class, "/swagger-ui/*");
        swaggerUi.setInitParameter(SwaggerUiServlet.SWAGGER_UI_VERSION, "3.47.1");
        swaggerUi.setInitParameter(SwaggerUiServlet.API_URL, "http://localhost:8080/api");
        swaggerUi.setInitParameter(SwaggerUiServlet.REQUEST_PREFIX,"/swagger-ui/");
        
        //final ServletHolder webjars = ctx.addServlet(WebjarsServlet.class, "/webjars/*");

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

}

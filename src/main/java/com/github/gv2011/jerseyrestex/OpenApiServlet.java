package com.github.gv2011.jerseyrestex;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.models.OpenAPI;

public class OpenApiServlet extends HttpServlet {

	private static final long serialVersionUID = 5357508871304173698L;

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_YAML = "application/yaml";
    public static final String ACCEPT_HEADER = "Accept";
    
    private final Supplier<OpenAPI> openAPISupplier;
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;
    
    

    public OpenApiServlet(Supplier<OpenAPI> openAPISupplier, ObjectMapper jsonMapper, ObjectMapper yamlMapper) {
		this.openAPISupplier = openAPISupplier;
		this.jsonMapper = jsonMapper;
		this.yamlMapper = yamlMapper;
	}



	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    	final OpenAPI oas = openAPISupplier.get();

    	boolean yaml = 
		   (
	    		Optional.ofNullable(req.getHeader(ACCEPT_HEADER)).orElse("")
	    		.toLowerCase(Locale.ROOT)
	    		.contains(APPLICATION_YAML)
		   ) ||  (
			   req.getRequestURL().toString().toLowerCase(Locale.ROOT).endsWith("yaml")
		   )
	    ;
    	
//    	yaml = false;
    	
    	resp.setStatus(200);

        resp.setContentType(yaml ? APPLICATION_YAML : APPLICATION_JSON);
        try (PrintWriter pw = resp.getWriter()) {
            pw.write(
        		(yaml ? yamlMapper : jsonMapper)
        		.writer(new DefaultPrettyPrinter())
        		.writeValueAsString(oas)
    		);
        }
    }
}

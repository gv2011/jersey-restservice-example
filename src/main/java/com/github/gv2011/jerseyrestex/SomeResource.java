package com.github.gv2011.jerseyrestex;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("res")
public class SomeResource {
	
	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getText() {
        return "Some text.";
    }
	
}

package com.github.gv2011.jerseyrestex;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.gv2011.jerseyrestex.model.Model2;

import io.swagger.v3.oas.annotations.media.Schema;

@Path("/rest2")
@Produces(MediaType.APPLICATION_JSON)
public class RestApi3 {

	
	@GET
	@Path("/model2")
	@Schema(required=true)
	public Model2 model2() {
		return new Model2() {
			@Override public String str() {return "lulu";}
		};
	}
	
}

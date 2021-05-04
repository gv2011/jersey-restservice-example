package com.github.gv2011.jerseyrestex;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.collect.ImmutableSet;

@Path("/rest2")
@Produces(MediaType.APPLICATION_JSON)
public class RestApi2 {

	
	@GET
	@Path("/mset")
	public Set<String> mset() {
		return ImmutableSet.of("a","b");
	}
	
	@GET
	@Path("/iset")
	public ImmutableSet<String> iset() {
		return ImmutableSet.of("a","b");
	}
	
}

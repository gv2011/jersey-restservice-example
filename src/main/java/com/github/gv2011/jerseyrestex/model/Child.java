package com.github.gv2011.jerseyrestex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Child {
	
	@JsonProperty
	String name();
	
	@JsonProperty
	long size();

}

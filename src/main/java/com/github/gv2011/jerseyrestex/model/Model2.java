package com.github.gv2011.jerseyrestex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Model2 {
	
	@JsonProperty(required=true)
	String str();

}

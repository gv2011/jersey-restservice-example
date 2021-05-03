package com.github.gv2011.jerseyrestex.model;

import com.fasterxml.jackson.annotation.JsonValue;

public interface Elementary {
	
	@JsonValue
	Object value();
	
}

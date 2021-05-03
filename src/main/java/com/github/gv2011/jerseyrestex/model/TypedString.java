package com.github.gv2011.jerseyrestex.model;

import com.fasterxml.jackson.annotation.JsonValue;

public interface TypedString<T extends TypedString<T>>{
	
	@JsonValue
	String toString();

}

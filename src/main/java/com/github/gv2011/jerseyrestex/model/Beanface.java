package com.github.gv2011.jerseyrestex.model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import io.swagger.v3.oas.annotations.media.Schema;

public interface Beanface {
	
	@JsonProperty
	String someString();
	
	@JsonProperty
	@Schema(format="colour", example="green")
	Colour typedString();

	@JsonProperty
	Optional<Beanface> optional();

	@JsonProperty
	@Schema(example="2021-05-02T15:55:06.000100")
	LocalDateTime time();

	@JsonProperty
	Elementary elementary();

	@JsonProperty
	Elementary anotherElementary();

	@JsonProperty
	Child child();
	
	@JsonProperty
	Set<String> mutableSet();
	
	@JsonProperty
	ImmutableSet<String> immutableSet();
	
	

}

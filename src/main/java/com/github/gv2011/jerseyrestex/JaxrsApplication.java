package com.github.gv2011.jerseyrestex;

import java.util.Set;

import javax.ws.rs.core.Application;

import com.google.common.collect.ImmutableSet;

public class JaxrsApplication extends Application{
	
	static {
		
	}

	private static final Set<Class<?>> CLASSES = ImmutableSet.of(
		ObjectMapperFactory.class, RestApi.class
	);

	@Override
	public Set<Class<?>> getClasses() {
		return CLASSES;
	}

}

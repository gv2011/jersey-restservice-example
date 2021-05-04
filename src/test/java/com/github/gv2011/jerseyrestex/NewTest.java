package com.github.gv2011.jerseyrestex;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.gv2011.jerseyrestex.model.Model2;
import com.google.common.collect.ImmutableSet;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

class NewTest {

	@Test
	void test() {
		OpenApiBuilder.configureDefaultModelConverter();
		SwaggerConfiguration conf = new SwaggerConfiguration();
		Reader reader = new Reader(conf);
		OpenAPI openAPI = reader.read(RestApi2.class);
		System.out.println(Json.pretty(openAPI));
	}
	
	@Test
	void test4() {
		OpenApiBuilder.configureDefaultModelConverter();
		SwaggerConfiguration conf = new SwaggerConfiguration();
		Reader reader = new Reader(conf);
		OpenAPI openAPI = reader.read(RestApi3.class);
		System.out.println(Json.pretty(openAPI));
	}
	
	@Test
	void test5() {
		JavaType jt = TypeFactory.defaultInstance().constructType(Model2.class);
		SerializationConfig serializationConfig = ObjectMapperFactory.createObjectMapper().getSerializationConfig();
		BeanDescription beanDesc = serializationConfig.introspect(jt);
		BeanPropertyDefinition prop = beanDesc.findProperties().get(0);
		System.out.println(prop.getClass());
		System.out.println(prop.isRequired());
	}
	
	@Test
	void test2() throws Exception {
		Type returnType = getClass().getMethod("iset").getGenericReturnType();
		JavaType jt = TypeFactory.defaultInstance().constructType(returnType);
		BeanDescription beanDesc = ObjectMapperFactory.createObjectMapper().getSerializationConfig().introspect(jt);
		List<BeanPropertyDefinition> props = beanDesc.findProperties();
		System.out.println(props.size());
	}
	
	@Test
	void test3() throws Exception {
		Type returnType = getClass().getMethod("mset").getGenericReturnType();
		JavaType jt = TypeFactory.defaultInstance().constructType(returnType);
		SerializationConfig serializationConfig = ObjectMapperFactory.createObjectMapper().getSerializationConfig();
		BeanDescription beanDesc = serializationConfig.introspect(jt);
		System.out.println(beanDesc.findProperties());
	}
	
	public ImmutableSet<String> iset(){
		return null;
	}
	public Set<String> mset(){
		return null;
	}
	
}

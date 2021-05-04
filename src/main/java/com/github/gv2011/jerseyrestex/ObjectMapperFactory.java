package com.github.gv2011.jerseyrestex;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;


@Provider
public final class ObjectMapperFactory implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

    public ObjectMapperFactory() {
        this.mapper = createObjectMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
    	assert type==ObjectMapper.class;
        return mapper;
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        configureMapper(mapper);
        return mapper;
    }

	public static void configureMapper(ObjectMapper mapper) {
		mapper.setConfig(mapper.getSerializationConfig().with(new PatchedClassIntrospector()));
		mapper.setSerializationInclusion(Include.NON_ABSENT);
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		mapper.registerModule(module);
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new GuavaModule());
	}
    
    private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

		@Override
		public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException {
			gen.writeString(value.toString());	
		}
	}


}


package com.github.gv2011.jerseyrestex;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Optional;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.gv2011.jerseyrestex.model.Elementary;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

final class PatchedModelResolver extends ModelResolver{
	
	private static final ImmutableSet<Class<?>> GUAVA_COLLECTION_CLASSES = ImmutableSet.of(
		ImmutableSet.class, ImmutableList.class, ImmutableMap.class
	);

	PatchedModelResolver(ObjectMapper mapper) {
		super(mapper);
	}
	
	@Override
	public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
		final Optional<Class<?>> clazz = type.getType() instanceof SimpleType
			?	Optional.ofNullable(((SimpleType)type.getType()).getRawClass())
			:	Optional.empty();
		if(clazz.equals(Optional.of(Elementary.class))) {
			return elementarySchema();
		}
		else if(clazz.equals(Optional.of(LocalDateTime.class))) {
			return localDateTimeSchema();
		}
		else {
			return super.resolve(type, context, chain);
		}
	}
	
    protected String _typeName(JavaType type, BeanDescription beanDesc) {
        if(type.getRawClass().equals(ImmutableSet.class)) return null;
        else return super._typeName(type, beanDesc);
    }
    
    static boolean isGuavaCollection(JavaType type) {
    	return GUAVA_COLLECTION_CLASSES.contains(type.getRawClass());
    }
    
	private static Schema<?> elementarySchema() {
		Schema<?> stringSchema = new StringSchema();
		Schema<?> booleanSchema = new BooleanSchema();
		ComposedSchema composedSchema = new ComposedSchema();
		composedSchema.setOneOf(ImmutableList.of(stringSchema, booleanSchema));
		return composedSchema;
	}

	private static Schema<?> localDateTimeSchema() {
		StringSchema localDateTimeSchema = new StringSchema();
		localDateTimeSchema.setFormat("local-date-time");
		localDateTimeSchema.setExample("2021-05-02T15:55:06.001");
		return localDateTimeSchema;
	}

}

package com.github.gv2011.jerseyrestex;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.gv2011.jerseyrestex.model.Elementary;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

public final class OpenApiBuilder {
	
	private static final AtomicBoolean CONFIGURED;
	
	static {
		CONFIGURED = new AtomicBoolean();
		configureDefaultModelConverter();
	}
	
	public static void configureDefaultModelConverter() {
		if(!CONFIGURED.getAndSet(true)) {
			ObjectMapperFactory.configureMapper(Json.mapper());
			ModelConverters.getInstance().addConverter(new PatchedModelResolver(Json.mapper()));
//			ModelConverters converters = ModelConverters.getInstance();
//			List<ModelConverter> conv = converters.getConverters();
//			assert conv.size()==1;
//			ModelConverter jacksonConverter = conv.get(0);
//			converters.removeConverter(jacksonConverter);
//			assert converters.getConverters().isEmpty();
//			converters.addConverter(new ModelResolver(ObjectMapperFactory.createObjectMapper()));
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public OpenAPI buildApi(){
		GenericOpenApiContextBuilder b = new JaxrsOpenApiContextBuilder();
		b.setResourceClasses(ImmutableSet.of(RestApi.class.getName()));
		OpenApiContext openApiCtx;
		try {
			openApiCtx = b.buildContext(true);
		} catch (OpenApiConfigurationException e) {
			throw new RuntimeException(e);
		}
		OpenAPI api = openApiCtx.read();
		Info info = new Info();
		info.setTitle("title1");
		info.setVersion("0.0.1");
		api.setInfo(info);
		return api;
	}

	private static ModelConverter modelConverter(ModelConverter jacksonConverter) {
		return new ModelConverter() {
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
					return jacksonConverter.resolve(type, context, chain);
				}
			}
		};
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

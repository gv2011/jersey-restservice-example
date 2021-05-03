package com.github.gv2011.jerseyrestex;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.gv2011.jerseyrestex.model.Beanface;
import com.google.common.collect.ImmutableList;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

class ValidationTest {

	@Test
	void testString() throws Exception {
		Schema<?> schema = new StringSchema();
		JsonNode schemaJson = Json.mapper().convertValue(schema, JsonNode.class);
		System.out.println(Json.pretty(schemaJson));
		SchemaValidator schemaValidator = new SchemaValidator(null, schemaJson);
		schemaValidator.validate(new TextNode("text"));
		try {
			schemaValidator.validate(BooleanNode.FALSE);
			fail();
		} catch (ValidationException e) {
			//expected
		}
	}


	@Test
	void testOneOf() throws Exception {
		Schema<?> stringSchema = new StringSchema();
		Schema<?> booleanSchema = new BooleanSchema();
		ComposedSchema composedSchema = new ComposedSchema();
		composedSchema.setOneOf(ImmutableList.of(stringSchema, booleanSchema));
		
		JsonNode schemaJson = Json.mapper().convertValue(composedSchema, JsonNode.class);
		SchemaValidator schemaValidator = new SchemaValidator(null, schemaJson);
		schemaValidator.validate(new TextNode("text"));
		schemaValidator.validate(BooleanNode.FALSE);
		try {
			schemaValidator.validate(new LongNode(7L));
			fail();
		} catch (ValidationException e) {
			//expected
		}
	}

	@Test
	@Disabled //needs context
	void testExample() throws Exception {
		Schema<?> schema = new OpenApiBuilder().buildApi().getComponents().getSchemas().get(Beanface.class.getSimpleName());
		JsonNode schemaJson = Json.mapper().convertValue(schema, JsonNode.class);
		SchemaValidator schemaValidator = new SchemaValidator(null, schemaJson);
		schemaValidator.validate(Json.mapper().convertValue(RestApi.getExample(), JsonNode.class));
	}

}

package com.github.gv2011.jerseyrestex;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openapi4j.schema.validator.v3.SchemaValidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;


class SchemaTest {

	@Test
	@SuppressWarnings("rawtypes")
	void testLegacyBeanSchema() throws OpenApiConfigurationException {
		OpenAPI api = readApi();
		Schema legacyBean = api.getComponents().getSchemas().get("LegacyBean");
		Map props = legacyBean.getProperties();
		assertThat(props.size(), is(2));
		
		Schema s = (Schema) props.get("someString");
		assertThat(s.getType(), is("string"));
		
		Schema o = (Schema) props.get("other");
		assertThat(o.get$ref(), is("#/components/schemas/LegacyBean"));
	}

	@Test
	@SuppressWarnings("rawtypes")
	void testBeanfaceSchema() throws OpenApiConfigurationException {
		OpenAPI api = readApi();
		Schema beanface = api.getComponents().getSchemas().get("Beanface");
		Map props = beanface.getProperties();
		assertThat(props.size(), is(5));
		{	Schema s = (Schema) props.get("someString");
			assertThat(s.getType(), is("string"));
		}
		{	Schema o = (Schema) props.get("other");
			assertThat(o.get$ref(), is("#/components/schemas/Beanface"));
		}
		{	Schema t = (Schema) props.get("typedString");
			assertThat(t.getType(), is("string"));
			assertThat(t.getFormat(), is("colour"));
		}
		{	Schema s = (Schema) props.get("optional");
			assertThat(s.get$ref(), is("#/components/schemas/Beanface"));
		}
		{	Schema s = (Schema) props.get("time");
			assertThat(s.getType(), is("string"));
			assertThat(s.getFormat(), is("date-time"));
		}
//		{	Schema s = (Schema) props.get("elementary");
////			System.out.println(api.getComponents().getSchemas().get("Elementary"));
//			System.out.println(Json.pretty(api));
//			assertThat(s.getType(), is("string"));
//			assertThat(s.getFormat(), is("date-time"));
//		}
	}
	
	@Test
	void testValidation() throws Exception {
//		OpenApi3 api = new OpenApi3Parser().parse(null, false)
		JsonNode schemaNode = new ObjectMapper().valueToTree(readApi());
		JsonNode contentNode = new TextNode("text");
		SchemaValidator schemaValidator = new SchemaValidator(null, schemaNode);
		schemaValidator.validate(contentNode);
	}

	@Test
	void printSchema() throws Exception {
		System.out.println(
			Json.pretty(readApi())
		)
		;
	}

	private static OpenAPI readApi() throws OpenApiConfigurationException {
		return new OpenApiBuilder().buildApi();
	}

}

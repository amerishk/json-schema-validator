package uk.co.o2.json.schema;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import uk.co.o2.json.constants.Constants;
import uk.co.o2.json.constants.MessageConstants;
import uk.co.o2.json.message.Messages;
import uk.co.o2.json.schema.ObjectSchema.Property;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The Class SchemaCompiler.
 */
class SchemaCompiler {
	
	/** The cache. */
	private final SchemaPassThroughCache cache;
	
	/** The json factory. */
	private final JsonFactory jsonFactory;
	
	/** The schemas to compile. */
	private final Queue<ProcessingEntry> schemasToCompile = new LinkedList<>();
	
	/** The messages. */
	private Messages messages;
	
	/**
	 * Instantiates a new schema compiler.
	 * 
	 * @param cache the cache
	 * @param jsonFactory the json factory
	 */
	public SchemaCompiler(SchemaPassThroughCache cache, JsonFactory jsonFactory,Messages messages) {
		this.cache = cache;
		this.jsonFactory = jsonFactory;
		this.messages = messages;
	}
	
	/**
	 * Parses the.
	 * 
	 * @param schemaLocation the schema location
	 * @return the json schema
	 */
	public JsonSchema parse(
	        URL schemaLocation) {
		
		List<ProcessedSchemaEntry> compiledSchemasToRegister = new ArrayList<>();
		
		scheduleSchemaForProcessing(schemaLocation);
		
		ProcessingEntry entry = schemasToCompile.peek();
		while (entry != null) {
			JsonSchema compiledSchema = parse(entry.rawSchema, entry.schemaLocation);
			compiledSchemasToRegister.add(new ProcessedSchemaEntry(entry.schemaLocation, compiledSchema));
			
			schemasToCompile.poll();
			entry = schemasToCompile.peek();
		}
		
		for (ProcessedSchemaEntry schemaToRegister : compiledSchemasToRegister) {
			cache.registerSchema(schemaToRegister.schemaLocation, schemaToRegister.compiledSchema);
		}
		
		return cache.getSchema(schemaLocation);
	}
	
	/**
	 * Schedule schema for processing.
	 * 
	 * @param schemaLocation the schema location
	 */
	private void scheduleSchemaForProcessing(
	        URL schemaLocation) {
		if (cache.hasSchema(schemaLocation)) {
			return; // schema has already been compiled before, or on another thread
		}
		
		for (ProcessingEntry it : schemasToCompile) {
			if (it.schemaLocation.toString().equals(schemaLocation.toString())) {
				return; // schema is already scheduled for compilation
			}
		}
		
		try {
			JsonParser parser = jsonFactory.createJsonParser(schemaLocation);
			try {
				JsonNode rawSchema = parser.readValueAsTree();
				schemasToCompile.add(new ProcessingEntry(schemaLocation, rawSchema));
			} finally {
				parser.close();
			}
		} catch (JsonParseException jpe) {
			throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_SCHEMA_AT_LOCATION) + schemaLocation.toString() + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_CONTAINS_INVALID_JSON), jpe);
		} catch (IOException ioe) {
			throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_SCHEMA_COULD_NOT_RETRIEVE) + schemaLocation.toString(), ioe);
		}
	}
	
	/**
	 * Parses the.
	 * 
	 * @param rawSchema the raw schema
	 * @param currentSchemaLocation the current schema location
	 * @return the json schema
	 */
	private JsonSchema parse(
	        JsonNode rawSchema,
	        URL currentSchemaLocation) {
		if (!rawSchema.isObject()) {
			throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_SCHEMA_MUST_BE_OBJECT));
		}
		
		JsonNode ref = rawSchema.get(Constants.KEY_REF);
		if (ref != null) {
			URL referencedSchemaLocation;
			try {
				referencedSchemaLocation = new URL(currentSchemaLocation, ref.textValue());
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_SCHEMA_REFERENCE_MALFORMED), e);
			}
			scheduleSchemaForProcessing(referencedSchemaLocation);
			return new SchemaReference(cache, referencedSchemaLocation);
		}
		
		String type = rawSchema.get(Constants.KEY_TYPE).asText();
		if (isSimpleTypeSchema(type)) {
			return parseSimpleTypeSchema(rawSchema);
		} else if (isObjectSchema(type)) {
			return parseObjectSchema(rawSchema, currentSchemaLocation);
		} else if (isArraySchema(type)) {
			return parseArraySchema(rawSchema, currentSchemaLocation);
		}
		throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_SCHEMA_ILLEGAL_TYPE) + type);
	}
	
	/**
	 * Checks if is array schema.
	 * 
	 * @param type the type
	 * @return true, if is array schema
	 */
	private boolean isArraySchema(
	        String type) {
		return Constants.KEY_ARRAY.equals(type);
	}
	
	/**
	 * Checks if is object schema.
	 * 
	 * @param type the type
	 * @return true, if is object schema
	 */
	private boolean isObjectSchema(
	        String type) {
		return Constants.KEY_OBJECT.equals(type);
	}
	
	/**
	 * Checks if is simple type schema.
	 * 
	 * @param type the type
	 * @return true, if is simple type schema
	 */
	private boolean isSimpleTypeSchema(
	        String type) {
		for (SimpleType it : SimpleType.values()) {
			if (it.name().equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Parses the simple type schema.
	 * 
	 * @param rawSchema the raw schema
	 * @return the simple type schema
	 */
	private SimpleTypeSchema parseSimpleTypeSchema(
	        JsonNode rawSchema) {
		SimpleTypeSchema result = new SimpleTypeSchema(this.messages);
		result.setType(SimpleType.valueOf(rawSchema.get(Constants.KEY_TYPE).asText().toUpperCase()));
		
		JsonNode pattern = rawSchema.get(Constants.KEY_PATTERN);
		if (pattern != null) {
			result.setPattern(Pattern.compile(pattern.textValue()));
		}
		
		JsonNode minLength = rawSchema.get(Constants.KEY_MIN_LENGTH);
		if (minLength != null) {
			result.setMinLength(minLength.intValue());
		}
		
		JsonNode maxLength = rawSchema.get(Constants.KEY_MAX_LENGTH);
		if (maxLength != null) {
			result.setMaxLength(maxLength.intValue());
		}
		
		JsonNode minimum = rawSchema.get(Constants.KEY_MINIMUM);
		if (minimum != null) {
			result.setMinimum(minimum.decimalValue());
		}
		
		JsonNode maximum = rawSchema.get(Constants.KEY_MAXIMUM);
		if (maximum != null) {
			result.setMaximum(maximum.decimalValue());
		}
		
		JsonNode exclusiveMinimum = rawSchema.get(Constants.KEY_EXCLUSIVE_MINIMUM);
		if (exclusiveMinimum != null) {
			result.setExclusiveMinimum(exclusiveMinimum.booleanValue());
		}
		
		JsonNode exclusiveMaximum = rawSchema.get(Constants.KEY_EXCLUSIVE_MAXIMUM);
		if (exclusiveMaximum != null) {
			result.setExclusiveMaximum(exclusiveMaximum.booleanValue());
		}
		
		JsonNode enumeration = rawSchema.get(Constants.KEY_ENUMERATION);
		if (enumeration != null) {
			List<JsonNode> enumerationValues = new ArrayList<>();
			for (JsonNode node : enumeration) {
				enumerationValues.add(node);
			}
			result.setEnumeration(enumerationValues);
		}
		
		JsonNode format = rawSchema.get(Constants.KEY_FORMAT);
		if (format != null) {
			result.setFormat(format.textValue());
		}
		return result;
	}
	
	/**
	 * Parses the array schema.
	 * 
	 * @param rawSchema the raw schema
	 * @param schemaLocation the schema location
	 * @return the array schema
	 */
	private ArraySchema parseArraySchema(
	        JsonNode rawSchema,
	        URL schemaLocation) {
		ArraySchema result = new ArraySchema(this.messages);
		JsonNode rawItems = rawSchema.get(Constants.KEY_ITEMS);
		if (rawItems != null) {
			result.setItems(parse(rawItems, schemaLocation));
		}
		JsonNode rawMinItems = rawSchema.get(Constants.KEY_MIN_ITEMS);
		if (rawMinItems != null) {
			result.setMinItems(rawMinItems.intValue());
		}
		JsonNode rawMaxItems = rawSchema.get(Constants.KEY_MAX_ITEMS);
		if (rawMaxItems != null) {
			result.setMaxItems(rawMaxItems.intValue());
		}
		return result;
	}
	
	/**
	 * Parses the object schema.
	 * 
	 * @param rawSchema the raw schema
	 * @param schemaLocation the schema location
	 * @return the object schema
	 */
	private ObjectSchema parseObjectSchema(
	        JsonNode rawSchema,
	        URL schemaLocation) {
		ObjectSchema result = new ObjectSchema(this.messages);
		configureAdditionalPropertiesForObjectSchema(rawSchema.get(Constants.KEY_ADDITIONAL_PROPERTIES), result, schemaLocation);
		configurePropertiesForObjectSchema(rawSchema.get(Constants.KEY_PROPERTIES), result, schemaLocation);
		return result;
	}
	
	/**
	 * Configure additional properties for object schema.
	 * 
	 * @param additionalProperties the additional properties
	 * @param schema the schema
	 * @param schemaLocation the schema location
	 */
	private void configureAdditionalPropertiesForObjectSchema(
	        JsonNode additionalProperties,
	        ObjectSchema schema,
	        URL schemaLocation) {
		if (additionalProperties == null) {
			return;
		}
		if (additionalProperties.isBoolean()) {
			JsonSchema additionalPropertiesSchema = additionalProperties.booleanValue() ? ObjectSchema.ALLOW_ALL_ADDITIONAL_PROPERTIES : ObjectSchema.FORBID_ANY_ADDITIONAL_PROPERTIES;
			schema.setAdditionalProperties(additionalPropertiesSchema);
		} else {
			schema.setAdditionalProperties(parse(additionalProperties, schemaLocation));
		}
	}
	
	/**
	 * Configure properties for object schema.
	 * 
	 * @param rawProperties the raw properties
	 * @param schema the schema
	 * @param schemaLocation the schema location
	 */
	private void configurePropertiesForObjectSchema(
	        JsonNode rawProperties,
	        ObjectSchema schema,
	        URL schemaLocation) {
		if (rawProperties == null) {
			return;
		}
		
		for (Iterator<String> iterator = rawProperties.fieldNames(); iterator.hasNext();) {
			String fieldName = iterator.next();
			
			Property property = new Property();
			property.setName(fieldName);
			
			JsonNode nestedSchema = rawProperties.get(fieldName);
			property.setNestedSchema(parse(nestedSchema, schemaLocation));
			
			JsonNode required = nestedSchema.get(Constants.KEY_REQUIRED);
			if (required != null) {
				property.setRequired(required.booleanValue());
			}
			
			schema.getProperties().add(property);
		}
	}
	
	/**
	 * The Class ProcessingEntry.
	 */
	private static class ProcessingEntry {
		
		/** The schema location. */
		final URL schemaLocation;
		
		/** The raw schema. */
		final JsonNode rawSchema;
		
		/**
		 * Instantiates a new processing entry.
		 * 
		 * @param schemaLocation the schema location
		 * @param rawSchema the raw schema
		 */
		ProcessingEntry(URL schemaLocation, JsonNode rawSchema) {
			this.schemaLocation = schemaLocation;
			this.rawSchema = rawSchema;
		}
	}
	
	/**
	 * The Class ProcessedSchemaEntry.
	 */
	private static class ProcessedSchemaEntry {
		
		/** The schema location. */
		final URL schemaLocation;
		
		/** The compiled schema. */
		final JsonSchema compiledSchema;
		
		/**
		 * Instantiates a new processed schema entry.
		 * 
		 * @param schemaLocation the schema location
		 * @param compiledSchema the compiled schema
		 */
		ProcessedSchemaEntry(URL schemaLocation, JsonSchema compiledSchema) {
			this.schemaLocation = schemaLocation;
			this.compiledSchema = compiledSchema;
		}
	}
}
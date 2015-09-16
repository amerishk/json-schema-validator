package uk.co.o2.json.schema;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.o2.json.constants.Constants;
import uk.co.o2.json.constants.MessageConstants;
import uk.co.o2.json.message.Messages;

import com.fasterxml.jackson.databind.JsonNode;

// TODO: Auto-generated Javadoc
/**
 * The Class ObjectSchema.
 */
class ObjectSchema implements JsonSchema {
	
	/** The messages. */
	private static Messages messages;	
	
	ObjectSchema(Messages messages){
		this.messages = messages;
	}
	
	/** The Constant ALLOW_ALL_ADDITIONAL_PROPERTIES. */
	public static final JsonSchema ALLOW_ALL_ADDITIONAL_PROPERTIES = new JsonSchema() {
		@Override
		public List<ErrorMessage> validate(
		        JsonNode jsonDocumentToValidate) {
			return emptyList();
		}
		
		@Override
		public String getDescription() {
			return Constants.EMPTY_STRING;
		}
		
		@Override
		public boolean isAcceptableType(
		        JsonNode jsonDocument) {
			return true;
		}
		
	};
	
	/** The Constant FORBID_ANY_ADDITIONAL_PROPERTIES. */
	public static final JsonSchema FORBID_ANY_ADDITIONAL_PROPERTIES = new JsonSchema() {
		
		
		@Override
		public List<ErrorMessage> validate(
		        JsonNode jsonDocumentToValidate) {
			/*Messages messages = (Messages) FNPMApplicationContext
					.getBean(Constants.MESSAGES_SOURCE);*/ 
			return singleError(Constants.EMPTY_STRING, messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_UNEXPECTED_PROPERTY));
		}
		
		@Override
		public String getDescription() {
			return Constants.EMPTY_STRING;
		}
		
		@Override
		public boolean isAcceptableType(
		        JsonNode jsonDocument) {
			return false;
		}

	};
	
	/** The properties. */
	private final List<Property> properties = new ArrayList<>();
	
	/**
	 * Gets the properties.
	 * 
	 * @return the properties
	 */
	List<Property> getProperties() {
		return properties;
	}
	
	/** The additional properties. */
	private JsonSchema additionalProperties = ALLOW_ALL_ADDITIONAL_PROPERTIES;
	
	/**
	 * Sets the additional properties.
	 * 
	 * @param additionalProperties the new additional properties
	 */
	void setAdditionalProperties(
	        JsonSchema additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	
	/**
	 * Gets the additional properties.
	 * 
	 * @return the additional properties
	 */
	JsonSchema getAdditionalProperties() {
		return additionalProperties;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.fn.pm.validation.JsonSchema#validate(com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public List<ErrorMessage> validate(
	        JsonNode jsonDocumentToValidate) {
		
		List<ErrorMessage> results = new ArrayList<>();
		if (!isAcceptableType(jsonDocumentToValidate)) {
			return singleError(Constants.EMPTY_STRING, messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_INVALID_TYPE_OBJECT));
		}
		Set<String> visitedPropertyNames = new HashSet<>();
		
		for (Property property : properties) {
			if (!jsonDocumentToValidate.has(property.getName())) {
				if (property.isRequired()) {
					results.add(new ErrorMessage(property.getName(), messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_MISSING_REQUIRED_PROPERTY) + property.getName()));
				}
			} else {
				JsonNode propertyValue = jsonDocumentToValidate.get(property.getName());
				for (ErrorMessage nestedMessage : property.getNestedSchema().validate(propertyValue)) {
					results.add(new ErrorMessage(property.getName(), nestedMessage));
				}
			}
			visitedPropertyNames.add(property.getName());
		}
		
		for (Iterator<Map.Entry<String, JsonNode>> iterator = jsonDocumentToValidate.fields(); iterator.hasNext();) {
			Map.Entry<String, JsonNode> entry = iterator.next();
			if (!visitedPropertyNames.contains(entry.getKey())) {
				for (ErrorMessage it : additionalProperties.validate(entry.getValue())) {
					results.add(new ErrorMessage(entry.getKey(), it));
				}
			}
		}
		
		return results;
	}
	
	/**
	 * Single error.
	 * 
	 * @param string the string
	 * @param string2 the string2
	 * @return the list
	 */
	private static List<ErrorMessage> singleError(
	        String string,
	        String string2) {
		ErrorMessage msg = new ErrorMessage(string, string2);
		List<ErrorMessage> list = new ArrayList<ErrorMessage>();
		list.add(msg);
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * com.fn.pm.validation.JsonSchema#isAcceptableType(com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public boolean isAcceptableType(
	        JsonNode jsonDocument) {
		return jsonDocument.isObject();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.fn.pm.validation.JsonSchema#getDescription()
	 */
	@Override
	public String getDescription() {
		return "object";
	}
	
	/**
	 * The Class Property.
	 */
	static class Property {
		
		/** The name. */
		private String name;
		
		/** The required. */
		private boolean required;
		
		/** The nested schema. */
		private JsonSchema nestedSchema;
		
		
		/**
		 * Instantiates a new property.
		 */
		Property() {
			SimpleTypeSchema any= new SimpleTypeSchema(ObjectSchema.messages);
			any.setType(SimpleType.ANY);
			nestedSchema = any;
		}
		
		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Sets the name.
		 * 
		 * @param name the new name
		 */
		public void setName(
		        String name) {
			this.name = name;
		}
		
		/**
		 * Checks if is required.
		 * 
		 * @return true, if is required
		 */
		public boolean isRequired() {
			return required;
		}
		
		/**
		 * Sets the required.
		 * 
		 * @param required the new required
		 */
		public void setRequired(
		        boolean required) {
			this.required = required;
		}
		
		/**
		 * Gets the nested schema.
		 * 
		 * @return the nested schema
		 */
		public JsonSchema getNestedSchema() {
			return nestedSchema;
		}
		
		/**
		 * Sets the nested schema.
		 * 
		 * @param nestedSchema the new nested schema
		 */
		public void setNestedSchema(
		        JsonSchema nestedSchema) {
			this.nestedSchema = nestedSchema;
		}
	}
}
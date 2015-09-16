package uk.co.o2.json.schema;

import java.util.ArrayList;
import java.util.List;

import uk.co.o2.json.constants.Constants;
import uk.co.o2.json.constants.MessageConstants;
import uk.co.o2.json.message.Messages;

import com.fasterxml.jackson.databind.JsonNode;

// TODO: Auto-generated Javadoc
/**
 * The Class ArraySchema.
 */
class ArraySchema implements JsonSchema {
	
	/** The items. */
	private JsonSchema items;
	
	/** The max items. */
	private int maxItems;
	
	/** The min items. */
	private int minItems;
	/** The messages. */
	private Messages messages;
	

	/**
	 * Instantiates a new array schema.
	 */
	ArraySchema(Messages messages) {
		SimpleTypeSchema simpleTypeSchema = new SimpleTypeSchema(messages);
		simpleTypeSchema.setType(SimpleType.ANY);
		this.items = simpleTypeSchema;
		this.messages = messages;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.fn.pm.validation.JsonSchema#validate(com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public List<ErrorMessage> validate(
	        JsonNode jsonDocument) {
		List<ErrorMessage> results = new ArrayList<>();
		if (!isAcceptableType(jsonDocument)) {
			return singleError(Constants.EMPTY_STRING, messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_INVALID_TYPE_ARRAY));
		}
		if ((maxItems != 0) && (jsonDocument.size() > maxItems)) {
			return singleError(Constants.EMPTY_STRING, messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_ARRAY_SIZE_EXCEED), jsonDocument.size(), maxItems);
		}
		
		if ((minItems != 0) && (jsonDocument.size() < minItems)) {
			return singleError(Constants.EMPTY_STRING, messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_ARRAY_SIZE_FALL_SHORT), jsonDocument.size(), minItems);
		}
		
		int index = Constants.INTEGER_ZERO;
		for (JsonNode item : jsonDocument) {
			results.addAll(generateNestedErrorMessages(index, items.validate(item)));
			index += Constants.INTEGER_ONE;
		}
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.fn.pm.validation.JsonSchema#getDescription()
	 */
	@Override
	public String getDescription() {
		return Constants.KEY_ARRAY;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * com.fn.pm.validation.JsonSchema#isAcceptableType(com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public boolean isAcceptableType(
	        JsonNode jsonDocument) {
		return jsonDocument.isArray();
	}
	
	/**
	 * Generate nested error messages.
	 * 
	 * @param index the index
	 * @param errorMessages the error messages
	 * @return the list
	 */
	private List<ErrorMessage> generateNestedErrorMessages(
	        int index,
	        List<ErrorMessage> errorMessages) {
		List<ErrorMessage> nestedResults = new ArrayList<>();
		String pathPrefix = "[" + index + "]";
		for (ErrorMessage error : errorMessages) {
			nestedResults.add(new ErrorMessage(pathPrefix, error));
		}
		return nestedResults;
	}
	
	/**
	 * Sets the items.
	 * 
	 * @param items the new items
	 */
	void setItems(
	        JsonSchema items) {
		this.items = items;
	}
	
	/**
	 * Gets the items.
	 * 
	 * @return the items
	 */
	JsonSchema getItems() {
		return items;
	}
	
	/**
	 * Sets the max items.
	 * 
	 * @param maxItems the new max items
	 */
	void setMaxItems(
	        int maxItems) {
		this.maxItems = maxItems;
	}
	
	/**
	 * Gets the max items.
	 * 
	 * @return the max items
	 */
	int getMaxItems() {
		return maxItems;
	}
	
	/**
	 * Sets the min items.
	 * 
	 * @param minItems the new min items
	 */
	void setMinItems(
	        int minItems) {
		this.minItems = minItems;
	}
	
	/**
	 * Gets the min items.
	 * 
	 * @return the min items
	 */
	int getMinItems() {
		return minItems;
	}
	
	/**
	 * Single error.
	 * 
	 * @param string the string
	 * @param string2 the string2
	 * @return the list
	 */
	private List<ErrorMessage> singleError(
	        String string,
	        String string2) {
		ErrorMessage msg = new ErrorMessage(string, string2);
		List<ErrorMessage> list = new ArrayList<ErrorMessage>();
		list.add(msg);
		return list;
	}
	
	/**
	 * Single error.
	 * 
	 * @param string the string
	 * @param string2 the string2
	 * @param i the i
	 * @param j the j
	 * @return the list
	 */
	private List<ErrorMessage> singleError(
	        String string,
	        String string2,
	        int i,
	        int j) {
		ErrorMessage msg = new ErrorMessage(string, string2);
		List<ErrorMessage> list = new ArrayList<ErrorMessage>();
		list.add(msg);
		return list;
	}
}

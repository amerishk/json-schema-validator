package uk.co.o2.json.schema;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.o2.json.constants.Constants;
import uk.co.o2.json.constants.MessageConstants;
import uk.co.o2.json.message.Messages;

import com.fasterxml.jackson.databind.JsonNode;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleTypeSchema.
 */
class SimpleTypeSchema implements JsonSchema {
	
	/**
	 * Holds the instance of LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTypeSchema.class);
	
	/** The type. */
	private SimpleType type = SimpleType.ANY;
	
	/** The pattern. */
	private Pattern pattern;
	
	/** The format. */
	private String format;
	
	/** The max length. */
	private int maxLength;
	
	/** The min length. */
	private int minLength;
	
	/** The minimum. */
	private Number minimum;
	
	/** The maximum. */
	private Number maximum;
	
	/** The exclusive minimum. */
	private boolean exclusiveMinimum;
	
	/** The exclusive maximum. */
	private boolean exclusiveMaximum;
	
	/** The enumeration. */
	private List<JsonNode> enumeration;
	
	/** The messages. */
	private static Messages messages;
	
	SimpleTypeSchema(Messages messages){
		SimpleTypeSchema.messages = messages;
	}
	/*
	 * (non-Javadoc)
	 * @see com.fn.pm.validation.JsonSchema#validate(com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public List<ErrorMessage> validate(
	        JsonNode node) {
		List<ErrorMessage> results = new ArrayList<>();
		if (!isAcceptableType(node)) {
			results.add(new ErrorMessage(Constants.EMPTY_STRING, messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_INVALID_TYPE) + type.name().toLowerCase()));
		} else {
			validatePattern(node, results);
			validateFormat(node, results);
			validateRange(node, results);
			validateLength(node, results);
			validateNodeValueIsFromEnumeratedList(node, results);
		}
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.fn.pm.validation.JsonSchema#getDescription()
	 */
	@Override
	public String getDescription() {
		return type.toString().toLowerCase();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * com.fn.pm.validation.JsonSchema#isAcceptableType(com.fasterxml.jackson.databind.JsonNode)
	 */
	@Override
	public boolean isAcceptableType(
	        JsonNode node) {
		return type.matches(node);
	}
	
	/**
	 * Sets the enumeration.
	 * 
	 * @param enumeration the new enumeration
	 */
	void setEnumeration(
	        List<JsonNode> enumeration) {
		if (EnumSet.of(SimpleType.NULL, SimpleType.ANY).contains(type)) {
			throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_ENUMERATION_NOT_ALLOWED));
		}
		validateEnumElementsOfSameType(enumeration);
		this.enumeration = enumeration;
	}
	
	/**
	 * Sets the exclusive minimum.
	 * 
	 * @param exclusiveMinimum the new exclusive minimum
	 */
	void setExclusiveMinimum(
	        boolean exclusiveMinimum) {
		validateTypeNumberOrIntegerFor(Constants.KEY_EXCLUSIVE_MINIMUM);
		this.exclusiveMinimum = exclusiveMinimum;
	}
	
	/**
	 * Sets the exclusive maximum.
	 * 
	 * @param exclusiveMaximum the new exclusive maximum
	 */
	void setExclusiveMaximum(
	        boolean exclusiveMaximum) {
		validateTypeNumberOrIntegerFor(Constants.KEY_EXCLUSIVE_MAXIMUM);
		this.exclusiveMaximum = exclusiveMaximum;
	}
	
	/**
	 * Sets the minimum.
	 * 
	 * @param minimum the new minimum
	 */
	void setMinimum(
	        Number minimum) {
		validateTypeNumberOrIntegerFor(Constants.KEY_MINIMUM);
		this.minimum = minimum;
	}
	
	/**
	 * Sets the maximum.
	 * 
	 * @param maximum the new maximum
	 */
	void setMaximum(
	        Number maximum) {
		validateTypeNumberOrIntegerFor(Constants.KEY_MAXIMUM);
		this.maximum = maximum;
	}
	
	/**
	 * Validate enum elements of same type.
	 * 
	 * @param values the values
	 */
	private void validateEnumElementsOfSameType(
	        List<JsonNode> values) {
		for (JsonNode value : values) {
			if (!type.matches(value)) {
				throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_ENUMERATION_NOT_ALLOWED) + type);
			}
		}
	}
	
	/**
	 * Validate type number or integer for.
	 * 
	 * @param fieldName the field name
	 */
	private void validateTypeNumberOrIntegerFor(
	        String fieldName) {
		if (!EnumSet.of(SimpleType.INTEGER, SimpleType.NUMBER).contains(type)) {
			throw new IllegalArgumentException(fieldName + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_CAN_ONLY_BE_USED_FOR_NUMBER));
		}
	}
	
	/**
	 * Sets the pattern.
	 * 
	 * @param pattern the new pattern
	 */
	void setPattern(
	        Pattern pattern) {
		validatePatternAndType(pattern, type);
		this.pattern = pattern;
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type the new type
	 */
	void setType(
	        SimpleType type) {
		validateFormatAndType(format, type);
		validatePatternAndType(pattern, type);
		this.type = type;
	}
	
	/**
	 * Sets the format.
	 * 
	 * @param format the new format
	 */
	void setFormat(
	        String format) {
		validateFormatAndType(format, type);
		this.format = format;
	}
	
	/**
	 * Sets the max length.
	 * 
	 * @param maxLength the new max length
	 */
	void setMaxLength(
	        int maxLength) {
		if (type != SimpleType.STRING) {
			throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_MAXLENGTH_CAN_ONLY_BE_USED_FOR_STRING));
		}
		this.maxLength = maxLength;
	}
	
	/**
	 * Sets the min length.
	 * 
	 * @param minLength the new min length
	 */
	void setMinLength(
	        int minLength) {
		if (type != SimpleType.STRING) {
			throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_MINLENGTH_CAN_ONLY_BE_USED_FOR_STRING));
		}
		this.minLength = minLength;
	}
	
	/**
	 * Validate pattern and type.
	 * 
	 * @param pattern the pattern
	 * @param type the type
	 */
	private static void validatePatternAndType(
	        Pattern pattern,
	        SimpleType type) {
		if ((type != SimpleType.STRING) && (pattern != null)) {
			throw new IllegalArgumentException(messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_REGEX_CAN_ONLY_BE_USED_FOR_STRING));
		}
	}
	
	/**
	 * Validate format and type.
	 * 
	 * @param format the format
	 * @param type the type
	 */
	private static void validateFormatAndType(
	        String format,
	        SimpleType type) {
		FormatValidator formatValidator = formatValidators.get(format);
		if ((formatValidator != null) && (!formatValidator.isCompatibleType(type))) {
			throw new IllegalArgumentException((messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_FORMAT)) + format + (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_IS_NOT_VALID_TYPE)) + type.name().toLowerCase());
		}
	}
	
	/**
	 * Validate node value is from enumerated list.
	 * 
	 * @param node the node
	 * @param results the results
	 */
	private void validateNodeValueIsFromEnumeratedList(
	        JsonNode node,
	        List<ErrorMessage> results) {
		if ((enumeration != null) && !enumeration.contains(node)) {
			results.add(new ErrorMessage(Constants.EMPTY_STRING, (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE)) + node.toString() + (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_MUST_BE_ONE_OF)) + enumeration.toString()));
		}
	}
	
	/**
	 * Validate length.
	 * 
	 * @param node the node
	 * @param results the results
	 */
	private void validateLength(
	        JsonNode node,
	        List<ErrorMessage> results) {
		if (minLength > 0) {
			String value = type.getValue(node).toString();
			if (value.length() < minLength) {
				results.add(new ErrorMessage(Constants.EMPTY_STRING, (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_APOSTROPHE)) + node.textValue() + (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_MUST_BE_GREATER_OR_EQUAL)) + minLength + (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_CHARACTERS))));
			}
		}
		if (maxLength > 0) {
			String value = type.getValue(node).toString();
			if (value.length() > maxLength) {
				results.add(new ErrorMessage(Constants.EMPTY_STRING, String.format((messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_MUST_BE_LESS_OR_EQUAL)), node.textValue(), maxLength)));
			}
		}
	}
	
	/**
	 * Validate range.
	 * 
	 * @param node the node
	 * @param results the results
	 */
	private void validateRange(
	        JsonNode node,
	        List<ErrorMessage> results) {
		if (this.minimum != null) {
			String nodeValueAsString = type.getValue(node).toString();
			BigDecimal value = new BigDecimal(nodeValueAsString);
			BigDecimal minimumValue = new BigDecimal(this.minimum.toString());
			if (exclusiveMinimum && (value.compareTo(minimumValue) < 1)) {
				results.add(new ErrorMessage(Constants.EMPTY_STRING, (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_APOSTROPHE))+ nodeValueAsString + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_MUST_BE_GREATER) + minimumValue + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_WHEN_EXCLUSIVEMINIMUM)));
			} else if (value.compareTo(minimumValue) < 0) {
				results.add(new ErrorMessage(Constants.EMPTY_STRING, (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_APOSTROPHE)) + nodeValueAsString + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_MUST_BE_GREATER_OR_EQUAL) + minimumValue));
			}
		}
		
		if (this.maximum != null) {
			String nodeValueAsString = type.getValue(node).toString();
			BigDecimal value = new BigDecimal(nodeValueAsString);
			BigDecimal maximumValue = new BigDecimal(this.maximum.toString());
			if (exclusiveMaximum && value.compareTo(maximumValue) >= 0) {
				results.add(new ErrorMessage(Constants.EMPTY_STRING, (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_APOSTROPHE)) + nodeValueAsString + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_MUST_BE_LESSER) + maximumValue + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_WHEN_EXCLUSIVEMAXIMUM)));
			} else if (value.compareTo(maximumValue) > 0) {
				results.add(new ErrorMessage(Constants.EMPTY_STRING, (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_APOSTROPHE)) + nodeValueAsString + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_MUST_BE_LESS_OR_EQUAL) + maximumValue));
			}
		}
	}
	
	/**
	 * Validate format.
	 * 
	 * @param node the node
	 * @param results the results
	 */
	private void validateFormat(
	        JsonNode node,
	        List<ErrorMessage> results) {
		if (format != null) {
			FormatValidator formatValidator = formatValidators.get(format);
			if (formatValidator != null && !formatValidator.isValid(node)) {
				results.add(new ErrorMessage(Constants.EMPTY_STRING, (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_VALUE_APOSTROPHE)) + node.textValue() + messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_IS_NOT_VALID) + format));
			}
		}
	}
	
	/**
	 * Validate pattern.
	 * 
	 * @param node the node
	 * @param results the results
	 */
	private void validatePattern(
	        JsonNode node,
	        List<ErrorMessage> results) {
		if (pattern != null) {
			String value = type.getValue(node).toString();
			if (!pattern.matcher(value).matches()) {
				results.add(new ErrorMessage(Constants.EMPTY_STRING, (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_STRING_VALUE)) + value + (messages.getMessage(MessageConstants.SCHEMA_VALIDATOR_DOES_NOT_MATCH_REGEX)) + pattern.pattern() + "'"));
			}
		}
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	SimpleType getType() {
		return type;
	}
	
	/**
	 * Gets the pattern.
	 * 
	 * @return the pattern
	 */
	Pattern getPattern() {
		return pattern;
	}
	
	/**
	 * Gets the format.
	 * 
	 * @return the format
	 */
	String getFormat() {
		return format;
	}
	
	/**
	 * Gets the max length.
	 * 
	 * @return the max length
	 */
	int getMaxLength() {
		return maxLength;
	}
	
	/**
	 * Gets the min length.
	 * 
	 * @return the min length
	 */
	int getMinLength() {
		return minLength;
	}
	
	/**
	 * Gets the minimum.
	 * 
	 * @return the minimum
	 */
	Number getMinimum() {
		return minimum;
	}
	
	/**
	 * Gets the maximum.
	 * 
	 * @return the maximum
	 */
	Number getMaximum() {
		return maximum;
	}
	
	/**
	 * Checks if is exclusive minimum.
	 * 
	 * @return true, if is exclusive minimum
	 */
	boolean isExclusiveMinimum() {
		return exclusiveMinimum;
	}
	
	/**
	 * Checks if is exclusive maximum.
	 * 
	 * @return true, if is exclusive maximum
	 */
	boolean isExclusiveMaximum() {
		return exclusiveMaximum;
	}
	
	/**
	 * Gets the enumeration.
	 * 
	 * @return the enumeration
	 */
	List<JsonNode> getEnumeration() {
		return enumeration;
	}
	
	/**
	 * The Interface FormatValidator.
	 */
	private static interface FormatValidator {
		
		/**
		 * Checks if is valid.
		 * 
		 * @param node the node
		 * @return true, if is valid
		 */
		boolean isValid(
		        JsonNode node);
		
		/**
		 * Checks if is compatible type.
		 * 
		 * @param type the type
		 * @return true, if is compatible type
		 */
		boolean isCompatibleType(
		        SimpleType type);
		
	}
	
	/** The format validators. */
	private static Map<String, FormatValidator> formatValidators = Collections.unmodifiableMap(new HashMap<String, FormatValidator>() {
		{
			put("date-time", new FormatValidator() {
				@Override
				public boolean isValid(
				        JsonNode node) {
					String value = SimpleType.STRING.getValue(node).toString();
					try {
						DatatypeConverter.parseDateTime(value);
						return true;
					} catch (IllegalArgumentException illegalArgumentException) {
						LOGGER.error("IllegalArgumentException " + illegalArgumentException);
						return false;
					}
				}
				
				@Override
				public boolean isCompatibleType(
				        SimpleType type) {
					return type == SimpleType.STRING;
				}
			});
			put("date", new FormatValidator() {
				@Override
				public boolean isValid(
				        JsonNode node) {
					String value = SimpleType.STRING.getValue(node).toString();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					dateFormat.setLenient(false);
					ParsePosition position = new ParsePosition(0);
					
					Date result = dateFormat.parse(value, position);
					
					String[] parts = value.substring(0, position.getIndex()).split("-");
					boolean partLengthsOk = parts.length == 3 && parts[0].length() == 4 && parts[1].length() == 2 && parts[2].length() == 2;
					
					boolean valueIsTooLongToBeADate = position.getIndex() < value.length();
					
					return (result != null) && partLengthsOk && (!valueIsTooLongToBeADate);
				}
				
				@Override
				public boolean isCompatibleType(
				        SimpleType type) {
					return type == SimpleType.STRING;
				}
			});
			put("time", new FormatValidator() {
				@Override
				public boolean isValid(
				        JsonNode node) {
					String value = SimpleType.STRING.getValue(node).toString();
					SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					dateFormat.setLenient(false);
					ParsePosition position = new ParsePosition(0);
					
					Date result = dateFormat.parse(value, position);
					
					return (result != null) && (position.getIndex() == value.length());
				}
				
				@Override
				public boolean isCompatibleType(
				        SimpleType type) {
					return type == SimpleType.STRING;
				}
			});
			put("utc-millisec", new FormatValidator() {
				@Override
				public boolean isValid(
				        JsonNode node) {
					return true;
				}
				
				@Override
				public boolean isCompatibleType(
				        SimpleType type) {
					return EnumSet.of(SimpleType.INTEGER, SimpleType.NUMBER).contains(type);
				}
				
			});
			put("regex", new FormatValidator() {
				@Override
				public boolean isValid(
				        JsonNode node) {
					String value = SimpleType.STRING.getValue(node).toString();
					try {
						// noinspection ResultOfMethodCallIgnored
						Pattern.compile(value);
						return true;
					} catch (PatternSyntaxException patternSyntaxException) {
						LOGGER.error("PatternSyntaxException " + patternSyntaxException);
						return false;
					}
				}
				
				@Override
				public boolean isCompatibleType(
				        SimpleType type) {
					return type == SimpleType.STRING;
				}
			});
			put("uri", new FormatValidator() {
				@Override
				public boolean isValid(
				        JsonNode node) {
					String value = SimpleType.STRING.getValue(node).toString();
					try {
						new URI(value);
						return true;
					} catch (URISyntaxException uriSyntaxException) {
						LOGGER.error("URISyntaxException " + uriSyntaxException);
						return false;
					}
				}
				
				@Override
				public boolean isCompatibleType(
				        SimpleType type) {
					return type == SimpleType.STRING;
				}
			});
		}
	});
}
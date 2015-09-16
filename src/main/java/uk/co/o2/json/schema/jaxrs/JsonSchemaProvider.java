package uk.co.o2.json.schema.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import uk.co.o2.json.constants.Constants;
import uk.co.o2.json.constants.MessageConstants;
import uk.co.o2.json.dto.MessageDTO;
import uk.co.o2.json.dto.ResponseDTO;
import uk.co.o2.json.message.Messages;
import uk.co.o2.json.schema.ErrorMessage;
import uk.co.o2.json.schema.JsonSchema;
import uk.co.o2.json.schema.SchemaPassThroughCache;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JsonSchemaProvider extends JacksonJsonProvider {
	
	/** The cache. */
	private static SchemaPassThroughCache cache;
	
	/** The schema lookup. */
	private final SchemaLookup schemaLookup;
	
	/**
	 * Holds the instance of Messages.
	 */
	@Autowired
	private Messages messages;
	
	/**
	 * schemaValidationSwitch - schema validation on/off switch
	 */
	private String schemaValidationSwitch;
	
	/**
	 * Instantiates a new json schema provider.
	 * 
	 * @param schemaLookup the schema lookup
	 */
	public JsonSchemaProvider(SchemaLookup schemaLookup) {
		cache = new SchemaPassThroughCache(new JsonFactory(new ObjectMapper()),messages);
		this.schemaLookup = schemaLookup;
		this.configure(SerializationFeature.INDENT_OUTPUT, true);
	}
	
	/**
	 * Instantiates a new json schema provider with explicitly setting schemaValidationSwitch.
	 * 
	 * @param schemaLookup
	 * @param validationSwitch
	 */
	public JsonSchemaProvider(SchemaLookup schemaLookup, String validationSwitch, Messages msgs) {
		this.messages=msgs;
		cache = new SchemaPassThroughCache(new JsonFactory(new ObjectMapper()),messages);
		this.schemaLookup = schemaLookup;
		this.configure(SerializationFeature.INDENT_OUTPUT, true);
		this.schemaValidationSwitch = validationSwitch;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.jaxrs.base.ProviderBase#readFrom(java.lang.Class,
	 * java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType,
	 * javax.ws.rs.core.MultivaluedMap, java.io.InputStream)
	 */
	@Override
	public Object readFrom(
	        Class<Object> type,
	        Type genericType,
	        Annotation[] annotations,
	        MediaType mediaType,
	        MultivaluedMap<String, String> httpHeaders,
	        InputStream entityStream) throws IOException {
		
		this.cache = new SchemaPassThroughCache(new JsonFactory(new ObjectMapper()),messages);
		/**
		 * Property to toggle the JSON schema validations On/Off
		 */
		String schemaValidation = messages == null ? this.schemaValidationSwitch : messages.getMessage(MessageConstants.SCHEMA_VALIDATION_SWITCH, Locale.ENGLISH);
		
		Schema schemaAnnotation = null;
		if (Constants.SCHEMA_VALIDATION_ON.equals(schemaValidation)) {
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(Schema.class)) {
					schemaAnnotation = (Schema) annotation;
					break;
				}
			}
		}
		
		if (schemaAnnotation != null) {
			ObjectMapper mapper = locateMapper(type, mediaType);
			JsonParser jp = mapper.getFactory().createJsonParser(entityStream);
			jp.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
			URL schemaLocation = schemaLookup.getSchemaURL(schemaAnnotation.value());
			JsonSchema jsonSchema = cache.getSchema(schemaLocation);
			JsonNode jsonNode = mapper.readTree(jp);
			List<ErrorMessage> validationErrors = jsonSchema.validate(jsonNode);
			if (validationErrors.isEmpty()) {
				return mapper.reader().withType(mapper.constructType(genericType)).readValue(jsonNode);
			}
			
			throw new WebApplicationException(generateErrorResponse(validationErrors));
		} else {
			return super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
		}
	}
	
	/**
	 * Generate error message.
	 * 
	 * @param validationErrors the validation errors
	 * @return the response
	 */
	protected Response generateErrorMessage(
	        List<ErrorMessage> validationErrors) {
		StringBuilder content = new StringBuilder();
		for (ErrorMessage error : validationErrors) {
			contentBuilder(content, error);
			content.append(Constants.NEW_LINE);
		}
		
		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(content.toString()).build();
	}
	
	/**
	 * Content builder.
	 * 
	 * @param content the content
	 * @param error the error
	 */
	private void contentBuilder(
	        StringBuilder content,
	        ErrorMessage error) {
		//content.append(error.getLocation());
		content.append((Constants.COLON_SPACE));
		content.append(error.getMessage());
	}
	
	/**
	 * Generate error response.
	 * 
	 * @param validationErrors the validation errors
	 * @return the response
	 */
	protected Response generateErrorResponse(
	        List<ErrorMessage> validationErrors) {
		StringBuilder content = null;
		List<MessageDTO> errorMessages = new ArrayList<MessageDTO>();
		MessageDTO errorMessage = null;
		for (ErrorMessage error : validationErrors) {
			content = new StringBuilder();
			contentBuilder(content, error);
			errorMessage = new MessageDTO();
			java.util.Date date = new java.util.Date();
			/** Creating the error message for each field validation failure **/
			errorMessage.setDateCreated(date.toString());
			errorMessage.setType(Constants.ERROR);
			errorMessage.setMessage(content.toString());
			errorMessage.setElementName(error.getLocation());
			errorMessages.add(errorMessage);
		}
		ResponseDTO response = new ResponseDTO();
		response.setApiMessages(errorMessages);
		
		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(response).build();
		
	}
	
}
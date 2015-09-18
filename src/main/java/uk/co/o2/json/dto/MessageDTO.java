package uk.co.o2.json.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.co.o2.json.constants.Constants;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This is the DTO class mapped to apiMessages attribute of Login API Response
 * 
 * @author Sapient
 */
@Component
@Qualifier(Constants.JSON_MESSAGE_DTO)
@Scope(Constants.PROTOTYPE)
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(Include.NON_EMPTY)
public class MessageDTO implements Serializable {
  
  /**
   * Variable for Serial version UID
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Variable for type element in Reponse which denotes the status. Typically either "success" or
   * "error".
   */
  private String type;
  /**
   * Variable for containing the error code
   */
  private String code;
  
  /**
   * Variable for dateCreated which is a timestamp of the api message for front-end
   */
  private String dateCreated;
  
  
  /**
   * Holds the list of additional status of the response
   */
  private List<AdditionalStatus> additionalStatus;
  
  /**
   * @return the additionalStatus
   */
  public List<AdditionalStatus> getAdditionalStatus() {
    return additionalStatus;
  }
  

  /**
   * Variable for containing description in case of error
   */
  private String description;
  
  /**
   *  Variable for containing element name in case of error 
   */
  private String elementName;
  
  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }
  
  /**
   * @param description the description to set
   */
  public void setDescription(
      String descriptionData) {
    this.description = descriptionData;
  }
  
  
  /**
   * @param additionalStatus the additionalStatus to set
   */
  public void setAdditionalStatus(
      List<AdditionalStatus> additionalStatusData) {
    this.additionalStatus = additionalStatusData;
  }
  
  /**
   * @return the code
   */
  public String getCode() {
    return code;
  }
  
  /**
   * @param code the code to set
   */
  public void setCode(
      String codeData) {
    this.code = codeData;
  }
  
  /**
   * Variable for message element which may be provided if a message should be displayed to the user
   * (both under success and error states). This is not the description of the event but rather
   * messaging on what the user is expected to do. For example: "please try again later" or
   * "we have successfully created your account".
   */
  private String message;
  /**
   * Variable for event element which is provided by the front-end during the request and should
   * simply be passed back as is.
   */
  
  private RequestDTO request;
  
  /**
   * @return the request
   */
  public RequestDTO getRequest() {
    return request;
  }
  
  /**
   * @param request the request to set
   */
  public void setRequest(
      RequestDTO requestData) {
    this.request = requestData;
  }
  
  
  
  /**
   * @return the type
   */
  public String getType() {
    return type;
  }
  
  /**
   * @param type the type to set
   */
  public void setType(
      String typeData) {
    this.type = typeData;
  }
  
  /**
   * @return the dateCreated
   */
  public String getDateCreated() {
    return dateCreated;
  }
  
  /**
   * @param dateCreated the dateCreated to set
   */
  public void setDateCreated(
      String dateCreatedData) {
    this.dateCreated = dateCreatedData;
  }
  
  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }
  
  /**
   * @param message the message to set
   */
  public void setMessage(
      String messageData) {
    this.message = messageData;
  }

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * @param elementName
	 *            the elementName to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
  
}

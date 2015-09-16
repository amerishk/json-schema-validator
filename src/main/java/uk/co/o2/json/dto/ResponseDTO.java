package uk.co.o2.json.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.o2.json.constants.Constants;

/**
 * This is the Login Response DTO which holds the response parameters
 * 
 * @author surbala singh
 */
@Component
@Scope(Constants.PROTOTYPE)
@Qualifier(Constants.RESPONSE_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDTO implements Serializable {
  
  /**
   * Holds the instance of serialVersionUID
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Holds the list of api messages response
   */
  private List<MessageDTO> apiMessages;
  
  /**
   * Holds the payload of response generated which includes information such as personal and contact
   * information
   */
  
  private PayloadResponseDTO payload;
  
  /**
   * @return the apiMessages
   */
  public List<MessageDTO> getApiMessages() {
    return apiMessages;
  }
  
  /**
   * @param apiMessages the apiMessages to set
   */
  public void setApiMessages(
      List<MessageDTO> apiMsg) {
    this.apiMessages = apiMsg;
  }
  
  /**
   * @return the payload
   */
  public PayloadResponseDTO getPayload() {
    return payload;
  }
  
  /**
   * @param payload the payload to set
   */
  public void setPayload(
      PayloadResponseDTO payloadData) {
    this.payload = payloadData;
  }
  
}

package uk.co.o2.json.dto;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import uk.co.o2.json.constants.Constants;

/**
 * This is the Login Request DTO which holds the request parameters
 * 
 * @author Sapient
 */
@Component
@Qualifier(Constants.REQUEST_DTO)
@Scope(Constants.PROTOTYPE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class RequestDTO implements Serializable {
  
  /**
   * Variable for Serial version UID
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Variable for event element, which would be provided by the front-end during the request and
   * should simply be passed back as is.
   */
  private String event;
  
  /**
   * Variable for id element. This would be returned back by the back-end as-is in the response
   * inside apiMessage.request
   */
  private String id;
  
  /**
   * @return the event
   */
  public String getEvent() {
    return event;
  }
  
  /**
   * @param event the event to set
   */
  public void setEvent(
      String event) {
    this.event = event;
  }
  
  /**
   * @return the id
   */
  public String getId() {
    return id;
  }
  
  /**
   * @param id the id to set
   */
  public void setId(
      String id) {
    this.id = id;
  }
  
}

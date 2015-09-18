package uk.co.o2.json.dto;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.co.o2.json.constants.Constants;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This is a DTO class for Response Header class which contains APIMESSAGES Attribute
 * 
 * @author Sapient
 */
@Component
@Scope(Constants.PROTOTYPE)
@Qualifier(Constants.JSON_ADDITIONAL_STATUS_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationAdditionalStatus implements Serializable {
  /**
   * Variable for Serial version UID
   */
  private static final long serialVersionUID = -936739716836407389L;
  
  /**
   * Variable for serverStatusCode in case of error response
   */
  private String serverStatusCode;
  /**
   * Variable for statusDescription in case of error response
   */
  private String statusDesc;
  /**
   * @return the serverStatusCode
   */
  public String getServerStatusCode() {
    return serverStatusCode;
  }
  /**
   * @param serverStatusCode the serverStatusCode to set
   */
  public void setServerStatusCode(
      String serverStatusCode) {
    this.serverStatusCode = serverStatusCode;
  }
  /**
   * @return the statusDesc
   */
  public String getStatusDesc() {
    return statusDesc;
  }
  /**
   * @param statusDesc the statusDesc to set
   */
  public void setStatusDesc(
      String statusDesc) {
    this.statusDesc = statusDesc;
  }
 
}

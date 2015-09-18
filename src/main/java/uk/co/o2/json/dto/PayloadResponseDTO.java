package uk.co.o2.json.dto;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.o2.json.constants.Constants;

/**
 * This is the DTO class mapped to PayLoad attribute of Verify API Response which includes account
 * information such as personal and contact informarion of user
 * 
 * @author surbala singh
 */
@Component
@Scope(Constants.PROTOTYPE)
@Qualifier(Constants.JSON_PAYLOAD_RESPONSE_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayloadResponseDTO implements Serializable {
  
  /**
   * Holds the instance of serialVersionUID
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Holds the account information of user including personal and contact information
   */
  
  private UserResponseDTO user;
  
  /**
   * @return the user
   */
  public UserResponseDTO getUser() {
    return user;
  }
  
  /**
   * @param user the user to set
   */
  public void setUser(
      UserResponseDTO userResObj) {
    this.user = userResObj;
  }
  
}

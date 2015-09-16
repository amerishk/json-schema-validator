package uk.co.o2.json.dto;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.o2.json.constants.Constants;

/**
 * This is the DTO class mapped to personalInformation attribute of verify API Request. This holds
 * the personal information of a user such as firstName, lastName, middleName,socialSecurityNumber
 * 
 * @author surbala singh
 */
@Component
@Scope(Constants.PROTOTYPE)
@Qualifier(Constants.VERIFY_ENROLLMENT_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollmentDTO implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * Holds the partyIdentifier of the user
   */
  private String partyIdentifier;
  
  /**
   * Holds the subjectId of the user
   */
  private String subjectId;
  
  /**
   * @return the partyIdentifier
   */
  public String getPartyIdentifier() {
    return partyIdentifier;
  }
  
  /**
   * @param partyIdentifier the partyIdentifier to set
   */
  public void setPartyIdentifier(
      String pIdentifier) {
    this.partyIdentifier = pIdentifier;
  }
  
  /**
   * @return the subjectId
   */
  public String getSubjectId() {
    return subjectId;
  }
  
  /**
   * @param subjectId the subjectId to set
   */
  public void setSubjectId(
      String subId) {
    this.subjectId = subId;
  }
}

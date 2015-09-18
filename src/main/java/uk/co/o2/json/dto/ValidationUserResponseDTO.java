package uk.co.o2.json.dto;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.o2.json.constants.Constants;

/**
 * This is the DTO class mapped to account attribute of verify API Response
 * 
 * @author surbala singh
 */
@Component
@Scope(Constants.PROTOTYPE)
@Qualifier(Constants.JSON_USER_RESPONSE_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationUserResponseDTO implements Serializable {
  /**
   * Holds the instance of serialVersionUID
   */
  private static final long serialVersionUID = 1L;
  /**
   * Holds user relates information such as as firstName, lastName, middleName and
   * socialSecurityNumber
   */
  
  private ValidationRegUserContactInformationDTO contactInformation;
  /**
   * Holds user relates contact information such as as mobilePhone, homePhone, workPhone ,email etc.
   */
  
  private ValidationEnrollmentDTO enrollment;
  
  /**
   * @return the contactInformation
   */
  public ValidationRegUserContactInformationDTO getContactInformation() {
    return contactInformation;
  }
  
  /**
   * @param contactInformation the contactInformation to set
   */
  public void setContactInformation(
      ValidationRegUserContactInformationDTO contactInfo) {
    this.contactInformation = contactInfo;
  }
  
  /**
   * @return the serialversionuid
   */
  public static long getSerialversionuid() {
    return serialVersionUID;
  }
  
  /**
   * @return the enrollment
   */
  public ValidationEnrollmentDTO getEnrollment() {
    return enrollment;
  }
  
  /**
   * @param enrollment the enrollment to set
   */
  public void setEnrollment(
      ValidationEnrollmentDTO enrollmentObj) {
    this.enrollment = enrollmentObj;
  }
  
}

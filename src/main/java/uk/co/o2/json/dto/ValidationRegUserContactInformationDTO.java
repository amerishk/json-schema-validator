package uk.co.o2.json.dto;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.o2.json.constants.Constants;

/**
 * This is the DTO class mapped to contactInformation attribute of Verify API Response.Holds user
 * relates contact information such as mobilePhone, homePhone, workPhone ,email etc.
 * 
 * @author surbala singh
 */
@Component
@Scope(Constants.PROTOTYPE)
@Qualifier(Constants.JSON_REG_USER_CONTACT_INFORMATION_DTO)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationRegUserContactInformationDTO implements Serializable {
  /**
   * Holds the instance of serialVersionUID
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Holds the mobile number of user
   */
  private String mobilePhone;
  /**
   * Holds the home phone number of user
   */
  private String homePhone;
  /**
   * Holds the work phone number of user
   */
  private String workPhone;
  /**
   * Holds the email address of user
   */
  private String email;
  /**
   * Holds the active text number of user
   */
  private String activeTextNumber;
  /**
   * Holds the active voice number of user
   */
  private String activeVoiceNumber;
  
  /**
   * @return the mobilePhone
   */
  public String getMobilePhone() {
    return mobilePhone;
  }
  
  /**
   * @param mobilePhone the mobilePhone to set
   */
  public void setMobilePhone(
      String mPhone) {
    this.mobilePhone = mPhone;
  }
  
  /**
   * @return the homePhone
   */
  public String getHomePhone() {
    return homePhone;
  }
  
  /**
   * @param homePhone the homePhone to set
   */
  public void setHomePhone(
      String hPhone) {
    this.homePhone = hPhone;
  }
  
  /**
   * @return the workPhone
   */
  public String getWorkPhone() {
    return workPhone;
  }
  
  /**
   * @param workPhone the workPhone to set
   */
  public void setWorkPhone(
      String wPhone) {
    this.workPhone = wPhone;
  }
  
  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }
  
  /**
   * @param email the email to set
   */
  public void setEmail(
      String emailAdd) {
    this.email = emailAdd;
  }
  
  /**
   * @return the activeTextNumber
   */
  public String getActiveTextNumber() {
    return activeTextNumber;
  }
  
  /**
   * @param activeTextNumber the activeTextNumber to set
   */
  public void setActiveTextNumber(
      String activeTextNum) {
    this.activeTextNumber = activeTextNum;
  }
  
  /**
   * @return the activeVoiceNumber
   */
  public String getActiveVoiceNumber() {
    return activeVoiceNumber;
  }
  
  /**
   * @param activeVoiceNumber the activeVoiceNumber to set
   */
  public void setActiveVoiceNumber(
      String activeVoiceNum) {
    this.activeVoiceNumber = activeVoiceNum;
  }
  
}

package uk.co.o2.json.message;

import java.util.Locale;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.NumberUtils;

import uk.co.o2.json.constants.Constants;

/**
 * This is the Message class which reads the properties file
 * 
 */
public class Messages implements InitializingBean, DisposableBean {
	/**
	 * Holds the instance of Reloadable MessageSource
	 */
	@Autowired
	@Qualifier(Constants.VALIDATION_MESSAGES_SOURCE)
	private ReloadableResourceBundleMessageSource validationMessagesSource;
	
	/**
	 * Holds the instance of ARR__EMPTY_OBJECT
	 */
	private final Object[] emptyObjectArr = new Object[0];
	
	/**
	 * Empty Constructor
	 */
	public Messages() {
	}
	
	/**
	 * This returns the ReloadableResourceBundleMessageSource object
	 * 
	 * @return
	 */
	public ReloadableResourceBundleMessageSource getMessagesSource() {
		return validationMessagesSource;
	}
	
	/**
	 * This sets the Message source object
	 * 
	 * @param messagesSource
	 */
	public void setMessagesSource(
	        ReloadableResourceBundleMessageSource validationMessagesSource) {
		this.validationMessagesSource = validationMessagesSource;
	}
	
	/**
	 * This returns the message value for the message prop name
	 * 
	 * @param msgPropName
	 * @return
	 */
	public String getMessage(
	        String msgPropName) {
		return getMessage(msgPropName, Constants.EMPTY_STRING, Locale.ENGLISH, emptyObjectArr);
	}
	
	/**
	 * This returns the message value for the message prop name and args
	 * 
	 * @param msgPropName
	 * @param args
	 * @return
	 */
	public String getMessage(
	        String msgPropName,
	        Object[] args) {
		return getMessage(msgPropName, Constants.EMPTY_STRING, Locale.ENGLISH, args);
	}
	
	/**
	 * This returns the message value for the prop name, args and Locale
	 * 
	 * @param msgPropName
	 * @param locale
	 * @return
	 */
	public String getMessage(
	        String msgPropName,
	        Locale locale) {
		return getMessage(msgPropName, Constants.EMPTY_STRING, locale, emptyObjectArr);
	}
	
	/**
	 * This returns the message value for the prop name, message and Locale
	 * 
	 * @param msgPropName
	 * @param defaultMessage
	 * @param locale
	 * @return
	 */
	public String getMessage(
	        String msgPropName,
	        String defaultMessage,
	        Locale locale) {
		return getMessage(msgPropName, defaultMessage, locale, emptyObjectArr);
	}
	
	/**
	 * This returns the message value for the prop name, default message, args and Locale
	 * 
	 * @param msgPropName
	 * @param defaultMessage
	 * @param args
	 * @param locale
	 * @return
	 */
	public String getMessage(
	        String msgPropName,
	        String defaultMessage,
	        Object[] args,
	        Locale locale) {
		return getMessage(msgPropName, defaultMessage, locale, args);
	}
	
	/**
	 * This returns the message value for the prop name, default message and Locale
	 * 
	 * @param msgPropName
	 * @param defaultMessage
	 * @param locale
	 * @param args
	 * @return
	 */
	String getMessage(
	        String msgPropName,
	        String defaultMessage,
	        Locale locale,
	        Object[] args) {
		try {
			String msgPropNameToUse = msgPropName;
			return validationMessagesSource.getMessage(msgPropNameToUse, args, defaultMessage, locale);
		} catch (NoSuchMessageException e) {
			return Constants.EMPTY_STRING;
		}
	}
	
	/**
	 * This returns the Integer value of a property name
	 * 
	 * @param propertyName
	 * @return
	 */
	public int getMessagePropertyInteger(
	        String propertyName) {
		String propertyValue = getMessage(propertyName, Constants.EMPTY_STRING, emptyObjectArr, null);
		try {
			return NumberUtils.parseNumber(propertyValue, Integer.class);
		} catch (NumberFormatException e) {
			return Constants.NEGATIVE_INTEGER_ONE;
		} catch (NoSuchMessageException e) {
			return Constants.NEGATIVE_INTEGER_ONE;
		}
	}
	
	/**
	 * This returns the property value based on property name.
	 * 
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	public int getMessagePropertyInteger(
	        String propertyName,
	        int defaultValue) {
		int propertyValue = getMessagePropertyInteger(propertyName);
		if (propertyValue == Constants.NEGATIVE_INTEGER_ONE) {
			return defaultValue;
		} else {
			return propertyValue;
		}
	}
	
	/**
	 * This returns the boolean value for a property name
	 * 
	 * @param propertyName
	 * @return
	 */
	public Boolean getMessagePropertyBoolean(
	        String propertyName) {
		try {
			String propFound = getMessage(propertyName, Constants.EMPTY_STRING, emptyObjectArr, Locale.getDefault());
			return Boolean.valueOf(propFound);
		} catch (NoSuchMessageException e) {
			return false;
		}
	}
	
	/**
	 * This returns the boolean value for a property name and default value
	 * 
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	public Boolean getMessagePropertyBoolean(
	        String propertyName,
	        Boolean defaultValue) {
		Boolean propertyValue = getMessagePropertyBoolean(propertyName);
		
		return propertyValue != null ? propertyValue : defaultValue;
	}
	
	/**
	 * Inherited method which destroys the message bundles
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		/**
		 * empty method
		 */
	}
	
	/**
	 * Inherited method which will be used to set the properties
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		/**
		 * empty method
		 */
	}
	
}

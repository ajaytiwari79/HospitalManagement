package com.kairos.utils.response;


/**
 * ValidationMessage object for application requests.
 */
public class ValidationMessageDTO {
    /**
     * Stores Message Strings
     */
    public enum MessageType {
        SUCCESS, INFO, WARNING, ERROR
    }

    private String message;
    private MessageType type;


    /**
     * getMessage
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * setMessage
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * getType
     * @return
     */
    public MessageType getType() {
        return type;
    }

    /**
     * setType
     * @param type
     */
    public void setType(MessageType type) {
        this.type = type;
    }


    /**
     * Default constructor
     */
    public ValidationMessageDTO() {
        super();
    }

    /**
     * Default constructor
     * @param type
     * @param message
     */
    public ValidationMessageDTO(MessageType type, String message) {
        super();
        this.message = message;
        this.type = type;
    }

}

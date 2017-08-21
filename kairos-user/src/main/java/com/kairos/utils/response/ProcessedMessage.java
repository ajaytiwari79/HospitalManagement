package com.kairos.utils.response;


/**
 * ProcessedMessage with common response fields
 */
public class ProcessedMessage {

    private String message;
    private int statusCode;
    private String description;
    private String response;
    private Object data;



    /**
     * getResponse
     * @return response
     */
    public String getResponse() {
        return response;
    }

    /**
     * setResponse
     * @param response
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     *  getData
     * @return object
     */
    public Object getData() {
        return data;
    }

    /**
     * setData
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * getMessage
     * @return message
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
     * getStatusCode
     * @return statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * setStatusCode
     * @param statusCode
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * getDescription
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * setDescription
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Default constructor
     */
    public ProcessedMessage() {
    }

    /**
     * Default constructor
     * @param message
     * @param response
     */
    public ProcessedMessage(String message, String response) {
        this.message = message;
        this.response = response;
    }

    /**
     * Default constructor
     * @param message
     * @param statusCode
     * @param description
     * @param data
     */
    public ProcessedMessage(String message, int statusCode, String description, Object data) {
        this.message = message;
        this.statusCode = statusCode;
        this.description = description;
        this.data = data;
    }

    /**
     * Default constructor
     * @param message
     * @param description
     * @param data
     */
    public ProcessedMessage(String message, String description, Object data) {
        this.message = message;
        this.description = description;
        this.data = data;
    }

}

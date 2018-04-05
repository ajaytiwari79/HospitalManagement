package com.kairos.response.dto.web;

/**
 * Created by oodles on 13/1/17.
 */
public class TransResponseWrapper {
    private String message;

    private String id;

    private String result;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getResult ()
    {
        return result;
    }

    public void setResult (String result)
    {
        this.result = result;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", id = "+id+", result = "+result+"]";
    }
}

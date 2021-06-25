package com.kairos.enums;

import java.io.Serializable;

/**
 * Created by prabjot on 19/10/16.
 */
public enum RequestType implements Serializable {

    SKILLS("Skills"), SERVICE("Service"), EXPERTISE("Expertise");
    public String value;

    RequestType(String value){
        this.value = value;
    }

    public static RequestType getByValue(final String value){
        for (RequestType requestType : RequestType.values()) {
            if (requestType.value.equals(value)) {
                return requestType;
            }
        }
        return null;
    }


}

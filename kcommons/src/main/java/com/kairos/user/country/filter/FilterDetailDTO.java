package com.kairos.user.country.filter;

/**
 * Created by prerna on 30/4/18.
 */

public class FilterDetailDTO {

    private String id;
    private String value;

    public FilterDetailDTO(){
        // default Constructor
    }

    public FilterDetailDTO(String id, String value){
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

package com.kairos.user.organization;

/**
 * Created by prerna on 26/2/18.
 */
public class OrganizationCategoryDTO {

    private String name;

    private String value;

    private int count;


    public OrganizationCategoryDTO(){
        // default constructor
    }

    public OrganizationCategoryDTO(String name, String value){
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

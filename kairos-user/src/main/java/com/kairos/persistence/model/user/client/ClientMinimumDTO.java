package com.kairos.persistence.model.user.client;

/**
 * Created by oodles on 23/1/17.
 */
public class ClientMinimumDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String cprnumber;

    public ClientMinimumDTO(String firstName, String lastName, String cprNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cprnumber = cprNumber;
    }

    public String getCprnumber() {
        return cprnumber;
    }

    public void setCprnumber(String cprnumber) {
        this.cprnumber = cprnumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ClientMinimumDTO() {
    }
}

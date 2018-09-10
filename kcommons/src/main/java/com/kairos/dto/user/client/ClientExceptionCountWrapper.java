package com.kairos.dto.user.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.client_exception.ClientExceptionCount;
import com.kairos.enums.Gender;

import java.util.List;

/**
 * this wrapper will wraps basic info infor client(citizen) and their client exceptions.
 * this is designed for mobile view
 * Created by prabjot on 14/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientExceptionCountWrapper {

    private String firstName;
    private String lastName;
    private Long id;
    private Gender gender;
    private List<ClientExceptionCount> clientExceptionCounts;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<ClientExceptionCount> getClientExceptionCounts() {
        return clientExceptionCounts;
    }

    public void setClientExceptionCounts(List<ClientExceptionCount> clientExceptionCounts) {
        this.clientExceptionCounts = clientExceptionCounts;
    }
}

package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVO {

    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Long countryId;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public UserVO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVO userVO = (UserVO) o;
        return Objects.equals(id, userVO.id) &&
                Objects.equals(userName, userVO.userName) &&
                Objects.equals(firstName, userVO.firstName) &&
                Objects.equals(lastName, userVO.lastName) &&
                Objects.equals(email, userVO.email) &&
                Objects.equals(countryId, userVO.countryId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userName, firstName, lastName, email, countryId);
    }
}

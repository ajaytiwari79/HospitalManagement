package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Embeddable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class UserVO {

    private Long userId;
    private String assessmentUserName;
    private String assessmentUserFirstName;
    private String assessmentUserLastName;
    private String assessmentUserEmail;
    private Long countryId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAssessmentUserName() {
        return assessmentUserName;
    }

    public void setAssessmentUserName(String assessmentUserName) {
        this.assessmentUserName = assessmentUserName;
    }

    public String getAssessmentUserFirstName() {
        return assessmentUserFirstName;
    }

    public void setAssessmentUserFirstName(String assessmentUserFirstName) {
        this.assessmentUserFirstName = assessmentUserFirstName;
    }

    public String getAssessmentUserLastName() {
        return assessmentUserLastName;
    }

    public void setAssessmentUserLastName(String assessmentUserLastName) {
        this.assessmentUserLastName = assessmentUserLastName;
    }

    private String getAssessmentUserEmail() {
        return assessmentUserEmail;
    }

    public void setAssessmentUserEmail(String assessmentUserEmail) {
        this.assessmentUserEmail = assessmentUserEmail;
    }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public UserVO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVO userVO = (UserVO) o;
        return Objects.equals(userId, userVO.userId) &&
               /* Objects.equals(userName, userVO.userName) &&
                Objects.equals(firstName, userVO.firstName) &&
                Objects.equals(lastName, userVO.lastName) &&*/
                Objects.equals(assessmentUserEmail, userVO.getAssessmentUserEmail());/* &&
                Objects.equals(countryId, userVO.countryId);*/
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, assessmentUserName, assessmentUserFirstName, assessmentUserLastName, assessmentUserEmail, countryId);
    }
}

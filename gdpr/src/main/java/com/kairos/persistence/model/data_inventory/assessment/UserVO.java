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

    public String getAssessmentUserEmail() {
        return assessmentUserEmail;
    }

    public void setAssessmentUserEmail(String assessmentUserEmail) {
        this.assessmentUserEmail = assessmentUserEmail;
    }

    public UserVO() {
    }

    public UserVO(Long userId, String assessmentUserName, String assessmentUserEmail , String assessmentUserFirstName , String assessmentUserLastName ){
        this.userId = userId;
        this.assessmentUserName = assessmentUserName;
        this.assessmentUserEmail = assessmentUserEmail;
        this.assessmentUserFirstName=assessmentUserFirstName;
        this.assessmentUserLastName=assessmentUserLastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVO userVO = (UserVO) o;
        return Objects.equals(userId, userVO.userId) &&
                Objects.equals(assessmentUserName, userVO.assessmentUserName) &&
                Objects.equals(assessmentUserEmail, userVO.assessmentUserEmail);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, assessmentUserName, assessmentUserEmail);
    }
}

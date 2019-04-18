package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long userId;
    private String assessmentUserName;
    private String assessmentUserFirstName;
    private String assessmentUserLastName;
    private String assessmentUserEmail;

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

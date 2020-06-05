package com.kairos.persistence.model.staff.personal_details;

import com.kairos.enums.Gender;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.user.employment.Employment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class StaffEmploymentWithTag {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private List<Employment> employments;
    private List<Tag> tags;
    private String city;
    private String currentStatus;
    private String dateOfBirth;
    private Gender gender;
    private String profilePic;
    private String user_id;
    private String province;
}

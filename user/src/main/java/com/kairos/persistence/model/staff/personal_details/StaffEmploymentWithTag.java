package com.kairos.persistence.model.staff.personal_details;

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
    private String firstName;
    private String lastName;
    private List<Employment> employments;
    private List<Tag> tags;

}

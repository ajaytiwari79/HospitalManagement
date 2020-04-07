package com.kairos.persistence.model.user.expertise.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.expertise.ChildCareDays;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorDays;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * @author pradeep
 * @date - 30/10/18
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class CareDaysQueryResult {
    private List<SeniorDays> seniorDays;
    private List<ChildCareDays> childCareDays;
    private Long expertiseId;
}

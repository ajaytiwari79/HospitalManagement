package com.kairos.persistence.model.user.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;

/**
 * Created by prerna on 1/5/18.
 */
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class FilterSelectionQueryResult {

    private String id;
    private String value;
    private LocalDate startDate;// used in case of expertise
    private LocalDate endDate; // used in case of expertise.


    public FilterSelectionQueryResult(String id, String value) {
        this.id = id;
        this.value = value;
    }
}

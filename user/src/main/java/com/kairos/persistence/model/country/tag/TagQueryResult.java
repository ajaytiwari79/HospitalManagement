package com.kairos.persistence.model.country.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prerna on 13/11/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TagQueryResult {
    private long id;
    private String name;
    private String masterDataType;
    private Boolean countryTag;
    private PenaltyScore penaltyScore;
    private Long orgTypeId;
    private List<Long> orgSubTypeIds;
    private String color;
    private String shortName;
    private String ultraShortName;
}

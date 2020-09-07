package com.kairos.persistence.model.country.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private Date startDate;
    private Date endDate;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations;
}

package com.kairos.persistence.model.user.expertise.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * @author pradeep
 * @date - 30/10/18
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CareDaysQueryResult {

    private Long id;
    private Integer from;
    private Integer to;
    private Integer leavesAllowed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getLeavesAllowed() {
        return leavesAllowed;
    }

    public void setLeavesAllowed(Integer leavesAllowed) {
        this.leavesAllowed = leavesAllowed;
    }
}

package com.kairos.persistence.model.country.reason_code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.reason_code.ReasonCodeType;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;

/**
 * Created by pavan on 23/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class ReasonCodeResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private ReasonCodeType reasonCodeType;
    private BigInteger timeTypeId;

    public ReasonCodeResponseDTO() {
        //Default Constructor
    }

    public ReasonCodeResponseDTO(Long id, String name, String code, String description, ReasonCodeType reasonCodeType) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.reasonCodeType = reasonCodeType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReasonCodeType getReasonCodeType() {
        return reasonCodeType;
    }

    public void setReasonCodeType(ReasonCodeType reasonCodeType) {
        this.reasonCodeType = reasonCodeType;
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }
}

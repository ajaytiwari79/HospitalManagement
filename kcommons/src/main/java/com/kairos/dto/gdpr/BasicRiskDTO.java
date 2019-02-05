package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.RiskSeverity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicRiskDTO {

    protected Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed",regexp ="^[a-zA-Z\\s]+$" )
    protected String name;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    protected String description;

    @NotBlank(message = "error.message.risk.recommendation")
    protected String riskRecommendation;

    @NotNull(message = "error.message.risk.level")
    protected RiskSeverity riskLevel;

    public String getName() { return name.trim(); }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description.trim(); }

    public void setDescription(String description) { this.description = description; }

    public String getRiskRecommendation() { return riskRecommendation.trim(); }

    public void setRiskRecommendation(String riskRecommendation) { this.riskRecommendation = riskRecommendation; }

    public RiskSeverity getRiskLevel() { return riskLevel; }

    public void setRiskLevel(RiskSeverity riskLevel) { this.riskLevel = riskLevel; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

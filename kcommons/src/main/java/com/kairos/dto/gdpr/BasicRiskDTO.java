package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.RiskSeverity;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicRiskDTO that = (BasicRiskDTO) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    public String getName() { return name.trim(); }

    public String getDescription() { return description.trim(); }

    public String getRiskRecommendation() { return riskRecommendation.trim(); }

}

package com.kairos.persistence.model.country.functions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Unit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by pavan on 14/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class FunctionDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean amountEditableAtUnit;
    private BigDecimal amount;
    private List<Unit> unions;
    private List<Level> organizationLevels;
    private String icon;
    private List<LocalDate> appliedDates;
    private Long employmentId;
    private int code;


    public FunctionDTO(Long id, String name, String description, LocalDate startDate, LocalDate endDate, List<Unit> unions, List<Level> organizationLevels, String icon,int code) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unions = unions;
        this.organizationLevels = organizationLevels;
        this.icon = icon;
        this.code=code;
    }
}

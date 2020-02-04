package com.kairos.shiftplanning.domain.wta;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)

/**
 * @Author pradeep singh
 *
 * @Modified added organization and staff for personal copy
 */
@Getter
@Setter
@NoArgsConstructor
public class WorkingTimeAgreement  {
   private BigInteger id;
    @NotNull(message = "error.WorkingTimeAgreement.name.notnull")
    private String name;

    private String description;
    // This will be only used when the countryId will update the WTA a new Copy of WTA will be assigned to organization having state disabled
    private boolean disabled;
    private Long employmentId;

    private Long countryId;

   // private Organization organization;

    private List<BigInteger> ruleTemplateIds;
    private List<WTABaseRuleTemplate> ruleTemplates;
    // to make a history
    private BigInteger parentWTA;

    private BigInteger countryParentWTA;

    private BigInteger organizationParentWTA;


    private List<BigInteger> tags = new ArrayList<>();

    private LocalDate startDate;
    private LocalDate endDate;
    private Date expiryDate;

    public List<BigInteger> getRuleTemplateIds() {
        return ruleTemplateIds=Optional.ofNullable(ruleTemplateIds).orElse(new ArrayList<>());
    }



    public WorkingTimeAgreement(BigInteger id, @NotNull(message = "error.WorkingTimeAgreement.name.notnull") String name, String description, LocalDate startDate, LocalDate endDate, Date expiryDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
    }

    public WorkingTimeAgreement basicDetails() {
        return new WorkingTimeAgreement(this.id, this.name, this.description, this.startDate, this.endDate, this.expiryDate);
    }


}

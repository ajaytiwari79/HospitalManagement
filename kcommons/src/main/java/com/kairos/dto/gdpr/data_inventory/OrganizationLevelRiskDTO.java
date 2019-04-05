package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.BasicRiskDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationLevelRiskDTO  extends BasicRiskDTO {

    //property may add in future
   // @NotNull(message = "error.message.risk.due.date")
    //private LocalDate dueDate;
    //private Staff riskOwner;

    private Long organizationId;
    private boolean reminderActive;
    private int daysToReminderBefore;

}

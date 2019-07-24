package com.kairos.persistence.model.master_data.default_proc_activity_setting;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.risk_management.Risk;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MasterProcessingActivity extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();

    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();

    @ElementCollection
    private List <ServiceCategory> organizationServices = new ArrayList<>();

    @ElementCollection
    private List <SubServiceCategory> organizationSubServices = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "processingActivity_id")
    private List<Risk> risks  = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="masterProcessingActivity_id")
    private MasterProcessingActivity masterProcessingActivity;

    @OneToMany(mappedBy="masterProcessingActivity", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<MasterProcessingActivity> subProcessingActivities = new ArrayList<>();

    private Long countryId;
    private boolean subProcessActivity;
    private boolean hasSubProcessingActivity;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;

   public MasterProcessingActivity(String name, String description, SuggestedDataStatus suggestedDataStatus, Long countryId) {
        this.name = name;
        this.description = description;
        this.suggestedDataStatus=suggestedDataStatus;
        this.countryId = countryId;
    }

}

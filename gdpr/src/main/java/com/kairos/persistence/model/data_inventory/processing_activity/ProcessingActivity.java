package com.kairos.persistence.model.data_inventory.processing_activity;


import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.*;
import com.kairos.persistence.model.risk_management.Risk;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProcessingActivity extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @Embedded
    private ManagingOrganization managingDepartment;
    @Embedded
    private Staff processOwner;
    private Long countryId;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<ProcessingPurpose> processingPurposes  = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private List<DataSource> dataSources  = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private List<TransferMethod> transferMethods  = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private List<AccessorParty> accessorParties  = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private List<ProcessingLegalBasis> processingLegalBasis  = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Asset> assets  = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="processingActivity_id")
    private ProcessingActivity processingActivity;
    @OneToMany(mappedBy = "processingActivity", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<ProcessingActivity> subProcessingActivities  = new ArrayList<>();
    @OneToOne
    private ResponsibilityType responsibilityType;
    private Integer controllerContactInfo;
    private Integer dpoContactInfo;
    private Integer jointControllerContactInfo;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private Integer dataRetentionPeriod;
    private boolean isSubProcessingActivity;
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<RelatedDataSubject> dataSubjectList = new ArrayList<>();
    private Long organizationId;
    private boolean active = true;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Risk> risks  = new ArrayList<>();
    private boolean suggested;

    public ProcessingActivity(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public ProcessingActivity(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    @Override
    public void delete() {
        super.delete();
        this.setDeleted(true);
        this.getRisks().forEach(BaseEntity::delete);
        if(!this.getSubProcessingActivities().isEmpty()) {
            this.getSubProcessingActivities().forEach(ProcessingActivity::delete);
        }

    }
}

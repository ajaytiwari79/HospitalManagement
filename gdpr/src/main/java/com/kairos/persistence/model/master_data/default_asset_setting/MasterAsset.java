package com.kairos.persistence.model.master_data.default_asset_setting;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.model.embeddables.ServiceCategory;
import com.kairos.persistence.model.embeddables.SubServiceCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MasterAsset extends BaseEntity {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();
    @ElementCollection
    private List<OrganizationSubType> organizationSubTypes = new ArrayList<>();
    @ElementCollection
    private List<ServiceCategory> organizationServices = new ArrayList<>();
    @ElementCollection
    private List<SubServiceCategory> organizationSubServices = new ArrayList<>();
    private Long countryId;
    @OneToOne(fetch = FetchType.EAGER)
    private AssetType assetType;
    @OneToOne(fetch = FetchType.EAGER)
    private AssetType subAssetType;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterAsset(String name, String description, Long countryId, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.suggestedDate = suggestedDate;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public MasterAsset(String name, String description, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.suggestedDataStatus = suggestedDataStatus;

    }

}

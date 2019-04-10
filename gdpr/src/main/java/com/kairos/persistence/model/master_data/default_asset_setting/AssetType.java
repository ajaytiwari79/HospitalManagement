package com.kairos.persistence.model.master_data.default_asset_setting;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.risk_management.Risk;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class AssetType extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty or null")
    @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private Long organizationId;
    private boolean isSubAssetType;
    private boolean hasSubAssetType;
    private SuggestedDataStatus suggestedDataStatus;
    private LocalDate suggestedDate;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "assetType_id")
    private List<Risk> risks  = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="assetType_id")
    private AssetType assetType;
    @OneToMany(mappedBy="assetType",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AssetType> subAssetTypes= new ArrayList<>();



    public AssetType(String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus=suggestedDataStatus;
    }

    public AssetType(@NotBlank(message = "error.message.name.notNull.orEmpty or null") @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name,Long organizationId, boolean isSubAssetType) {
        this.name = name;
        this.organizationId = organizationId;
        this.isSubAssetType = isSubAssetType;
    }

    public AssetType( Long countryId,  SuggestedDataStatus suggestedDataStatus) {
        this.countryId = countryId;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    @Override
    public void delete() {
        this.setDeleted(true);
        this.getRisks().forEach(BaseEntity::delete);
        if(!this.getSubAssetTypes().isEmpty()) {
            this.getSubAssetTypes().forEach(AssetType::delete);
        }
    }
}

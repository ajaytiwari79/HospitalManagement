package com.kairos.persistence.model.master_data.default_asset_setting;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DataDisposal extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.name.special.character.notAllowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private SuggestedDataStatus suggestedDataStatus;
    private LocalDate suggestedDate;
    private Long organizationId;


    public String getName() {
        return name.trim();
    }

    public DataDisposal(@NotBlank(message = "error.message.name.notNull.orEmpty") String name, Long countryId, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.countryId = countryId;
        this.suggestedDataStatus = suggestedDataStatus;
    }


    public DataDisposal(@NotBlank(message = "error.message.name.notNull.orEmpty") String name, Long organizationId){
        this.name = name;
        this.organizationId = organizationId;
    }

    public DataDisposal(Long countryId, @NotBlank(message = "error.message.name.notNull.orEmpty") String name){
        this.name = name;
        this.countryId = countryId;
    }

    public DataDisposal(@NotBlank(message = "error.message.name.notNull.orEmpty") @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name) {
        this.name = name;
    }
}

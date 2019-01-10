package com.kairos.persistence.model.clause_tag;

import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import java.util.prefs.BackingStoreException;


@Entity
public class ClauseTagMD extends BaseEntity {

    @NotBlank(message = "Name cannot be  empty")
    private String name;
    private boolean defaultTag;
    private Long countryId;

    public ClauseTagMD(@NotBlank(message = "Name cannot be  empty") String name) {
        this.name = name;
    }

    public ClauseTagMD() {
    }

    public boolean isDefaultTag() { return defaultTag; }

    public void setDefaultTag(boolean defaultTag) { this.defaultTag = defaultTag; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

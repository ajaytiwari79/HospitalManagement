package com.kairos.persistence.model.clause_tag;

import com.kairos.persistence.model.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class ClauseTag extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    private boolean defaultTag;
    private Long countryId;
    private Long organizationId;

    public ClauseTag(@NotBlank(message = "error.message.name.notNull.orEmpty") String name) {
        this.name = name;
    }

    public ClauseTag(@NotBlank(message = "error.message.name.notNull.orEmpty") String name, boolean defaultTag) {
        this.name = name;
        this.defaultTag = defaultTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClauseTag clauseTag = (ClauseTag) o;
        return defaultTag == clauseTag.defaultTag &&
                Objects.equals(name, clauseTag.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, defaultTag);
    }
}

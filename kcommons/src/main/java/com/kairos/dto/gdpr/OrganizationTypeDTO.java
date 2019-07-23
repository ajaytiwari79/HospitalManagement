package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrganizationTypeDTO {

    @NotNull(message = "error.message.id.notnull")
    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationTypeDTO that = (OrganizationTypeDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}

package com.kairos.persistence.model.master_data.data_category_element;

import com.kairos.persistence.model.common.BaseEntity;
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
public class DataCategory extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z0-9\\s]+$")
    private String name;

    // empty array to get rid of null pointer
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "data_category_id")
    private List<DataElement> dataElements=new ArrayList<>();
    private Long countryId;
    private Long organizationId;

    public DataCategory(@NotBlank(message = "error.message.name.notNull.orEmpty") @Pattern(message = "error.message.special.character.notAllowed", regexp = "^[a-zA-Z0-9\\s]+$") String name, List<DataElement> dataElements, Long organizationId) {
        this.name = name;
        this.dataElements = dataElements;
        this.organizationId = organizationId;
    }

    public DataCategory(@NotBlank(message = "error.message.name.notNull.orEmpty") @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z0-9\\s]+$") String name) {
        this.name = name;
    }
}

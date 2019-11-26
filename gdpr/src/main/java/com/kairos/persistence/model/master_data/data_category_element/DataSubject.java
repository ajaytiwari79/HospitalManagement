package com.kairos.persistence.model.master_data.data_category_element;

import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DataSubject extends BaseEntity {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.special.character.notAllowed", regexp = "^[a-zA-Z0-9\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();
    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="DataSubjectCategories",
            joinColumns = @JoinColumn( name="data_subject_id"),
            inverseJoinColumns = @JoinColumn( name="data_category_id")
    )
    private List<DataCategory> dataCategories=new ArrayList<>();
    private Long countryId;
    private Long organizationId;


    public DataSubject(String name, String description, List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes,
                       List<DataCategory> dataCategories) {
        this.name = name;
        this.description = description;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.dataCategories = dataCategories;
    }


    public DataSubject(@NotBlank(message = "error.message.name.notNull.orEmpty")
                               @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name, @NotBlank(message = "error.message.description.notNull.orEmpty") String description, @NotEmpty List<DataCategory> dataCategories) {
        this.name = name;
        this.description = description;
        this.dataCategories = dataCategories;
    }

    public DataSubject(@NotBlank(message = "error.message.name.notNull.orEmpty") @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name, @NotBlank(message = "error.message.description.notNull.orEmpty") String description) {
        this.name = name;
        this.description=description;
    }

}

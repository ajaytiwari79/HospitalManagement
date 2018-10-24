package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.DataCategoryDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
/*
* may be rquired in future
* */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationDataSubjectDTO {


    private BigInteger id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private OrganizationDataCategoryDTO dataCategory;

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name.trim();}


    public void setName(String name) { this.name = name; }

    public OrganizationDataCategoryDTO getDataCategory() { return dataCategory; }

    public void setDataCategory(OrganizationDataCategoryDTO dataCategory) { this.dataCategory = dataCategory; }

    /*
    * if multiple data subject create at same time
    * */
    /*@NotEmpty(message = "Data Subject name Can't be Empty")
    private Set<String> dataSubjectNames;

    @Valid
    private List<DataCategoryDTO> dataCategories;

    public Set<String> getDataSubjectNames() { return dataSubjectNames; }

    public void setDataSubjectNames(Set<String> dataSubjectNames) { this.dataSubjectNames = dataSubjectNames; }

    public List<DataCategoryDTO> getDataCategories() { return dataCategories; }

    public void setDataCategories(List<DataCategoryDTO> dataCategories) { this.dataCategories = dataCategories; }
*/
}

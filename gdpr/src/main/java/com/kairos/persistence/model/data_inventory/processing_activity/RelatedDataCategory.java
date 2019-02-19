package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class RelatedDataCategory  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private
    Long id;


    @NotNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private List<RelatedDataElements> dataElements;

    public RelatedDataCategory() {
    }

    public List<RelatedDataElements> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<RelatedDataElements> dataElements) {
        this.dataElements = dataElements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RelatedDataCategory(Long id, @NotNull String name, @NotEmpty List<RelatedDataElements> dataElements) {
        this.id = id;
        this.name = name;
        this.dataElements = dataElements;
    }


}

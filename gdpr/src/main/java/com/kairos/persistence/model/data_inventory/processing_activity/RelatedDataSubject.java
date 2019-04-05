package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class RelatedDataSubject  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private
    Long id;

    @NotNull
    private String name;

    public RelatedDataSubject() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(cascade = CascadeType.ALL)
    private List<RelatedDataCategory> dataCategories = new ArrayList<>();

    public List<RelatedDataCategory> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(List<RelatedDataCategory> dataCategories) {
        this.dataCategories = dataCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RelatedDataSubject(Long id, @NotNull String name,List<RelatedDataCategory> relatedDataCategories) {
        this.id = id;
        this.name = name;
        this.dataCategories=relatedDataCategories;
    }
}

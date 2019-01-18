package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class RelatedDataSubject  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    private String name;

    public RelatedDataSubject() {
    }

    public RelatedDataSubject(Long id ) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(cascade = CascadeType.ALL)
    private List<RelatedDataCategory> dataCategories;

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

    public RelatedDataSubject(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }
}

package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.BaseEntity;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class RelatedDataElements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @NotNull
    private String name;

    public RelatedDataElements() {
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
}

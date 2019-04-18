package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class RelatedDataSubject  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @NotNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private List<RelatedDataCategory> dataCategories = new ArrayList<>();

    public RelatedDataSubject(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }
}

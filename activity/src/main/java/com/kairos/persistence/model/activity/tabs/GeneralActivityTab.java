package com.kairos.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralActivityTab implements Serializable {

    private String name;
    private String code;
    private String printoutSymbol;
    private BigInteger categoryId;
    private Boolean colorPresent;
    private String backgroundColor;
    private String description;
    private boolean isActive = true;
    private String shortName;
    private boolean eligibleForUse = true;
    private String originalIconName;
    private String modifiedIconName;
    private String ultraShortName;
    private LocalDate startDate;
    private LocalDate endDate;


    private List<BigInteger> tags = new ArrayList<>();

    public GeneralActivityTab(String name, String description, String ultraShortName) {
        this.name = name;
        this.description = description;
        this.ultraShortName = ultraShortName;
    }

}


package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityBasicDTO {

    private Long id;
    private String name;
    private boolean subProcess;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public boolean isSubProcess() { return subProcess; }

    public void setSubProcess(boolean subProcess) {this.subProcess = subProcess;
    }
}

package com.kairos.response.dto.web.pay_group_area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.region.Municipality;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 12/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayGroupAreaResponse {
    private List<Level> organizationLevels = new ArrayList<>();
    private List<Municipality> municipalities = new ArrayList<>();

    public PayGroupAreaResponse() {
        // default constructor
    }

    public List<Level> getOrganizationLevels() {
        return organizationLevels;
    }

    public void setOrganizationLevels(List<Level> organizationLevels) {
        this.organizationLevels = organizationLevels;
    }

    public List<Municipality> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<Municipality> municipalities) {
        this.municipalities = municipalities;
    }


    public PayGroupAreaResponse(List<Level> organizationLevels, List<Municipality> municipalities) {
        this.organizationLevels = organizationLevels;
        this.municipalities = municipalities;

    }


}

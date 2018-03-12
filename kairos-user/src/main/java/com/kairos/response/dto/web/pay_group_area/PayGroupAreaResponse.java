package com.kairos.response.dto.web.pay_group_area;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.region.Municipality;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 12/3/18.
 */
public class PayGroupAreaResponse {
    private List<Level> organizationLevels = new ArrayList<>();
    private List<Municipality> municipalities = new ArrayList<>();
    private List<PayGroupAreaQueryResult> payGroupAreas = new ArrayList<>();

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

    public List<PayGroupAreaQueryResult> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(List<PayGroupAreaQueryResult> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
    }

    public PayGroupAreaResponse(List<Level> organizationLevels, List<Municipality> municipalities, List<PayGroupAreaQueryResult> payGroupAreas) {
        this.organizationLevels = organizationLevels;
        this.municipalities = municipalities;
        this.payGroupAreas = payGroupAreas;
    }
}

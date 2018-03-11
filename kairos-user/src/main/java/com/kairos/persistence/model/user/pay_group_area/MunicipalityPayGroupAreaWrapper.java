package com.kairos.persistence.model.user.pay_group_area;

import com.kairos.persistence.model.user.region.Municipality;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 10/3/18.
 */

@QueryResult
public class MunicipalityPayGroupAreaWrapper {

    private Municipality municipality;
    private PayGroupArea payGroupArea;

    public MunicipalityPayGroupAreaWrapper() {
        // default constructor
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public PayGroupArea getPayGroupArea() {
        return payGroupArea;
    }

    public void setPayGroupArea(PayGroupArea payGroupArea) {
        this.payGroupArea = payGroupArea;
    }
}

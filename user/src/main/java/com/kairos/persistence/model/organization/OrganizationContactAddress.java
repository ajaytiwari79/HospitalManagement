package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 3/3/17.
 */
@QueryResult
public class OrganizationContactAddress {

    private Unit unit;
    private ContactAddress contactAddress;
    private ZipCode zipCode;
    private Municipality municipality;


    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(ContactAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    public ZipCode getZipCode() {
        return zipCode;
    }

    public void setZipCode(ZipCode zipCode) {
        this.zipCode = zipCode;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public Municipality getMunicipality() {

        return municipality;
    }
}


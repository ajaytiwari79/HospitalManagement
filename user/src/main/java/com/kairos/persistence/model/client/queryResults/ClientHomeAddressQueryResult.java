package com.kairos.persistence.model.client.queryResults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 3/5/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class ClientHomeAddressQueryResult {

    private Client citizen;
    private ContactAddress homeAddress;
    private ZipCode zipCode;
    private Municipality municipality;
    private Long localAreaTagId;

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public void setZipCode(ZipCode zipCode) {
        this.zipCode = zipCode;
    }

    public void setHomeAddress(ContactAddress homeAddress) {

        this.homeAddress = homeAddress;
    }

    public ContactAddress getHomeAddress() {

        return homeAddress;
    }

    public ZipCode getZipCode() {
        return zipCode;
    }

    public Client getCitizen() {
        return citizen;
    }

    public void setCitizen(Client citizen) {
        this.citizen = citizen;
    }

    public Long getLocalAreaTagId() {
        return localAreaTagId;
    }

    public void setLocalAreaTagId(Long localAreaTagId) {
        this.localAreaTagId = localAreaTagId;
    }
}

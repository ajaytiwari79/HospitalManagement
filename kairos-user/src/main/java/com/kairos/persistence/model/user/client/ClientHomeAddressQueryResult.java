package com.kairos.persistence.model.user.client;

import org.springframework.data.neo4j.annotation.QueryResult;

import com.kairos.persistence.model.user.region.ZipCode;

/**
 * Created by prabjot on 3/5/17.
 */
@QueryResult
public class ClientHomeAddressQueryResult {

    private ContactAddress homeAddress;
    private ZipCode zipCode;

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
}

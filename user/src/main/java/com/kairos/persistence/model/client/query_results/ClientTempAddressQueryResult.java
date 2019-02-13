package com.kairos.persistence.model.client.query_results;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.default_data.HousingType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 9/5/17.
 */
@QueryResult
public class ClientTempAddressQueryResult {

    private ContactAddress temporaryAddress;
    private ZipCode temporaryZipCode;
    private Municipality temporaryAddressMunicipality;
    private HousingType temporaryAddressHousingType;

    public void setTemporaryAddressHousingType(HousingType temporaryAddressHousingType) {
        this.temporaryAddressHousingType = temporaryAddressHousingType;
    }

    public HousingType getTemporaryAddressHousingType() {

        return temporaryAddressHousingType;
    }

    public void setTemporaryAddressMunicipality(Municipality temporaryAddressMunicipality) {
        this.temporaryAddressMunicipality = temporaryAddressMunicipality;
    }

    public Municipality getTemporaryAddressMunicipality() {

        return temporaryAddressMunicipality;
    }

    public void setTemporaryAddress(ContactAddress temporaryAddress) {
        this.temporaryAddress = temporaryAddress;
    }

    public void setTemporaryZipCode(ZipCode temporaryZipCode) {
        this.temporaryZipCode = temporaryZipCode;
    }

    public ContactAddress getTemporaryAddress() {

        return temporaryAddress;
    }

    public ZipCode getTemporaryZipCode() {
        return temporaryZipCode;
    }

    @Override
    public String toString() {
        return "ClientTempAddressQueryResult{" +
                "temporaryAddress=" + temporaryAddress +
                ", temporaryZipCode=" + temporaryZipCode +
                '}';
    }
}

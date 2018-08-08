package com.kairos.persistence.model.client.queryResults;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.HousingType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 9/5/17.
 */
@QueryResult
public class ClientAddressQueryResult {

    private ContactAddress homeAddress;
    private ZipCode homeZipCode;
    private Municipality homeAddressMunicipality;
    private ContactAddress secondaryAddress;
    private ZipCode secondaryZipCode;
    private Municipality secondaryAddressMunicipality;
    private ContactAddress partnerAddress;
    private ZipCode partnerZipCode;
    private Municipality partnerAddressMunicipality;
    private HousingType homeAddressHousingType;

    public void setHomeAddressHousingType(HousingType homeAddressHousingType) {
        this.homeAddressHousingType = homeAddressHousingType;
    }

    public void setSecondaryAddressHousingType(HousingType secondaryAddressHousingType) {
        this.secondaryAddressHousingType = secondaryAddressHousingType;
    }

    public void setPartnerAddressHousingType(HousingType partnerAddressHousingType) {
        this.partnerAddressHousingType = partnerAddressHousingType;
    }

    public HousingType getHomeAddressHousingType() {

        return homeAddressHousingType;
    }

    public HousingType getSecondaryAddressHousingType() {
        return secondaryAddressHousingType;
    }

    public HousingType getPartnerAddressHousingType() {
        return partnerAddressHousingType;
    }

    private HousingType secondaryAddressHousingType;
    private HousingType partnerAddressHousingType;

    public void setHomeAddressMunicipality(Municipality homeAddressMunicipality) {
        this.homeAddressMunicipality = homeAddressMunicipality;
    }

    public void setSecondaryAddressMunicipality(Municipality secondaryAddressMunicipality) {
        this.secondaryAddressMunicipality = secondaryAddressMunicipality;
    }

    public void setPartnerAddressMunicipality(Municipality partnerAddressMunicipality) {
        this.partnerAddressMunicipality = partnerAddressMunicipality;
    }

    public Municipality getHomeAddressMunicipality() {

        return homeAddressMunicipality;
    }

    public Municipality getSecondaryAddressMunicipality() {
        return secondaryAddressMunicipality;
    }

    public Municipality getPartnerAddressMunicipality() {
        return partnerAddressMunicipality;
    }

    public ContactAddress getHomeAddress() {
        return homeAddress;
    }

    public ZipCode getHomeZipCode() {
        return homeZipCode;
    }

    public ContactAddress getSecondaryAddress() {
        return secondaryAddress;
    }

    public ZipCode getSecondaryZipCode() {
        return secondaryZipCode;
    }

    public ContactAddress getPartnerAddress() {
        return partnerAddress;
    }

    public ZipCode getPartnerZipCode() {
        return partnerZipCode;
    }


    public void setHomeAddress(ContactAddress homeAddress) {
        this.homeAddress = homeAddress;
    }

    public void setHomeZipCode(ZipCode homeZipCode) {
        this.homeZipCode = homeZipCode;
    }

    public void setSecondaryAddress(ContactAddress secondaryAddress) {
        this.secondaryAddress = secondaryAddress;
    }

    public void setSecondaryZipCode(ZipCode secondaryZipCode) {
        this.secondaryZipCode = secondaryZipCode;
    }

    public void setPartnerAddress(ContactAddress partnerAddress) {
        this.partnerAddress = partnerAddress;
    }

    public void setPartnerZipCode(ZipCode partnerZipCode) {
        this.partnerZipCode = partnerZipCode;
    }

    @Override
    public String toString() {
        return "ClientAddressQueryResult{" +
                "homeAddress=" + homeAddress +
                ", homeZipCode=" + homeZipCode +
                ", homeAddressMunicipality=" + homeAddressMunicipality +
                ", secondaryAddress=" + secondaryAddress +
                ", secondaryZipCode=" + secondaryZipCode +
                ", secondaryAddressMunicipality=" + secondaryAddressMunicipality +
                ", partnerAddress=" + partnerAddress +
                ", partnerZipCode=" + partnerZipCode +
                ", partnerAddressMunicipality=" + partnerAddressMunicipality +
                '}';
    }
}

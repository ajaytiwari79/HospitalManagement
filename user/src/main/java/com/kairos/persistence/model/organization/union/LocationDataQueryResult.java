package com.kairos.persistence.model.organization.union;

import com.kairos.dto.user.staff.client.ContactAddressDTO;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class LocationDataQueryResult {

    private Long locationId;
    private ContactAddress address;
    private ZipCode zipCode;
    private Municipality municipality;
    private List<Municipality> municipalities;
    private Province province;
    private Region region;


    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }



    public ZipCode getZipCode() {
        return zipCode;
    }

    public void setZipCode(ZipCode zipCode) {
        this.zipCode = zipCode;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public List<Municipality> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<Municipality> municipalities) {
        this.municipalities = municipalities;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }


    public ContactAddress getAddress() {
        return address;
    }

    public void setAddress(ContactAddress address) {
        this.address = address;
    }
}

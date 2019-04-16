package com.kairos.persistence.model.organization.union;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class UnionDataQueryResult {

    private Organization union;
    private List<Sector> sectors;
    private List<Location> locations;
    private ContactAddress address;
    private ZipCode zipCode;
    private Municipality municipality;
    private List<Municipality> municipalities;
    private Country country;



    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
        this.union = union;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(List<Sector> sectors) {
        this.sectors = sectors;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public ContactAddress getAddress() {
        return address;
    }

    public void setAddress(ContactAddress address) {
        this.address = address;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}

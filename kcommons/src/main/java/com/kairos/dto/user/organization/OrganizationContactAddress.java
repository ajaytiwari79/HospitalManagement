package com.kairos.dto.user.organization;

import com.kairos.dto.activity.address.ContactAddressDTO;
import com.kairos.dto.user.organization.address.ZipCode;

/**
 * Created by prabjot on 3/3/17.
 */
public class OrganizationContactAddress {

    private OrganizationDTO organization;
    private ContactAddressDTO contactAddress;
    private ZipCode zipCode;
    private Municipality municipality;

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public ContactAddressDTO getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(ContactAddressDTO contactAddress) {
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


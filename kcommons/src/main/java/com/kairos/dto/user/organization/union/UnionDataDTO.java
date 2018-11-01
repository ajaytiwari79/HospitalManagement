package com.kairos.dto.user.organization.union;

import com.kairos.dto.user.organization.MunicipalityDTO;
import com.kairos.dto.user.staff.client.ContactAddressDTO;
import java.util.List;
public class UnionDataDTO {

    private Long id;
    private String name;
    private List<SectorDTO> sectors;
    private ContactAddressDTO mainAddress;
    private List<LocationDTO> locations;
    private List<MunicipalityDTO> municipalities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SectorDTO> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorDTO> sectors) {
        this.sectors = sectors;
    }

    public ContactAddressDTO getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(ContactAddressDTO mainAddress) {
        this.mainAddress = mainAddress;
    }

    public List<LocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }


    public List<MunicipalityDTO> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<MunicipalityDTO> municipalities) {
        this.municipalities = municipalities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

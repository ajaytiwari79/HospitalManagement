package com.kairos.dto.user.organization.union;

import com.kairos.dto.user.organization.MunicipalityDTO;
import com.kairos.dto.user.staff.client.ContactAddressDTO;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class LocationDTO {

    private Long id;
    @NotEmpty(message="message.location.name.null")
    @NotNull(message="message.location.name.null")
    private String name;

    private ContactAddressDTO address;

    private List<MunicipalityDTO> municipalities;
    public LocationDTO() {

    }
    public LocationDTO(Long id, String name, ContactAddressDTO address,List<MunicipalityDTO> municipalities) {
        this.id = id;
        this.name = name;
        this.municipalities = municipalities;
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public ContactAddressDTO getAddress() {
        return address;
    }

    public void setAddress(ContactAddressDTO address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<MunicipalityDTO> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<MunicipalityDTO> municipalities) {
        this.municipalities = municipalities;
    }
}

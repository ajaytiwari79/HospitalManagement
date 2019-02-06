package com.kairos.dto.user.organization.union;

import com.kairos.dto.user.staff.client.ContactAddressDTO;
import com.kairos.enums.UnionState;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UnionDTO {
    @NotBlank
    @NotNull
    private String name;
    private ContactAddressDTO mainAddress;
    private List<Long> sectorIds = new ArrayList<>();
    private List<Long> locationIds = new ArrayList<>();
    private Long id;
    private UnionState state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContactAddressDTO getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(ContactAddressDTO mainAddress) {
        this.mainAddress = mainAddress;
    }

    public List<Long> getSectorIds() {
        return sectorIds;
    }

    public void setSectorIds(List<Long> sectorIds) {
        this.sectorIds = sectorIds;
    }

    public List<Long> getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(List<Long> locationIds) {
        this.locationIds = locationIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnionState getState() {
        return state;
    }

    public void setState(UnionState state) {
        this.state = state;
    }

}

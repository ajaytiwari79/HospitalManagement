package com.kairos.dto.user.organization.union;

import com.kairos.dto.user.organization.MunicipalityDTO;
import com.kairos.dto.user.staff.client.ContactAddressDTO;
import com.kairos.enums.UnionState;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class UnionDataDTO {

    private Long id;
    private String name;
    private List<SectorDTO> sectors = new ArrayList<>();
    private ContactAddressDTO mainAddress;
    private List<LocationDTO> locations = new ArrayList<>();
    private List<MunicipalityDTO> municipalities = new ArrayList<>();
    private UnionState state;

}

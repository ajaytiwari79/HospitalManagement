package com.kairos.dto.user.organization.union;

import com.kairos.dto.user.staff.client.ContactAddressDTO;
import com.kairos.enums.UnionState;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UnionDTO {
    @NotBlank
    @NotNull
    private String name;
    @Valid
    private ContactAddressDTO mainAddress;
    @Valid
    private List<SectorDTO> sectors=new ArrayList<>();
    private List<Long> locationIds = new ArrayList<>();
    private Long id;
    private UnionState state;
}

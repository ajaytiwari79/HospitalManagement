package com.kairos.dto.user.organization.union;

import com.kairos.dto.user.organization.MunicipalityDTO;
import com.kairos.dto.user.staff.client.ContactAddressDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {

    private Long id;
    @NotEmpty(message="message.location.name.blank")
    private String name;

    private ContactAddressDTO address;

    private List<MunicipalityDTO> municipalities;



}

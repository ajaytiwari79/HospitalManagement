package com.kairos.dto.user.staff.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientStaffInfoDTO {
    private Long clientId;
    private Long staffId;

    public ClientStaffInfoDTO(Long staffId) {
        this.staffId = staffId;
    }
}

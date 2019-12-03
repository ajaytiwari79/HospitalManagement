package com.kairos.dto.user.staff;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
@Getter
@Setter
public class ContactPersonDTO {

    @NotNull(message = "Serviceid can't be null")
    private Long serviceTypeId;
    private Long  primaryStaffId;
    private Long secondaryStaffId1;
    private Long  secondaryStaffId2;
    private Long secondaryStaffId3;
    private List<Long> houseHoldMembers;
}
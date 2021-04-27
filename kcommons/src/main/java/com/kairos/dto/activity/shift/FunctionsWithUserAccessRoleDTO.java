package com.kairos.dto.activity.shift;

import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionsWithUserAccessRoleDTO {

    private Map<LocalDate, List<FunctionDTO>> functions;
    private UserAccessRoleDTO userAccessRoleDTO;
}

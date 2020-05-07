package com.kairos.dto.activity.open_shift;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriorityGroupWrapper {
    private PriorityGroupDefaultData defaultData;
    private List<PriorityGroupDTO> priorityGroupData;
}

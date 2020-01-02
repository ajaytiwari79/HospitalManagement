package com.kairos.persistence.model.shortcuts;

import com.kairos.dto.activity.ShortCuts.GraphFilterDTO;
import com.kairos.dto.activity.counter.TabKPIDTO;
import com.kairos.dto.activity.todo.TodoFilter;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Shortcut extends MongoBaseEntity {
    private Long staffId;
    private Long unitId;
    private String name;
    private String selectedRole;
    private boolean showAllStaffs;
    private StaffFilterDTO staffFilter;
    private List<TabKPIDTO> tabKPIs;
    private TodoFilter todoFilter;
    private GraphFilterDTO graphFilter;
    private List<ShortcutsDetails> shortcutsDetails;


}
package com.kairos.persistence.model.shortcuts;

import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.counter.TabKPI;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Shortcuts extends MongoBaseEntity {
    private Long staffId;
    private Long unitId;
    private String name;
    private StaffFilterDTO staffFilter;
    private TabKPI tabKPIs;
    private List<ShortcutsDetails> shortcutsDetails;


}
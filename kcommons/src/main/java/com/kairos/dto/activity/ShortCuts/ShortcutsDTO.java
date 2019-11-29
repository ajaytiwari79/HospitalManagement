package com.kairos.dto.activity.ShortCuts;

import com.kairos.dto.activity.counter.TabKPIDTO;
import com.kairos.dto.activity.todo.TodoFilter;
import com.kairos.dto.user.staff.StaffFilterDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShortcutsDTO {
    private BigInteger id;
    private String name;
    private Long staffId;
    private Long unitId;
    private StaffFilterDTO staffFilter;
    private List<TabKPIDTO> tabKPIs;
    private TodoFilter todoFilter;
    private GraphFilterDTO graphFilter;
    private List<ShortcutsDetailsDTO> shortcutsDetails;

    public void setName(String name) {
        this.name = name.trim();
    }
}

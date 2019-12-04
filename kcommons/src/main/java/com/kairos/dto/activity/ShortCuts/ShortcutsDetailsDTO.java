package com.kairos.dto.activity.ShortCuts;

import com.kairos.enums.ViewTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShortcutsDetailsDTO {
    private ViewTypeEnum viewTypeEnum;
    private String tabId;
    private String value;
}

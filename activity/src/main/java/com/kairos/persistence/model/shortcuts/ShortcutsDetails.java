package com.kairos.persistence.model.shortcuts;

import com.kairos.enums.ViewTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShortcutsDetails {
    private ViewTypeEnum viewTypeEnum;
    private String tabId;
    private String value;
}

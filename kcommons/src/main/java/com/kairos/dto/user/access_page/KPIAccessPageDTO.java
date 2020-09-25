package com.kairos.dto.user.access_page;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class KPIAccessPageDTO {
    private String name;
    private String moduleId;
    private boolean read;
    private boolean write;
    private boolean active;
    private List<KPIAccessPageDTO> child;
    private boolean enable;
    private boolean defaultTab;
    private Map<String, TranslationInfo> translations;
}

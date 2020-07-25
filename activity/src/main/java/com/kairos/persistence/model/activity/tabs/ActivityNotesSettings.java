package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by vipul on 24/8/17.
 */
@Getter
@Setter
public class ActivityNotesSettings {
    @KPermissionField
    private String content;
    private String originalDocumentName;
    private String modifiedDocumentName;
}

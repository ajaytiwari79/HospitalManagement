package com.kairos.persistence.model.activity.tabs;

import com.kairos.annotations.KPermissionField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by vipul on 24/8/17.
 */
@Getter
@Setter
public class ActivityNotesSettings implements Serializable {
    private static final long serialVersionUID = -4487390269321893236L;
    @KPermissionField
    private String content;
    private String originalDocumentName;
    private String modifiedDocumentName;
}

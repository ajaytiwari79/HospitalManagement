package com.kairos.dto.user.access_permission;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by prabjot on 23/5/17.
 */
@Getter
@Setter
public class AccessPermissionDTO {

    private boolean isRead;
    private boolean isWrite;
    private long pageId;
    private Long unitId;
    private Long staffId;
}

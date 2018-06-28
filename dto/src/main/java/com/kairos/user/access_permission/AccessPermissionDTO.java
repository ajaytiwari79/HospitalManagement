package com.kairos.user.access_permission;

/**
 * Created by prabjot on 23/5/17.
 */
public class AccessPermissionDTO {

    private boolean isRead;
    private boolean isWrite;
    private long pageId;
    private Long unitId;
    private Long staffId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public long getPageId() {

        return pageId;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isWrite() {
        return isWrite;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setWrite(boolean write) {
        isWrite = write;
    }
}

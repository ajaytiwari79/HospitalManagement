package com.kairos.persistence.model.user.auth;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 28/9/17.
 */
@QueryResult
public class TabPermission {

    private Long id;
    private String tabId;
    private boolean isWrite;
    private boolean isRead;
    private Long unitId;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public boolean isWrite() {
        return isWrite;
    }

    public void setWrite(boolean write) {
        isWrite = write;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TabPermission that = (TabPermission) o;

        return new EqualsBuilder()
                .append(isWrite, that.isWrite)
                .append(isRead, that.isRead)
                .append(tabId, that.tabId)
                .append(unitId, that.unitId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tabId)
                .append(isWrite)
                .append(isRead)
                .append(unitId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "TabPermission{" +
                "tabId='" + tabId + '\'' +
                ", isWrite=" + isWrite +
                ", isRead=" + isRead +
                ", unitId=" + unitId +
                '}';
    }
}


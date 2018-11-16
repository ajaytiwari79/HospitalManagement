package com.kairos.dto.user.expertise;

/**
 * @author pradeep
 * @date - 16/11/18
 */

public class CareDaysDTO {
    private Long id;
    private Integer from;
    private Integer to;
    private Integer leavesAllowed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getLeavesAllowed() {
        return leavesAllowed;
    }

    public void setLeavesAllowed(Integer leavesAllowed) {
        this.leavesAllowed = leavesAllowed;
    }
}

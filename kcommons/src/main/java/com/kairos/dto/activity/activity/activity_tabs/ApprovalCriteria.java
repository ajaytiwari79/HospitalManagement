package com.kairos.dto.activity.activity.activity_tabs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 *Created By Pavan on 5/9/18
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalCriteria {
    private Float approvalPercentage;
    private Short approvalTime; // in Days
    private transient String color;
    private transient String colorName;

    public ApprovalCriteria(String color, String colorName) {
        this.color = color;
        this.colorName = colorName;
    }
}

package com.kairos.wrapper.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
final public class ActivityTimeTypeWrapper {

    private BigInteger id;
    private String name;
    private List<TimeTypeHierarchy> timeTypeHierarchyList;


    @Getter
    @Setter
    @NoArgsConstructor
    public class TimeTypeHierarchy {
        private BigInteger id;
        private String timeTypes;
        private String label;
    }

}

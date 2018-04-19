package com.planner.domain.config;


import com.planner.domain.common.BaseEntity;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class Level extends BaseEntity{

    private int hard;
    private int medium;
    private int soft;


}

package com.planning.domain.config;


import com.planning.domain.common.BaseEntity;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class Level extends BaseEntity{

    private int hard;
    private int medium;
    private int soft;


}

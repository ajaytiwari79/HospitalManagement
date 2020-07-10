package com.kairos.persistence.model.activity;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class StaffActivityMostlyUse extends MongoBaseEntity {
    private Long staffId;
    private BigInteger activityId;
    private int useActivityCount;
}

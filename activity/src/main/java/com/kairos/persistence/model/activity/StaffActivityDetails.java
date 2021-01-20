package com.kairos.persistence.model.activity;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class StaffActivityDetails extends MongoBaseEntity implements Serializable {
    private static final long serialVersionUID = -7283513552363864822L;
    private Long staffId;
    private BigInteger activityId;
    private int useActivityCount;
}

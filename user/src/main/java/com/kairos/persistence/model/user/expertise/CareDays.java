package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class CareDays extends UserBaseEntity implements Comparable<CareDays> {
    private Integer from;
    private Integer to;
    private Integer leavesAllowed;

    @Override
    public int compareTo(CareDays o) {
        return o.from-this.from;
    }
}

package com.kairos.persistence.model.user.profile;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 14/9/16.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends UserBaseEntity {
    private String name;

}

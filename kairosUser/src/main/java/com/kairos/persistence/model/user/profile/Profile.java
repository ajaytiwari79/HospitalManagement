package com.kairos.persistence.model.user.profile;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 14/9/16.
 */
@NodeEntity
public class Profile extends UserBaseEntity {


    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Profile(String name) {
        this.name = name;
    }

    public Profile() {
    }
}

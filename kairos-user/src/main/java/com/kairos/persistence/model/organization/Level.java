package com.kairos.persistence.model.organization;



import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by prabjot on 21/8/17.
 */
@NodeEntity
public class Level extends UserBaseEntity {

    private String name;
    private String description;
    private boolean isEnabled = true;

    public Level() {
        //default constructor
    }

    public Level(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
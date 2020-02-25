package com.kairos.persistence.model.task_type;

import com.kairos.persistence.model.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by oodles on 23/11/16.
 */
@Document
public class MapPointer extends MongoBaseEntity {

    private String name;
    private String iconURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}

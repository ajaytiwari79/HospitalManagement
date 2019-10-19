package com.kairos.persistence.model.user.language;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * Created by prabjot on 19/10/16.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class LanguageLevel extends UserBaseEntity {

    @NotBlank(message = "error.LanguageLevel.name.notEmpty")
    private String name;
    private String description;
    @Relationship(type =  BELONGS_TO)
    private Country country;

    public LanguageLevel(String name) {
        this.name = StringUtils.trim(name);
    }

    private boolean isEnabled = true;


    public Map<String,Object> retrieveDetails(){
        Map<String,Object> data = new HashMap<>();
        data.put("id",this.id);
        data.put("name",this.name);
        data.put("description",this.description);
        data.put("lastModificationDate",this.getLastModificationDate());
        data.put("creationDate",this.getCreationDate());
        return data;


    }
}

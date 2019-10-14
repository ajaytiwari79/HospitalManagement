package com.kairos.persistence.model.user.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.PROVINCE;


/**
 * Created by oodles on 22/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Municipality extends UserBaseEntity {
    @NotBlank(message = ERROR_MUNICIPALITY_NAME_NOTEMPTY)
    private String name;

    @NotBlank(message = ERROR_MUNICIPALITY_GEOFENCE_NOTEMPTY)
    private String geoFence;

    @NotBlank(message = ERROR_MUNICIPALITY_CODE_NOTEMPTY)
    private String code;

    private float latitude;
    private float longitude;

    @Relationship(type = PROVINCE)
    private Province province;
    private boolean isEnable = true;

    public Municipality(Long id,String name) {
        this.name = name;
        this.id=id;
    }


    public Map<String, Object> retrieveDetails() {
        Map<String, Object> response = new HashMap();
        response.put("id", this.id);
        response.put("name", this.name);
        response.put("code", this.code);
        response.put("geoFence", this.geoFence);
        return response;
    }

    public Municipality retrieveBasicDetails() {
        return new Municipality(this.id, this.name);
    }
}

package com.kairos.persistence.model.user.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * Created by oodles on 12/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Region extends UserBaseEntity {

    private static final long serialVersionUID = 202781127800317559L;
    @NotBlank(message = ERROR_REGION_NAME_NOTEMPTY)
    private String name;
    @NotBlank(message = ERROR_REGION_CODE_NOTEMPTY)
    private String code;
    @NotBlank(message = ERROR_REGION_GEOFENCE_NOTEMPTY)
    private String geoFence;

    private float latitude;

    private float longitude;

    @Relationship(type = BELONGS_TO)
    private Country country;

    private boolean isEnable = true;

    public Region(String name, String code, float latitude, float longitude) {
        this.name = name;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Map<String,Object> retrieveDetails() {
        Map<String,Object> response = new HashMap();
        response.put("id",this.id);
        response.put("name",this.name);
        response.put("code",this.code);
        response.put("geoFence",this.geoFence);
        return  response;

    }
}

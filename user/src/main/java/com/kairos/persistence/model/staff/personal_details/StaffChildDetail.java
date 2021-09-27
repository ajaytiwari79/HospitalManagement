package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.annotations.KPermissionField;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created By G.P.Ranjan on 5/11/19
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class StaffChildDetail extends UserBaseEntity {
    private static final long serialVersionUID = 349713961690696674L;
    @KPermissionField
    private String name;
    @KPermissionField
    private String cprNumber;
    @KPermissionField
    private boolean childCustodyRights;

}

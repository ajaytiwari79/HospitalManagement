package com.kairos.persistence.model.user.office_esources_and_metadata;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;


/**
 * Created by @pankaj on 9/2/17.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfficeResources extends UserBaseEntity {

    private String name;
    private String resourceType;
}

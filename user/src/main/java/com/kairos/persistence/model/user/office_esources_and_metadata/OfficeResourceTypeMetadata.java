package com.kairos.persistence.model.user.office_esources_and_metadata;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by @pankaj on 9/2/17.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfficeResourceTypeMetadata extends UserBaseEntity {
    private List<String> officeResource;
    private List<String> vehicleResource;

}

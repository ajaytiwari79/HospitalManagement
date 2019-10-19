package com.kairos.persistence.model.user.resources;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;

/**
 * Created by oodles on 13/12/17.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class VehicleLocation  extends UserBaseEntity {

    @NotNull(message = ERROR_NAME_NOTNULL)
    private String name;
    private String description;
    private boolean enabled = true;

    public VehicleLocation(@NotNull(message = ERROR_NAME_NOTNULL) String name, String description) {
        this.name = name;
        this.description = description;
    }

}

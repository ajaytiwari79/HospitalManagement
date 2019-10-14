package com.kairos.persistence.model.user.resources;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;

@QueryResult
@Getter
@Setter
public class VehicleLocationDTO {

    private Long id;
    @NotNull(message = ERROR_NAME_NOTNULL)
    private String name;
    private String description;
}

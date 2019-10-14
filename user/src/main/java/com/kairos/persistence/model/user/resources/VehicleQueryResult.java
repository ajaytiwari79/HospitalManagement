package com.kairos.persistence.model.user.resources;

import com.kairos.dto.user.country.feature.FeatureDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prerna on 6/12/17.
 */
@QueryResult
@Getter
@Setter
public class VehicleQueryResult {

    private Long id;
    private String name;
    private String description;
    private String icon;
    private boolean enabled = true;
    private List<FeatureDTO> features;
}

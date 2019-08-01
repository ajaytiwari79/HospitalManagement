package com.kairos.persistence.model.kpermissions;

import lombok.*;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@QueryResult
public class KPermissionSubModelFieldQueryResult {
    private String modelName;
    private List<String> modelFields = new ArrayList<>();
}

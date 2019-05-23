package com.kairos.persistence.model.kpermissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@QueryResult
public class KPermissionSubModelFieldQueryResult {
    String modelName;
    List<String> modelFields = new ArrayList<>();
}

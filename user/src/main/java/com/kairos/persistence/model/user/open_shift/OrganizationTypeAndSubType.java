package com.kairos.persistence.model.user.open_shift;

import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.organization.OrganizationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

@QueryResult
@Getter
@Setter
public class OrganizationTypeAndSubType {
    private Long id;
    private String name;
    private List<OrganizationType> children;
    private Long levelId;
    private Map<String, TranslationInfo> translations;
}

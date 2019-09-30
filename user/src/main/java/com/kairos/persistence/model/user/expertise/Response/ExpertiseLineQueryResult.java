package com.kairos.persistence.model.user.expertise.Response;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Sector;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@QueryResult
@Getter
@Setter
public class ExpertiseLineQueryResult {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Sector sector;
    private Level organizationLevel;
    private List<OrganizationService> organizationServices;
    private Organization union;
    private Map<String, Object> seniorityLevel;
}

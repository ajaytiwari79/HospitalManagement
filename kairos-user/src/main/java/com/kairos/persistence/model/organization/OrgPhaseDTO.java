package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.user.phase.PhaseDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 20/9/17.
 */
@QueryResult
public class OrgPhaseDTO {
    private String name;
    private String email;
    private Long id;
    private List<PhaseDTO> phases;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PhaseDTO> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseDTO> phases) {
        this.phases = phases;
    }
}

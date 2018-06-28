package com.kairos.activity.client.dto.organization;


import com.kairos.activity.phase.PhaseDTO;

import java.util.List;

/**
 * Created by vipul on 20/9/17.
 */
public class OrganizationPhaseDTO {
    private String name;
    private String email;
    private Long id;
    private List<PhaseDTO> phases;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PhaseDTO> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseDTO> phases) {
        this.phases = phases;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganizationPhaseDTO() {

    }

    public OrganizationPhaseDTO(String name, String email, Long id, List<PhaseDTO> phases) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.phases = phases;
    }

    @Override
    public String toString(){
        return name+" "+email+" ";
    }
}

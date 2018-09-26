package com.kairos.dto.user.country.agreement.cta.cta_response;

public class PhaseResponseDTO {
    private Long id;
    private String name;
    private int sequence;

    public PhaseResponseDTO() {
        //default constructor
    }

    public PhaseResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}

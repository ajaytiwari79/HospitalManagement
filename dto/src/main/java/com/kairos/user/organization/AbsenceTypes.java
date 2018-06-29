package com.kairos.user.organization;

/**
 * Created by oodles on 16/12/16.
 */
public class AbsenceTypes {

    private Long id;

    private Long ATVTID; // Absence Type Id as in Visitour master data
    private String name;
    private long organizationId;

    public AbsenceTypes() {
    }

    public AbsenceTypes(Long ATVTID, String name, Long organizationId) {
        this.ATVTID = ATVTID;
        this.name = name;
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getATVTID() {
        return ATVTID;
    }

    public void setATVTID(Long ATVTID) {
        this.ATVTID = ATVTID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

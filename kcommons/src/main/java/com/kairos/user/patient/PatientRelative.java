package com.kairos.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientRelative {
    private String id;

    private String patientId;

    private RelatedPatient relatedPatient;

    private RelativeContactDetails contact;

    private String importance;

    private String displayName;

    private String type;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getPatientId ()
    {
        return patientId;
    }

    public void setPatientId (String patientId)
    {
        this.patientId = patientId;
    }

    public RelatedPatient getRelatedPatient ()
    {
        return relatedPatient;
    }

    public void setRelatedPatient (RelatedPatient relatedPatient)
    {
        this.relatedPatient = relatedPatient;
    }

    public String getImportance ()
    {
        return importance;
    }

    public void setImportance (String importance)
    {
        this.importance = importance;
    }

    public String getDisplayName ()
    {
        return displayName;
    }

    public void setDisplayName (String displayName)
    {
        this.displayName = displayName;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    public RelativeContactDetails getContact() {
        return contact;
    }

    public void setContact(RelativeContactDetails contact) {
        this.contact = contact;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", patientId = "+patientId+", relatedPatient = "+relatedPatient+", importance = "+importance+", displayName = "+displayName+", type = "+type+", version = "+version+"]";
    }
}
package com.kairos.dto.user.patient;

public class PatientIdentifier {
    private String managedExternally;

    //  private String _links;

    private String type;

    private String identifier;

    public String getManagedExternally() {
        return managedExternally;
    }

    public void setManagedExternally(String managedExternally) {
        this.managedExternally = managedExternally;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "ClassPojo [managedExternally = " + managedExternally + ", type = " + type + ", identifier = " + identifier + "]";
    }
}
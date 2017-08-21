package com.kairos.persistence.model.user.client;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by oodles on 28/9/16.
 */
public class ClientAllergies extends UserBaseEntity {
    private String allergyType;
    private String allergyName;
    private boolean isAllergyValidated;
    private String[] avoidance;

    public ClientAllergies(String allergyType, String allergyName, boolean isAllergyValidated, String[] avoidance) {
        this.allergyType = allergyType;
        this.allergyName = allergyName;
        this.isAllergyValidated = isAllergyValidated;
        this.avoidance = avoidance;
    }

    public ClientAllergies() {
    }

    public String getAllergyType() {
        return allergyType;
    }

    public void setAllergyType(String allergyType) {
        this.allergyType = allergyType;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }

    public boolean isAllergyValidated() {
        return isAllergyValidated;
    }

    public void setAllergyValidated(boolean allergyValidated) {
        isAllergyValidated = allergyValidated;
    }

    public String[] getAvoidance() {
        return avoidance;
    }

    public void setAvoidance(String[] avoidance) {
        this.avoidance = avoidance;
    }
}


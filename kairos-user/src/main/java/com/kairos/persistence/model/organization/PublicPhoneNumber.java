package com.kairos.persistence.model.organization;

import org.neo4j.ogm.annotation.NodeEntity;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by pankaj on 23/12/16.
 */
@NodeEntity
public class PublicPhoneNumber extends UserBaseEntity {

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }



    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PublicPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

     public PublicPhoneNumber() {
        
    }
}

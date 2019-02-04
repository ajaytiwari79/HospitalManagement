package com.kairos.persistence.model.client;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.GraphId;

/**
 * Created by oodles on 28/9/16.
 */
public class ClientDoctor extends UserBaseEntity {
    private boolean isPrimary;
    private String type;
    private String name;
    private String nameOfClinic;
    private String typeOfClinic;
    private ContactAddress clinicAddress;
    private ContactDetail contactDetail;


    public ClientDoctor(boolean isPrimary, String type, String name, String nameOfClinic, String typeOfClinic, ContactAddress clinicAddress, ContactDetail contactDetail) {
        this.isPrimary = isPrimary;
        this.type = type;
        this.name = name;
        this.nameOfClinic = nameOfClinic;
        this.typeOfClinic = typeOfClinic;
        this.clinicAddress = clinicAddress;
        this.contactDetail = contactDetail;
    }

    public ClientDoctor() {
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameOfClinic() {
        return nameOfClinic;
    }

    public void setNameOfClinic(String nameOfClinic) {
        this.nameOfClinic = nameOfClinic;
    }

    public String getTypeOfClinic() {
        return typeOfClinic;
    }

    public void setTypeOfClinic(String typeOfClinic) {
        this.typeOfClinic = typeOfClinic;
    }

    public ContactAddress getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(ContactAddress clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }
}

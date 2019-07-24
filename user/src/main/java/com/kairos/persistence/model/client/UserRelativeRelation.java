package com.kairos.persistence.model.client;

import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.*;

/**
 * Created by oodles on 3/10/16.
 */
@RelationshipEntity(type = "USER_HAS_RELATIVE")
public class UserRelativeRelation extends UserBaseEntity {

    @StartNode
    User user;

    @EndNode
    User relative;

    private boolean isNextToKim;
    private String priority;
    private ContactAddress address;
    private ContactDetail contactDetail;
    private String distanceToRelative;
    private boolean canUpdateOnPublicPortal;
    private boolean isFullGuardian;
    private String remarks;


    public UserRelativeRelation(User user, User relative, String priority, ContactAddress address, ContactDetail contactDetail, String distanceToRelative, boolean canUpdateOnPublicPortal, boolean isFullGuardian, String remarks) {
        this.user = user;
        this.relative = relative;
        this.priority = priority;
        this.address = address;
        this.contactDetail = contactDetail;
        this.distanceToRelative = distanceToRelative;
        this.canUpdateOnPublicPortal = canUpdateOnPublicPortal;
        this.isFullGuardian = isFullGuardian;
        this.remarks = remarks;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getRelative() {
        return relative;
    }

    public void setRelative(User relative) {
        this.relative = relative;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public ContactAddress getAddress() {
        return address;
    }

    public void setAddress(ContactAddress address) {
        this.address = address;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public String getDistanceToRelative() {
        return distanceToRelative;
    }

    public void setDistanceToRelative(String distanceToRelative) {
        this.distanceToRelative = distanceToRelative;
    }

    public boolean isCanUpdateOnPublicPortal() {
        return canUpdateOnPublicPortal;
    }

    public void setCanUpdateOnPublicPortal(boolean canUpdateOnPublicPortal) {
        this.canUpdateOnPublicPortal = canUpdateOnPublicPortal;
    }

    public boolean isNextToKim() {
        return isNextToKim;
    }

    public void setNextToKim(boolean nextToKim) {
        isNextToKim = nextToKim;
    }

    public boolean isFullGuardian() {
        return isFullGuardian;
    }

    public void setFullGuardian(boolean fullGuardian) {
        isFullGuardian = fullGuardian;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.annotations.KPermissionField;
import com.kairos.annotations.KPermissionModel;
import com.kairos.annotations.KPermissionSubModel;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.staff.StaffFavouriteFilter;
import com.kairos.persistence.model.user.language.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 24/10/16.
 */
@KPermissionModel
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Staff extends UserBaseEntity {
    @KPermissionSubModel
    @Relationship(type = HAS_CONTACT_DETAIL)
    private ContactDetail contactDetail;
    //address tab
    @KPermissionSubModel
    @Relationship(type = HAS_CONTACT_ADDRESS)
    private ContactAddress contactAddress;

    @KPermissionSubModel
    @Relationship(type = SECONDARY_CONTACT_ADDRESS)
    private ContactAddress secondaryContactAddress;

    @Relationship(type = BELONGS_TO)
    private User user;

    private EngineerType engineerType;

    @Relationship(type = HAS_FAVOURITE_FILTERS)
    private List<StaffFavouriteFilter> staffFavouriteFilterList;

    private String generalNote;
    private String reqFromPerson;
    @KPermissionField
    private String cardNumber;
    private boolean copyKariosMailToLogin;
    @KPermissionField
    private String sendNotificationBy;
    private String profilePic;
    @KPermissionField
    private String email;
    @KPermissionField
    private String badgeNumber;
    @KPermissionField
    private String userName;

    //time care external id`
    @KPermissionField
    private Long externalId;

    //monaco id
    private String manacoId;

    //personal info
    @KPermissionField
    private String firstName;
    @KPermissionField
    private String lastName;
    @KPermissionField
    private String familyName;
    @KPermissionField
    private String signature;
    @KPermissionField
    private String password;
    private String nationalInsuranceNumber;
    @KPermissionField
    private StaffStatusEnum currentStatus;
    @KPermissionField
    private Long inactiveFrom;
    private long organizationId;
    @KPermissionField
    private Language language;
    // Visitour Speed Profile
    private Integer speedPercent;
    private Integer workPercent;
    private Integer overtime;
    private Float costDay;
    private Float costCall;
    private Float costKm;
    private Float costHour;
    private Float costHourOvertime;
    private Integer capacity;
    private Long kmdExternalId;
    private String careOfName;

    private String access_token; // specially required for chat server only
    private String user_id; //specially required for chat server only

    @Relationship(type = BELONGS_TO_TAGS)
    private List<Tag> tags;

    public Staff(String email, String userName, String firstName, String lastName, String familyName, StaffStatusEnum currentStatus, Long inactiveFrom, String cprNumber) {
        this.email = email;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.familyName = familyName;
        this.currentStatus = currentStatus;
        this.inactiveFrom = inactiveFrom;
    }
    public Staff(String firstName) {
        this.firstName = firstName;
    }

    public void saveNotes(String generalNote, String requestFromPerson) {
        this.generalNote = generalNote;
        this.reqFromPerson = requestFromPerson;
    }

    public Map<String, Object> retrieveNotes() {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("generalNote", this.generalNote);
        map.put("reqFromPerson", this.reqFromPerson);
        return map;
    }

    public void addFavouriteFilters(StaffFavouriteFilter staffFavouriteFilter) {
        List<StaffFavouriteFilter> staffFavouriteFilterList = Optional.ofNullable(this.staffFavouriteFilterList).orElse(new ArrayList<>());
        staffFavouriteFilterList.add(staffFavouriteFilter);
        this.staffFavouriteFilterList = staffFavouriteFilterList;
    }
}

package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.staff.StaffFavouriteFilter;
import com.kairos.persistence.model.user.language.Language;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 24/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class Staff extends UserBaseEntity {

    private String generalNote;
    private String reqFromPerson;

    private String cardNumber;
    private boolean copyKariosMailToLogin;
    private String sendNotificationBy;
    private String profilePic;
    private String email;
    private String badgeNumber;
    private String userName;

    //time care external id`
    private Long externalId;

    //monaco id
    private String manacoId;

    //personal info
    private String firstName;
    private String lastName;
    private String familyName;
    private String signature;
    private String password;

    private String nationalInsuranceNumber;
    private StaffStatusEnum currentStatus;
    private Long inactiveFrom;
    long organizationId;
    private long visitourId;

    private String visitourTeamId;
    private Language language;


    @Relationship(type = HAS_CONTACT_DETAIL)
    private ContactDetail contactDetail;
    //address tab
    @Relationship(type = HAS_CONTACT_ADDRESS)
    private ContactAddress contactAddress;

    @Relationship(type = SECONDARY_CONTACT_ADDRESS)
    private ContactAddress secondaryContactAddress;

    @Relationship(type = BELONGS_TO)
    User user;

    EngineerType engineerType;


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

    @Relationship(type = HAS_FAVOURITE_FILTERS)
    private List<StaffFavouriteFilter> staffFavouriteFilterList;
    private String careOfName;

    private String access_token;
    private String user_id;




    public Staff() {
    }

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
    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(ContactAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Long getInactiveFrom() {
        return inactiveFrom;
    }

    public void setInactiveFrom(Long inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public void saveNotes(String generalNote, String requestFromPerson) {
        this.generalNote = generalNote;
        this.reqFromPerson = requestFromPerson;
    }
    public String getNationalInsuranceNumber() {
        return nationalInsuranceNumber;
    }

    public void setNationalInsuranceNumber(String nationalInsuranceNumber) {
        this.nationalInsuranceNumber = nationalInsuranceNumber;
    }


    public Map<String, Object> retrieveNotes() {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("generalNote", this.generalNote);
        map.put("reqFromPerson", this.reqFromPerson);
        return map;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(long visitourId) {
        this.visitourId = visitourId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public boolean isCopyKariosMailToLogin() {
        return copyKariosMailToLogin;
    }

    public void setCopyKariosMailToLogin(boolean copyKariosMailToLogin) {
        this.copyKariosMailToLogin = copyKariosMailToLogin;
    }

    public String getSendNotificationBy() {
        return sendNotificationBy;
    }

    public void setSendNotificationBy(String sendNotificationBy) {
        this.sendNotificationBy = sendNotificationBy;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGeneralNote() {
        return generalNote;
    }

    public void setGeneralNote(String generalNote) {
        this.generalNote = generalNote;
    }

    public String getReqFromPerson() {
        return reqFromPerson;
    }

    public void setReqFromPerson(String reqFromPerson) {
        this.reqFromPerson = reqFromPerson;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public EngineerType getEngineerType() {
        return engineerType;
    }

    public void setEngineerType(EngineerType engineerType) {
        this.engineerType = engineerType;
    }

    public String getVisitourTeamId() {
        return visitourTeamId;
    }

    public void setVisitourTeamId(String visitourTeamId) {
        this.visitourTeamId = visitourTeamId;
    }

    public String getManacoId() {
        return manacoId;
    }

    public void setManacoId(String manacoId) {
        this.manacoId = manacoId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSpeedPercent() {
        return speedPercent;
    }

    public void setSpeedPercent(Integer speedPercent) {
        this.speedPercent = speedPercent;
    }

    public Integer getWorkPercent() {
        return workPercent;
    }

    public void setWorkPercent(Integer workPercent) {
        this.workPercent = workPercent;
    }

    public Integer getOvertime() {
        return overtime;
    }

    public void setOvertime(Integer overtime) {
        this.overtime = overtime;
    }

    public Float getCostDay() {
        return costDay;
    }

    public void setCostDay(Float costDay) {
        this.costDay = costDay;
    }

    public Float getCostCall() {
        return costCall;
    }

    public void setCostCall(Float costCall) {
        this.costCall = costCall;
    }

    public Float getCostKm() {
        return costKm;
    }

    public void setCostKm(Float costKm) {
        this.costKm = costKm;
    }

    public Float getCostHour() {
        return costHour;
    }

    public void setCostHour(Float costHour) {
        this.costHour = costHour;
    }

    public Float getCostHourOvertime() {
        return costHourOvertime;
    }

    public void setCostHourOvertime(Float costHourOvertime) {
        this.costHourOvertime = costHourOvertime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getKmdExternalId() {
        return kmdExternalId;
    }

    public void setKmdExternalId(Long kmdExternalId) {
        this.kmdExternalId = kmdExternalId;
    }

    public void addFavouriteFilters(StaffFavouriteFilter staffFavouriteFilter) {
        List<StaffFavouriteFilter> staffFavouriteFilterList = Optional.ofNullable(this.staffFavouriteFilterList).orElse(new ArrayList<>());
        staffFavouriteFilterList.add(staffFavouriteFilter);
        this.staffFavouriteFilterList = staffFavouriteFilterList;
    }

    public List<StaffFavouriteFilter> getStaffFavouriteFilterList() {
        return staffFavouriteFilterList;
    }

    public void setStaffFavouriteFilterList(List<StaffFavouriteFilter> staffFavouriteFilterList) {
        this.staffFavouriteFilterList = staffFavouriteFilterList;
    }

    public String getCareOfName() {
        return careOfName;
    }

    public void setCareOfName(String careOfName) {
        this.careOfName = careOfName;
    }

    public StaffStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(StaffStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    public ContactAddress getSecondaryContactAddress() {
        return secondaryContactAddress;
    }

    public void setSecondaryContactAddress(ContactAddress secondaryContactAddress) {
        this.secondaryContactAddress = secondaryContactAddress;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}

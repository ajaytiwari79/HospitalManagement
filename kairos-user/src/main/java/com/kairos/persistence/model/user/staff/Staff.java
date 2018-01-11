package com.kairos.persistence.model.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.client.Client;
import com.kairos.persistence.model.user.client.ContactAddress;
import com.kairos.persistence.model.user.client.ContactDetail;
import com.kairos.persistence.model.user.country.EngineerType;
import com.kairos.persistence.model.user.expertise.Expertise;
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

    String generalNote;
    String reqFromPerson;
    private long employedSince;
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
    private boolean isActive;
    private long inactiveFrom;
    long organizationId;
    private long visitourId;
    private String cprNumber;
    private String visitourTeamId;


    private Language language;

    @Relationship(type = HAS_EXPERTISE_IN)
    Expertise expertise;

    @Relationship(type = HAS_CONTACT_DETAIL)
    private ContactDetail contactDetail;
    //address tab
    @Relationship(type = HAS_CONTACT_ADDRESS)
    private ContactAddress contactAddress;

    @Relationship(type = BELONGS_TO)
    User user;
    @Relationship(type = STAFF_BELONGS_TO)
    Client client;

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
    private List<StaffFavouriteFilters> staffFavouriteFiltersList;



    public Staff(String firstName) {
        this.firstName = firstName;
    }

    public Staff() {
    }

    public Staff(long employedSince, String email, String userName, String firstName, String lastName, String familyName, boolean isActive, long inactiveFrom, String cprNumber) {
        this.employedSince = employedSince;
        this.email = email;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.familyName = familyName;
        this.isActive = isActive;
        this.inactiveFrom = inactiveFrom;
        this.cprNumber = cprNumber;
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

    public long getInactiveFrom() {
        return inactiveFrom;
    }

    public void setInactiveFrom(long inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public void saveNotes(String generalNote, String requestFromPerson) {
        this.generalNote = generalNote;
        this.reqFromPerson = requestFromPerson;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getEmployedSince() {
        return employedSince;
    }

    public void setEmployedSince(long employedSince) {
        this.employedSince = employedSince;
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

    public Expertise getExpertise() {
        return expertise;
    }

    public void setVisitourId(long visitourId) {
        this.visitourId = visitourId;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
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

    public Map<String, Object> retrieveExpertiseDetails() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("staffId", this.getId());
        map.put("staffName", this.getFirstName() + "   " + this.getLastName());
        map.put("expertiseId", getExpertise().getId());
        map.put("expertiseName", getExpertise().getName());
        return map;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public Map<String,Object> fetchContactAddressDetail(){

        ContactAddress contactAddress =this.contactAddress;
        Map<String,Object> map = null;
        if(contactAddress != null){
            map = new HashMap<>();
            map.put("houseNumber",contactAddress.getHouseNumber());
            map.put("floorNumber",contactAddress.getFloorNumber());
            map.put("street1",contactAddress.getStreet1());
            map.put("zipCodeId",contactAddress.getZipCode()!=null ? contactAddress.getZipCode().getId(): null);
            map.put("city",contactAddress.getCity()!=null? contactAddress.getCity(): "");
            map.put("municipalityId",(contactAddress.getMunicipality()==null)?null:contactAddress.getMunicipality().getId());
            map.put("regionName",contactAddress.getRegionName());
            map.put("country",contactAddress.getCountry());
            map.put("latitude",contactAddress.getLatitude());
            map.put("longitude",contactAddress.getLongitude());
            map.put("province",contactAddress.getProvince());
            map.put("streetUrl",contactAddress.getStreetUrl());
            map.put("addressProtected",contactAddress.isAddressProtected());
            map.put("verifiedByVisitour",contactAddress.isVerifiedByVisitour());
        }
        return map;
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

    public void addFavouriteFilters(StaffFavouriteFilters staffFavouriteFilters){
        List<StaffFavouriteFilters> staffFavouriteFiltersList = Optional.ofNullable(this.staffFavouriteFiltersList).orElse(new ArrayList<>());
        staffFavouriteFiltersList.add(staffFavouriteFilters);
        this.staffFavouriteFiltersList = staffFavouriteFiltersList;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "{Staff={" +
                "generalNote='" + generalNote + '\'' +
                ", reqFromPerson='" + reqFromPerson + '\'' +
                ", employedSince=" + employedSince +
                ", cardNumber='" + cardNumber + '\'' +
                ", copyKariosMailToLogin=" + copyKariosMailToLogin +
                ", sendNotificationBy='" + sendNotificationBy + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", email='" + email + '\'' +
                ", badgeNumber='" + badgeNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", externalId=" + externalId +
                ", manacoId='" + manacoId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", signature='" + signature + '\'' +
                ", nationalInsuranceNumber='" + nationalInsuranceNumber + '\'' +
                ", isDisabled=" + isActive +
                ", inactiveFrom=" + inactiveFrom +
                ", organizationId=" + organizationId +
                ", visitourId=" + visitourId +
                ", cprNumber='" + cprNumber + '\'' +
                ", visitourTeamId='" + visitourTeamId + '\'' +
                ", speedPercent=" + speedPercent +
                ", workPercent=" + workPercent +
                ", overtime=" + overtime +
                ", costDay=" + costDay +
                ", costCall=" + costCall +
                ", costKm=" + costKm +
                ", costHour=" + costHour +
                ", costHourOvertime=" + costHourOvertime +
                '}'+
                '}';
    }
}

package com.kairos.persistence.model.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.CitizenHealthStatus;
import com.kairos.enums.client.ClientEnum;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.default_data.CitizenStatus;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.region.LocalAreaTag;
import com.kairos.utils.CPRUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.enums.CitizenHealthStatus.ALIVE;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 27/9/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class Client extends UserBaseEntity {
    private static final long serialVersionUID = -3657706200600394102L;
    @Relationship(type = CIVILIAN_STATUS)
    private CitizenStatus civilianStatus;
    private String profilePic;
    private String nameAmongStaff;
    private ClientEnum clientType;
    // Equipment Details
    private String requiredEquipmentsList;

    private boolean doHaveFreeChoiceServices;

    //AddressDTO Information - Each Client can have multiple addresses
    @Relationship(type = HAS_OFFICE_ADDRESS)
    private ContactAddress officeAddress;
    @Relationship(type = HAS_HOME_ADDRESS)
    private ContactAddress homeAddress;
    @Relationship(type = HAS_SECONDARY_ADDRESS)
    private ContactAddress secondaryAddress;
    @Relationship(type = HAS_PARTNER_ADDRESS)
    private ContactAddress partnerAddress;

    @JsonIgnore
    @Relationship(type = HAS_TEMPORARY_ADDRESS)
    private List<ClientTemporaryAddress> temporaryAddress;

    @Relationship(type = HAS_LOCAL_AREA_TAG)
    private LocalAreaTag localAreaTag;

    private ClientEnum.CitizenShip citizenship;
    private String nationalityType;
    @Relationship(type = HAS_CONTACT_DETAIL)
    private ContactDetail contactDetail;

    private boolean wantToUserOwnVehicle;

    private boolean isCitizenGettingPension;
    private int priority;
    private boolean useOwnVehicle;
    private boolean peopleInHousehold;

    private boolean livesAlone;
    private boolean doRequireTranslationAssistance;
    private String[] translationLanguage;

    private String portPhoneNumber;
    private String electronicKeyNumber;

    private String comment;
    private String commentExtra;

    private int speakDanishLevel;
    private int writeDanishLevel;
    private int readDanishLevel;
    private String occupation;


    // Transportation Details
    private String driverLicenseNumber;
    private List<Language> languageUnderstands;
    private String placeOfBirth;


    private boolean wheelChair;
    private boolean liftBus;
    private boolean require2peopleForTransport;
    private boolean requireMinibusForTransport;
    private boolean requireOxygenUnderTransport;
    private boolean useWheelChair;

    private boolean isMemberOfDenmark;
    private boolean IsCitizenGetPension;
    private String citizenPensionType;
    private boolean isCitizenTerminal;
    private String CitizenTerminalDescription;
    private boolean citizenDead;

    // Health info
    private boolean doesHaveAllergies;
    private boolean doesHaveDiseases;
    private boolean doesHaveDiagnoses;


    //Availability info
    private Long availabilityDateFrom;
    private Long availabilityDateTo;


    private String visitourTeamId;

    private long deceasedDate;
    private long terminatedDate;

    // KP-4178 added for
    @Relationship(type = IS_A)
    private User user;

    private CitizenHealthStatus healthStatus = ALIVE;

    @Relationship(type = HAS_ALLERGY)
    private List<ClientAllergies> clientAllergiesList;
    @Relationship(type = HAS_DISEASE)
    private List<ClientDisease> clientDiseaseList;
    @Relationship(type = HAS_DIAGNOSE)
    private List<ClientDiagnose> clientDiagnoseList;
    @Relationship(type = HAS_DOCTOR)
    private List<ClientDoctor> clientDoctorList;
    // Un-Availability Calender

    private String secondaryEmail;

    //dead, terminal, sick, in hospital etc
    private String citizenState;

    private Boolean importFromKMD;

    private String kmdNexusExternalId;

    private int updatedInfo1And2Count;
    private int notDraggedAndDroppedDemands;
    private int onEscalationListMinPKm;
    private int unplannedMinPKm;
    private int longDrivingTimeInMin;
    private int noOfVisitationHours;
    private int noOfVisitationTasks;
    private int mostDrivenKm;

    @Relationship(type = HAS_RELATION_OF)
    private List<ClientRelationType> clientRelationTypes;

    public Client() {
        // Constructors
    }

    public List<ClientTemporaryAddress> getTemporaryAddress() {
        return Optional.ofNullable(temporaryAddress).orElse(new ArrayList<>());
    }

    public void addClientRelations(ClientRelationType clientRelationType) {
        this.clientRelationTypes = Optional.ofNullable(this.clientRelationTypes).orElse(new ArrayList<>());
        this.clientRelationTypes.add(clientRelationType);

    }

    public void setContactPerson(Staff contactPerson) {
        Staff contactPerson1 = contactPerson;
    }
    public String getFullName() {
        return this.getUser().getFirstName() + " " + this.getUser().getLastName();
    }

    // General Tab Constructor
    public Map<String, Object> retrieveClientGeneralDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("cprNumber", this.getUser().getCprNumber());
        map.put("firstName", this.getUser().getFirstName());
        map.put("lastName", this.getUser().getLastName());
        map.put("nameAmongStaff", this.nameAmongStaff);
        map.put("age", Period.between(CPRUtil.getDateOfBirthFromCPR(this.getUser().getCprNumber()), LocalDate.now()).getYears());
        map.put("gender", this.getUser().getGender());
        map.put("profilePic", this.profilePic);
        map.put("citizenDead", this.citizenDead);
        map.put("visitourTeamId", this.visitourTeamId);
        map.put("occupation", this.getOccupation());
        map.put("doHaveFreeChoiceServices", this.doHaveFreeChoiceServices);
        map.put("doRequireTranslationAssistance", this.doRequireTranslationAssistance);
        map.put("nationalityType", this.nationalityType);
        map.put("citizenship", this.citizenship);
        map.put("peopleInHousehold", this.peopleInHousehold);
        map.put("livesAlone", this.livesAlone);
        map.put("healthStatus", this.healthStatus);
        map.put("deathDate", getDate(this.deceasedDate));
        map.put("deceasedDate", getDate(this.deceasedDate));
        map.put("terminatedDate", getDate(this.terminatedDate));
        return map;
    }

    // Transportation Tab Constructor
    public Map<String, Object> retrieveTransportationDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.getId());
        map.put("driverLicenseNumber", this.driverLicenseNumber != null ?  this.driverLicenseNumber : "");
        map.put("useWheelChair", this.useWheelChair);
        map.put("liftBus", this.liftBus);
        map.put("requiredEquipmentsList", this.requiredEquipmentsList != null ? this.requiredEquipmentsList : "");
        map.put("doRequireTranslationAssistance", this.doRequireTranslationAssistance);
        map.put("require2peopleForTransport", this.require2peopleForTransport);
        map.put("requireOxygenUnderTransport", this.requireOxygenUnderTransport);
        map.put("wantToUserOwnVehicle", this.wantToUserOwnVehicle);
        map.put("useOwnVehicle", this.useOwnVehicle);
        return map;
    }

    // AddressDTO Tab Constructor
    public List<Object> retrieveLanguages() {
        List<Object> objectList = new ArrayList<>();
        if (languageUnderstands != null) {
            for (Language language : languageUnderstands) {
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("id", language.getId());
                objectList.add(objectMap);
            }
            return objectList;
        }
        return null;
    }

    public Map<String, Object> retrieveAddressDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("homeAddress", this.homeAddress);
        map.put("secondaryAddress", this.secondaryAddress);
        map.put("partnerAddress", this.partnerAddress);
        return map;
    }

    // Medical Tab Constructor
    public Map<String, Object> retrieveMedicalDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("clientDoctorList", this.clientDoctorList);
        map.put("clientDiagnoseList", this.clientDiagnoseList);
        map.put("isCitizenTerminal", this.isCitizenTerminal);
        map.put("isMemberOfDenmark", this.isMemberOfDenmark);
        map.put("isCitizenGettingPension", this.isCitizenGettingPension);
        map.put("citizenPensionType", this.citizenPensionType);
        return map;
    }

    public Map<String, Object> retrieveSocialMediaDetails() {
        Map<String, Object> map = new HashMap<>();

        map.put("facebookAccount", this.contactDetail != null ? this.contactDetail.getFacebookAccount() : "");
        map.put("linkedInAccount", this.contactDetail != null ? this.contactDetail.getLinkedInAccount() : "");
        map.put("twitterAccount", this.contactDetail != null ? this.contactDetail.getTwitterAccount() : "");
        map.put("messenger", this.contactDetail != null ? this.contactDetail.getMessenger() : "");
        map.put("mobilePhone", this.contactDetail != null ? this.contactDetail.getMobilePhone() : "");
        map.put("workPhone", this.contactDetail != null ? this.contactDetail.getWorkPhone() : "");
        map.put("privatePhone", this.contactDetail != null ? this.contactDetail.getPrivatePhone() : "");
        map.put("hideMobilePhone", this.contactDetail != null ? this.contactDetail.isHideMobilePhone() : "");
        map.put("hideWorkPhone", this.contactDetail != null ? this.contactDetail.isHideWorkPhone() : "");
        map.put("hidePrivatePhone", this.contactDetail != null ? this.contactDetail.isHidePrivatePhone() : "");
        map.put("workEmail", this.contactDetail != null ? this.contactDetail.getPrivateEmail() : "");
        map.put("emergencyPhone", this.contactDetail != null ? this.contactDetail.getEmergencyPhone() : "");
        map.put("hideEmergencyPhone", this.contactDetail != null ? this.contactDetail.isHideEmergencyPhone() : "");
        return map;
    }

    public ContactDetail saveContactDetail(NextToKinDTO nextToKinDTO, ContactDetail contactDetail) {
        if (Optional.ofNullable(nextToKinDTO.getContactDetail()).isPresent()) {
            ContactDetail contactDetailToUpdate = nextToKinDTO.getContactDetail();
            contactDetail.setPrivatePhone(contactDetailToUpdate.getPrivatePhone());
            contactDetail.setPrivateEmail(contactDetailToUpdate.getPrivateEmail());
            contactDetail.setMobilePhone(contactDetailToUpdate.getMobilePhone());
            contactDetail.setFacebookAccount(contactDetailToUpdate.getFacebookAccount());
            contactDetail.setTwitterAccount(contactDetailToUpdate.getTwitterAccount());
            contactDetail.setLinkedInAccount(contactDetailToUpdate.getLinkedInAccount());
            contactDetail.setMessenger(contactDetailToUpdate.getMessenger());
            contactDetail.setWorkPhone(contactDetailToUpdate.getWorkPhone());
        }
        return contactDetail;
    }

}


package com.kairos.persistence.model.user.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.enums.ClientEnum;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.country.CitizenStatus;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.staff.Staff;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 27/9/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client extends User {


    private boolean isEnabled = true;
    private CitizenStatus civilianStatus;
    private String profilePic;

    @JsonIgnore
    @Relationship(type = NEXT_TO_KIN)
    private Client nextToKin;

    private String nameAmongStaff;

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

    public int getMostDrivenKm() {
        return mostDrivenKm;
    }

    public void setMostDrivenKm(int mostDrivenKm) {
        this.mostDrivenKm = mostDrivenKm;
    }

    private int mostDrivenKm;




    // Constructors
    public Client() {
    }

    public Client(String cprNumber, Gender gender, String firstName, String lastName, String nickName) {
        this.setCprNumber(cprNumber);
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nameAmongStaff = nickName;
    }


    public int getUpdatedInfo1And2Count() {
        return updatedInfo1And2Count;
    }

    public void setUpdatedInfo1And2Count(int updatedInfo1And2Count) {
        this.updatedInfo1And2Count = updatedInfo1And2Count;
    }

    public int getNotDraggedAndDroppedDemands() {
        return notDraggedAndDroppedDemands;
    }

    public void setNotDraggedAndDroppedDemands(int notDraggedAndDroppedDemands) {
        this.notDraggedAndDroppedDemands = notDraggedAndDroppedDemands;
    }

    public int getOnEscalationListMinPKm() {
        return onEscalationListMinPKm;
    }

    public void setOnEscalationListMinPKm(int onEscalationListMinPKm) {
        this.onEscalationListMinPKm = onEscalationListMinPKm;
    }

    public int getUnplannedMinPKm() {
        return unplannedMinPKm;
    }

    public void setUnplannedMinPKm(int unplannedMinPKm) {
        this.unplannedMinPKm = unplannedMinPKm;
    }

    public int getLongDrivingTimeInMin() {
        return longDrivingTimeInMin;
    }

    public void setLongDrivingTimeInMin(int longDrivingTimeInMin) {
        this.longDrivingTimeInMin = longDrivingTimeInMin;
    }

    public String getVisitourTeamId() {
        return visitourTeamId;
    }

    public void setVisitourTeamId(String visitourTeamId) {
        this.visitourTeamId = visitourTeamId;
    }

    public boolean isCitizenDead() {
        return citizenDead;
    }

    public void setCitizenDead(boolean citizenDead) {
        this.citizenDead = citizenDead;
    }

    public String getNameAmongStaff() {
        return nameAmongStaff;
    }

    public void setNameAmongStaff(String nameAmongStaff) {
        this.nameAmongStaff = nameAmongStaff;
    }

    public Client getNextToKin() {
        return nextToKin;
    }

    public void setNextToKin(Client nextToKin) {
        this.nextToKin = nextToKin;
    }

    public boolean isUseWheelChair() {
        return useWheelChair;
    }

    public void setUseWheelChair(boolean useWheelChair) {
        this.useWheelChair = useWheelChair;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public CitizenStatus getCivilianStatus() {
        return civilianStatus;
    }

    public void setCivilianStatus(CitizenStatus civilianStatus) {
        this.civilianStatus = civilianStatus;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


    public boolean isWantToUserOwnVehicle() {
        return wantToUserOwnVehicle;
    }

    public void setWantToUserOwnVehicle(boolean wantToUserOwnVehicle) {
        this.wantToUserOwnVehicle = wantToUserOwnVehicle;
    }

    public String getRequiredEquipmentsList() {
        return requiredEquipmentsList;
    }

    public void setRequiredEquipmentsList(String requiredEquipmentsList) {
        this.requiredEquipmentsList = requiredEquipmentsList;
    }

    public boolean isDoHaveFreeChoiceServices() {
        return doHaveFreeChoiceServices;
    }

    public void setDoHaveFreeChoiceServices(boolean doHaveFreeChoiceServices) {
        this.doHaveFreeChoiceServices = doHaveFreeChoiceServices;
    }


    public ContactAddress getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(ContactAddress officeAddress) {
        this.officeAddress = officeAddress;
    }

    public ContactAddress getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(ContactAddress homeAddress) {
        this.homeAddress = homeAddress;
    }

    public ContactAddress getSecondaryAddress() {
        return secondaryAddress;
    }

    public void setSecondaryAddress(ContactAddress secondaryAddress) {
        this.secondaryAddress = secondaryAddress;
    }

    public ContactAddress getPartnerAddress() {
        return partnerAddress;
    }

    public void setPartnerAddress(ContactAddress partnerAddress) {
        this.partnerAddress = partnerAddress;
    }

    public List<ClientTemporaryAddress> getTemporaryAddress() {
        return Optional.ofNullable(temporaryAddress).orElse(new ArrayList<>());
    }

    public void setTemporaryAddress(List<ClientTemporaryAddress> temporaryAddress) {
        this.temporaryAddress = temporaryAddress;
    }

    public ClientEnum.CitizenShip getCitizenship() {
        return citizenship;
    }

    public void setPeopleInHousehold(boolean peopleInHousehold) {
        this.peopleInHousehold = peopleInHousehold;
    }

    public void setCitizenship(ClientEnum.CitizenShip citizenship) {
        this.citizenship = citizenship;
    }

    public String getNationalityType() {
        return nationalityType;
    }

    public void setNationalityType(String nationalityType) {
        this.nationalityType = nationalityType;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public boolean isCitizenGettingPension() {
        return isCitizenGettingPension;
    }

    public void setCitizenGettingPension(boolean citizenGettingPension) {
        this.isCitizenGettingPension = citizenGettingPension;
    }

    public boolean isPeopleInHousehold() {
        return peopleInHousehold;
    }

    public String getCitizenState() {
        return citizenState;
    }

    public void setCitizenState(String citizenState) {
        this.citizenState = citizenState;
    }

    public boolean isLivesAlone() {
        return livesAlone;
    }

    public void setLivesAlone(boolean livesAlone) {
        this.livesAlone = livesAlone;
    }

    public boolean isDoRequireTranslationAssistance() {
        return doRequireTranslationAssistance;
    }

    public void setDoRequireTranslationAssistance(boolean doRequireTranslationAssistance) {
        this.doRequireTranslationAssistance = doRequireTranslationAssistance;
    }

    public String[] getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String[] translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public String getPortPhoneNumber() {
        return portPhoneNumber;
    }

    public void setPortPhoneNumber(String portPhoneNumber) {
        this.portPhoneNumber = portPhoneNumber;
    }

    public String getElectronicKeyNumber() {
        return electronicKeyNumber;
    }

    public void setElectronicKeyNumber(String electronicKeyNumber) {
        this.electronicKeyNumber = electronicKeyNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentExtra() {
        return commentExtra;
    }

    public void setCommentExtra(String commentExtra) {
        this.commentExtra = commentExtra;
    }

    public int getSpeakDanishLevel() {
        return speakDanishLevel;
    }

    public void setSpeakDanishLevel(int speakDanishLevel) {
        this.speakDanishLevel = speakDanishLevel;
    }

    public int getWriteDanishLevel() {
        return writeDanishLevel;
    }

    public void setWriteDanishLevel(int writeDanishLevel) {
        this.writeDanishLevel = writeDanishLevel;
    }

    public int getReadDanishLevel() {
        return readDanishLevel;
    }

    public void setReadDanishLevel(int readDanishLevel) {
        this.readDanishLevel = readDanishLevel;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public List<Language> getLanguageUnderstands() {
        return languageUnderstands;
    }

//    public void setLanguageUnderstands(List<Language> languageUnderstands) {
//        this.languageUnderstands = languageUnderstands;
//    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public boolean isWheelChair() {
        return wheelChair;
    }

    public void setWheelChair(boolean wheelChair) {
        this.wheelChair = wheelChair;
    }

    public boolean isLiftBus() {
        return liftBus;
    }

    public void setLiftBus(boolean liftBus) {
        this.liftBus = liftBus;
    }

    public boolean isRequire2peopleForTransport() {
        return require2peopleForTransport;
    }

    public void setRequire2peopleForTransport(boolean require2peopleForTransport) {
        this.require2peopleForTransport = require2peopleForTransport;
    }

    public boolean isRequireMinibusForTransport() {
        return requireMinibusForTransport;
    }

    public void setRequireMinibusForTransport(boolean requireMinibusForTransport) {
        this.requireMinibusForTransport = requireMinibusForTransport;
    }

    public boolean isRequireOxygenUnderTransport() {
        return requireOxygenUnderTransport;
    }

    public void setRequireOxygenUnderTransport(boolean requireOxygenUnderTransport) {
        this.requireOxygenUnderTransport = requireOxygenUnderTransport;
    }

    public boolean isMemberOfDenmark() {
        return isMemberOfDenmark;
    }

    public void setMemberOfDenmark(boolean memberOfDenmark) {
        isMemberOfDenmark = memberOfDenmark;
    }

    public boolean isCitizenGetPension() {
        return IsCitizenGetPension;
    }

    public void setCitizenGetPension(boolean citizenGetPension) {
        IsCitizenGetPension = citizenGetPension;
    }

    public String getCitizenPensionType() {
        return citizenPensionType;
    }

    public void setCitizenPensionType(String citizenPensionType) {
        this.citizenPensionType = citizenPensionType;
    }

    public boolean isCitizenTerminal() {
        return isCitizenTerminal;
    }

    public void setCitizenTerminal(boolean citizenTerminal) {
        isCitizenTerminal = citizenTerminal;
    }

    public String getCitizenTerminalDescription() {
        return CitizenTerminalDescription;
    }

    public void setCitizenTerminalDescription(String citizenTerminalDescription) {
        CitizenTerminalDescription = citizenTerminalDescription;
    }

    public boolean isDoesHaveAllergies() {
        return doesHaveAllergies;
    }

    public void setDoesHaveAllergies(boolean doesHaveAllergies) {
        this.doesHaveAllergies = doesHaveAllergies;
    }

    public boolean isDoesHaveDiseases() {
        return doesHaveDiseases;
    }

    public void setDoesHaveDiseases(boolean doesHaveDiseases) {
        this.doesHaveDiseases = doesHaveDiseases;
    }

    public boolean isDoesHaveDiagnoses() {
        return doesHaveDiagnoses;
    }

    public void setDoesHaveDiagnoses(boolean doesHaveDiagnoses) {
        this.doesHaveDiagnoses = doesHaveDiagnoses;
    }

    public Long getAvailabilityDateFrom() {
        return availabilityDateFrom;
    }

    public void setAvailabilityDateFrom(Long availabilityDateFrom) {
        this.availabilityDateFrom = availabilityDateFrom;
    }

    public Long getAvailabilityDateTo() {
        return availabilityDateTo;
    }

    public void setAvailabilityDateTo(Long availabilityDateTo) {
        this.availabilityDateTo = availabilityDateTo;
    }

    public List<ClientAllergies> getClientAllergiesList() {
        return clientAllergiesList;
    }

    public void setClientAllergiesList(List<ClientAllergies> clientAllergiesList) {
        this.clientAllergiesList = clientAllergiesList;
    }

    public List<ClientDisease> getClientDiseaseList() {
        return clientDiseaseList;
    }

    public void setClientDiseaseList(List<ClientDisease> clientDiseaseList) {
        this.clientDiseaseList = clientDiseaseList;
    }

    public List<ClientDiagnose> getClientDiagnoseList() {
        return clientDiagnoseList;
    }

    public void setClientDiagnoseList(List<ClientDiagnose> clientDiagnoseList) {
        this.clientDiagnoseList = clientDiagnoseList;
    }

    public List<ClientDoctor> getClientDoctorList() {
        return clientDoctorList;
    }

    public void setClientDoctorList(List<ClientDoctor> clientDoctorList) {
        this.clientDoctorList = clientDoctorList;
    }

    public void setClientType(ClientEnum clientType) {
        ClientEnum clientType1 = clientType;
    }

    public boolean isUseOwnVehicle() {
        return useOwnVehicle;
    }

    public void setUseOwnVehicle(boolean useOwnVehicle) {
        this.useOwnVehicle = useOwnVehicle;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    public Boolean getImportFromKMD() {
        return importFromKMD;
    }

    public void setImportFromKMD(Boolean importFromKMD) {
        this.importFromKMD = importFromKMD;
    }

    public String getKmdNexusExternalId() {
        return kmdNexusExternalId;
    }

    public void setKmdNexusExternalId(String kmdNexusExternalId) {
        this.kmdNexusExternalId = kmdNexusExternalId;
    }

    // General Tab Constructor
    public Map<String, Object> retrieveClientGeneralDetails() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", super.getId());
        map.put("cprNumber", this.cprNumber);
        map.put("firstName", this.firstName);
        map.put("lastName", this.lastName);
        map.put("nameAmongStaff", this.nameAmongStaff);
    //    map.put("civilianStatus", this.civilianStatus);
        map.put("age", this.age);
        map.put("gender", this.gender);
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
        return map;
    }

    // Transportation Tab Constructor
    public Map<String, Object> retrieveTransportationDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("driverLicenseNumber", this.driverLicenseNumber != null ? this.driverLicenseNumber : "");
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

        return map;
    }


    public Map<String, Object> retrieveNextToKinDetails() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> contactDetails = new HashMap<>();
        Map<String, Object> homeAddress = new HashMap<>();

        response.put("id", this.id);
        response.put("firstName", this.firstName != null ? this.firstName : "");
        response.put("lastName", this.lastName != null ? this.lastName : "");
        response.put("nickName", this.nickName != null ? this.nickName : "");
        response.put("name", this.firstName + " " + this.lastName);
        response.put("cprNumber", this.cprNumber != null ? this.cprNumber : "");
        response.put("profilePic", this.profilePic);
        response.put("civilianStatus", this.civilianStatus);
        response.put("gender", getGender());
        response.put("age", getAge());

        if (this.homeAddress != null) {
            homeAddress.put("houseNumber", this.homeAddress.getHouseNumber());
            homeAddress.put("floorNumber", this.homeAddress.getFloorNumber());
            homeAddress.put("street1", this.homeAddress.getStreet1());
            homeAddress.put("latitude", this.homeAddress.getLatitude());
            homeAddress.put("zipCode", this.homeAddress.getZipCode());
            homeAddress.put("longitude", this.homeAddress.getLongitude());
//            homeAddress.put("province", this.homeAddress.getProvince());

        }
        if (this.contactDetail != null) {
            if (this.contactDetail.isHidePrivatePhone() == false) {
                contactDetails.put("privatePhone", this.contactDetail.getPrivatePhone());
            }
            if (this.contactDetail.isHideMobilePhone() == false) {
                contactDetails.put("mobilePhone", this.contactDetail.getMobilePhone());
            }
            if (this.contactDetail.isHideWorkPhone() == false) {
                contactDetails.put("workPhone", this.contactDetail.getWorkPhone());
            }
            if (this.contactDetail.isHideLandlinePhone() == false) {
                contactDetails.put("landLinePhone", this.contactDetail.getLandLinePhone());
            }

            contactDetails.put("facebookAccount", this.contactDetail.getFacebookAccount());
            contactDetails.put("linkedInAccount", this.contactDetail.getLinkedInAccount());
            contactDetails.put("twitterAccount", this.contactDetail.getTwitterAccount());
            contactDetails.put("privateEmail", this.contactDetail.getPrivateEmail());
            contactDetails.put("messenger", this.contactDetail.getMessenger());
        }
        response.put("homeAddress", this.homeAddress);
        response.put("contactDetail", contactDetails);

        return response;
    }

    public void setContactPerson(Staff contactPerson) {
        Staff contactPerson1 = contactPerson;
    }


    public Map<String, Object> retrieveMinimumDetails() {
        Map<String, Object> response = new HashMap<>();

        response.put("id", this.id);
        response.put("gender", this.gender);
        response.put("name", this.firstName + " " + this.lastName);

        return response;
    }

    public int getNoOfVisitationHours() {
        return noOfVisitationHours;
    }

    public void setNoOfVisitationHours(int noOfVisitationHours) {
        this.noOfVisitationHours = noOfVisitationHours;
    }

    public int getNoOfVisitationTasks() {
        return noOfVisitationTasks;
    }

    public void setNoOfVisitationTasks(int noOfVisitationTasks) {
        this.noOfVisitationTasks = noOfVisitationTasks;
    }

    public String getFullName(){
        return this.firstName+" "+this.lastName;
    }

    @Override
    public String toString() {
        return "{Client={" +
                "isEnabled=" + isEnabled + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", nextToKin=" + nextToKin +
                ", nameAmongStaff='" + nameAmongStaff + '\'' +
                ", requiredEquipmentsList='" + requiredEquipmentsList + '\'' +
                ", doHaveFreeChoiceServices=" + doHaveFreeChoiceServices +
                ", citizenship=" + citizenship +
                ", nationalityType='" + nationalityType + '\'' +
                ", wantToUserOwnVehicle=" + wantToUserOwnVehicle +
                ", isCitizenGettingPension=" + isCitizenGettingPension +
                ", priority=" + priority +
                ", useOwnVehicle=" + useOwnVehicle +
                ", peopleInHousehold=" + peopleInHousehold +
                ", livesAlone=" + livesAlone +
                ", doRequireTranslationAssistance=" + doRequireTranslationAssistance +
                ", translationLanguage=" + Arrays.toString(translationLanguage) +
                ", portPhoneNumber='" + portPhoneNumber + '\'' +
                ", electronicKeyNumber='" + electronicKeyNumber + '\'' +
                ", comment='" + comment + '\'' +
                ", commentExtra='" + commentExtra + '\'' +
                ", speakDanishLevel=" + speakDanishLevel +
                ", writeDanishLevel=" + writeDanishLevel +
                ", readDanishLevel=" + readDanishLevel +
                ", occupation='" + occupation + '\'' +
                ", driverLicenseNumber='" + driverLicenseNumber + '\'' +
                ", languageUnderstands=" + languageUnderstands +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", wheelChair=" + wheelChair +
                ", liftBus=" + liftBus +
                ", require2peopleForTransport=" + require2peopleForTransport +
                ", requireMinibusForTransport=" + requireMinibusForTransport +
                ", requireOxygenUnderTransport=" + requireOxygenUnderTransport +
                ", useWheelChair=" + useWheelChair +
                ", isMemberOfDenmark=" + isMemberOfDenmark +
                ", IsCitizenGetPension=" + IsCitizenGetPension +
                ", citizenPensionType='" + citizenPensionType + '\'' +
                ", isCitizenTerminal=" + isCitizenTerminal +
                ", CitizenTerminalDescription='" + CitizenTerminalDescription + '\'' +
                ", citizenDead=" + citizenDead +
                ", doesHaveAllergies=" + doesHaveAllergies +
                ", doesHaveDiseases=" + doesHaveDiseases +
                ", doesHaveDiagnoses=" + doesHaveDiagnoses +
                ", availabilityDateFrom=" + availabilityDateFrom +
                ", availabilityDateTo=" + availabilityDateTo +
                ", visitourTeamId='" + visitourTeamId + '\'' +
                ", secondaryEmail='" + secondaryEmail + '\'' +
                ", citizenState='" + citizenState + '\'' +
                ", importFromKMD=" + importFromKMD +
                ", kmdNexusExternalId='" + kmdNexusExternalId + '\'' +
                ", updatedInfo1And2Count=" + updatedInfo1And2Count +
                ", notDraggedAndDroppedDemands=" + notDraggedAndDroppedDemands +
                ", onEscalationListMinPKm=" + onEscalationListMinPKm +
                ", unplannedMinPKm=" + unplannedMinPKm +
                ", longDrivingTimeInMin=" + longDrivingTimeInMin +
                ", mostDrivenKm=" + mostDrivenKm +
                '}'+
                '}';
    }
}


package com.kairos.user.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.Gender;
import com.kairos.user.staff.ContactAddress;
import com.kairos.user.staff.ContactDetail;


/**
 * Created by oodles on 27/9/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private boolean isEnabled = true;
    private String profilePic;
    private String nameAmongStaff;

    // Equipment Details
    private String requiredEquipmentsList;

    private boolean doHaveFreeChoiceServices;


    private ContactAddress officeAddress;

    private ContactAddress homeAddress;

    private ContactAddress secondaryAddress;

    private ContactAddress partnerAddress;

    private String nationalityType;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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


    public void setPeopleInHousehold(boolean peopleInHousehold) {
        this.peopleInHousehold = peopleInHousehold;
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

}


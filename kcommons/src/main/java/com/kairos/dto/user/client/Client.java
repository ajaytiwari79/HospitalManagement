package com.kairos.dto.user.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.staff.ContactAddress;
import com.kairos.dto.user.staff.ContactDetail;
import com.kairos.enums.Gender;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by oodles on 27/9/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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
    private int mostDrivenKm;



}


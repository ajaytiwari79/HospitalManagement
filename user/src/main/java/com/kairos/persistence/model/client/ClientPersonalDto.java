package com.kairos.persistence.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.client.ClientEnum;
import com.kairos.enums.Gender;
import com.kairos.persistence.model.country.default_data.CitizenStatus;
import com.kairos.persistence.model.user.language.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by oodles on 24/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientPersonalDto {
    private Long id;
    private String nickName;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String cprNumber;
    private String nameAmongStaff;
    CitizenStatus civilianStatus;
    private ClientEnum.CitizenShip citizenship;
    String occupation;
    boolean doHaveFreeChoiceServices;
    boolean doRequireTranslationAssistance;
    String nationalityType;
    private List<Language> languageUnderstands;
    private String profilePic;
    private String visitourTeamId;

    public String getVisitourTeamId() {
        return visitourTeamId;
    }

    public void setVisitourTeamId(String visitourTeamId) {
        this.visitourTeamId = visitourTeamId;
    }

    private boolean livesAlone;
    private boolean peopleInHousehold;

    public boolean isLivesAlone() {
        return livesAlone;
    }

    public void setLivesAlone(boolean livesAlone) {
        this.livesAlone = livesAlone;
    }

    public boolean isPeopleInHousehold() {
        return peopleInHousehold;
    }

    public void setPeopleInHousehold(boolean peopleInHousehold) {
        this.peopleInHousehold = peopleInHousehold;
    }

    public String getNameAmongStaff() {
        return nameAmongStaff;
    }

    public void setNameAmongStaff(String nameAmongStaff) {
        this.nameAmongStaff = nameAmongStaff;
    }

    public ClientEnum.CitizenShip getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(ClientEnum.CitizenShip citizenship) {
        this.citizenship = citizenship;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public CitizenStatus getCivilianStatus() {
        return civilianStatus;
    }

    public void setCivilianStatus(CitizenStatus civilianStatus) {
        this.civilianStatus = civilianStatus;
    }

    public boolean isDoHaveFreeChoiceServices() {
        return doHaveFreeChoiceServices;
    }

    public void setDoHaveFreeChoiceServices(boolean doHaveFreeChoiceServices) {
        this.doHaveFreeChoiceServices = doHaveFreeChoiceServices;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public boolean isDoRequireTranslationAssistance() {
        return doRequireTranslationAssistance;
    }

    public void setDoRequireTranslationAssistance(boolean doRequireTranslationAssistance) {
        this.doRequireTranslationAssistance = doRequireTranslationAssistance;
    }

    public String getNationalityType() {
        return nationalityType;
    }

    public void setNationalityType(String nationalityType) {
        this.nationalityType = nationalityType;
    }

    public List<Language> getLanguageUnderstands() {
        return Optional.ofNullable(languageUnderstands).orElse(new ArrayList<>());
    }

    public void setLanguageUnderstands(List<Language> languageUnderstands) {
        this.languageUnderstands = languageUnderstands;
    }


    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}

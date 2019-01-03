package com.kairos.persistence.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.Gender;
import com.kairos.enums.user.UserType;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.client.NextToKinDTO;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.user.profile.Profile;
import com.kairos.persistence.model.user_personalized_settings.UserPersonalizedSettings;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * User Domain & it's properties
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends UserBaseEntity {
    protected String cprNumber;

    private String userName;
    protected String nickName;
    protected String firstName;
    protected String lastName;
    protected Gender gender;
    private boolean pregnant;
    private String email;
    private Long lastSelectedOrganizationId;
    private LocalDate dateOfBirth;

    //uniqueness of user
    private String timeCareExternalId;

    @NotNull(message = "error.User.password.notnull")
    @Size(min = 8, max = 50, message = "error.User.password.size")
    private String password;

    protected int age;
    private String accessToken;
    private List<String> roles;
    private ContactDetail contactDetail;
    private ContactAddress homeAddress;

    @Relationship(type = ADMINS_COUNTRY)
    private List<Country> countryList;

    @Relationship(type = HAS_PROFILE)
    private Profile profile;

    private int otp;
    //sendgrid mail
    private String forgotPasswordToken;

    private LocalDateTime forgotTokenRequestTime;

    //define, first time password changed or not
    private boolean isPasswordUpdated;

    private Long kmdExternalId;
    private UserType userType = UserType.USER_ACCOUNT;

    @Transient
    private Boolean hubMember;


    @Relationship(type = HAS_PERSONALIZED_SETTINGS)
    private UserPersonalizedSettings userPersonalizedSettings;

    @Relationship(type = SELECTED_LANGUAGE)
    private SystemLanguage userLanguage;

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public ContactAddress getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(ContactAddress homeAddress) {
        this.homeAddress = homeAddress;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getKmdExternalId() {
        return kmdExternalId;
    }

    public void setKmdExternalId(Long kmdExternalId) {
        this.kmdExternalId = kmdExternalId;
    }

    public LocalDateTime getForgotTokenRequestTime() {
        return forgotTokenRequestTime;
    }

    public void setForgotTokenRequestTime(LocalDateTime forgotTokenRequestTime) {
        this.forgotTokenRequestTime = forgotTokenRequestTime;
    }

    /**
     * getAccessToken
     *
     * @return
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * setAccessToken
     *
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getForgotPasswordToken() {
        return forgotPasswordToken;
    }

    public void setForgotPasswordToken(String forgotPasswordToken) {
        this.forgotPasswordToken = forgotPasswordToken;
    }

    /**
     * getUserName
     *
     * @return
     */
    public String getUserName() {
        if (userName != null) {
            userName.toLowerCase();
        }
        return userName;

    }

    /**
     * setUserName
     *
     * @param userName
     */
    public void setUserName(String userName) {
        if (userName != null)
            this.userName = userName.toLowerCase();
    }


    /**
     * getEmail
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * setEmail
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * getPassword
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * setPassword
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    public int getAge() {
        int age = 0;
        if (cprNumber == null) {
            return this.age;

        }
        if (cprNumber.length() == 9) {
            cprNumber = "0" + cprNumber;
        }
        //System.out.print("\n CPR: ----"+cprNumber+"---\n");
        if (cprNumber != null) {
            Integer year = Integer.valueOf(cprNumber.substring(4, 6));
            Integer month = Integer.valueOf(cprNumber.substring(2, 4));
            Integer day = Integer.valueOf(cprNumber.substring(0, 2));
            Integer century = Integer.parseInt(cprNumber.substring(6, 7));

            if (century >= 0 && century <= 3) {
                century = 1900;
            }
            if (century == 4) {
                if (year <= 36) {
                    century = 2000;
                } else {
                    century = 1900;
                }
            }
            if (century >= 5 && century <= 8) {
                if (year <= 57) {
                    century = 2000;
                }
                if (year >= 58 && year <= 99) {
                    century = 1800;
                }
            }
            if (century == 9) {
                if (year <= 36) {
                    century = 2000;
                } else {
                    century = 1900;
                }
            }
            year = century + year;
            LocalDate today = LocalDate.now();
            LocalDate birthday = LocalDate.of(year, month, day);
            // Calculating age in yeas from DOB
            Period period = Period.between(birthday, today);
            age = period.getYears();
            this.age = age;
        }
        return this.age;
    }


    public void setAge(int age) {
        this.age = age;
    }


    public UserPersonalizedSettings getUserPersonalizedSettings() {
        return userPersonalizedSettings;
    }

    public void setUserPersonalizedSettings(UserPersonalizedSettings userPersonalizedSettings) {
        this.userPersonalizedSettings = userPersonalizedSettings;
    }

    /**
     * For Jackson parsing
     */
    public User() {
    }

    /**
     * User Constructor
     *
     * @param userName
     * @param password
     */
    public User(String userName, String password) {
        this.firstName = userName;
        this.password = password;
    }

    public User(String userName, String password, ContactDetail contactDetail, ContactAddress contactAddress) {
        this.userName = userName;
        this.password = password;
        this.homeAddress = contactAddress;
        this.contactDetail = contactDetail;
    }

    /**
     * @param firstName
     * @param lastName
     * @param cprNumber
     * @param dateOfBirth
     * @Use while uploading multiple client in batch
     */
    public User(String firstName, String lastName, String cprNumber, LocalDate dateOfBirth) {
        this.cprNumber = cprNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * User Constructor
     *
     * @param name
     * @param userName
     * @param email
     * @param password
     * @param age
     */
    public User(String name, String userName, String email, String password, int age, Profile profile) {
        this.firstName = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.age = age;
        this.profile = profile;
    }

    public User(String name, String userName, String email, String password, int age) {
        this.firstName = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.age = age;
    }


    public User(String cprNumber, String firstName, String lastName, String email, String userName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cprNumber = cprNumber;
        this.userName = userName;
    }

    /**
     * Constructor for User with CountryList
     *
     * @param name
     * @param userName
     * @param email
     * @param password
     * @param age
     * @param countryList
     */
    public User(String name, String userName, String email, String password, int age, List<Country> countryList) {
        this.firstName = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.age = age;
        this.countryList = countryList;
    }

    public String getTimeCareExternalId() {
        return timeCareExternalId;
    }

    public void setTimeCareExternalId(String timeCareExternalId) {
        this.timeCareExternalId = timeCareExternalId;
    }

    public boolean isPasswordUpdated() {
        return isPasswordUpdated;
    }

    public void setPasswordUpdated(boolean passwordUpdated) {
        isPasswordUpdated = passwordUpdated;
    }

    @Override
    public String toString() {
        return "{User={" +
                "cprNumber='" + cprNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", timeCareExternalId='" + timeCareExternalId + '\'' +
                ", age=" + age +
                ", accessToken='" + accessToken + '\'' +
                ", otp=" + otp +
                ", isPasswordUpdated=" + isPasswordUpdated +
                '}' +
                '}';
    }

    public Boolean getHubMember() {
        return hubMember;
    }

    public void setHubMember(Boolean hubMember) {
        this.hubMember = hubMember;
    }

    public Long getLastSelectedOrganizationId() {
        return lastSelectedOrganizationId;
    }

    public void setLastSelectedOrganizationId(Long lastSelectedOrganizationId) {
        this.lastSelectedOrganizationId = lastSelectedOrganizationId;
    }


    public boolean isPregnant() {
        return pregnant;
    }

    public void setPregnant(boolean pregnant) {
        this.pregnant = pregnant;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public SystemLanguage getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(SystemLanguage userLanguage) {
        this.userLanguage = userLanguage;
    }

    public void setBasicDetail(NextToKinDTO nextToKinDTO) {
        this.setFirstName(nextToKinDTO.getFirstName());
        this.setLastName(nextToKinDTO.getLastName());
        this.setNickName(nextToKinDTO.getNickName());
        this.setCprNumber(nextToKinDTO.getCprNumber());
        Integer ageVariable = Integer.valueOf(nextToKinDTO.getCprNumber().substring(nextToKinDTO.getCprNumber().length() - 1));
        this.setGender((ageVariable % 2 == 0) ? Gender.FEMALE : Gender.MALE);
    }


    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}

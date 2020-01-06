package com.kairos.persistence.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.annotations.KPermissionField;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.Gender;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.user.ChatStatus;
import com.kairos.enums.user.UserType;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.client.NextToKinDTO;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.user.profile.Profile;
import com.kairos.persistence.model.user_personalized_settings.UserPersonalizedSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.DateUtils.getCurrentLocalDate;
import static com.kairos.constants.UserMessagesConstants.ERROR_USER_PASSCODE_NOTNULL;
import static com.kairos.constants.UserMessagesConstants.ERROR_USER_PASSCODE_SIZE;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;
import static com.kairos.utils.CPRUtil.getDateOfBirthFromCPR;

/**
 * User Domain & it's properties
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class User extends UserBaseEntity {
    @KPermissionField
    protected String cprNumber;
    private String userName;
    @KPermissionField
    protected String nickName;

    protected String firstName;
    protected String lastName;
    protected Gender gender;
    @KPermissionField
    private boolean pregnant;
    private String email;
    private ConfLevel confLevel;
    private Long lastSelectedOrganizationId;
    private OrganizationCategory lastSelectedOrganizationCategory;
    private LocalDate dateOfBirth;
    @NotNull(message = ERROR_USER_PASSCODE_NOTNULL)
    @Size(min = 8, max = 50, message = ERROR_USER_PASSCODE_SIZE)
    private String password;

    protected int age;
    private String accessToken;
    private List<String> roles;
    private ContactDetail contactDetail;
    private ContactAddress homeAddress;
    private LocalDate joiningDate;


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

    //define, first time UserName updated or not
    private boolean userNameUpdated;

    private Long countryId;

    //define for personal google calender
    private String googleCalenderTokenId;
    //define for personal google calender
    private String googleCalenderAccessToken;
    @Properties
    private Map<String, String> unitWiseAccessRole=new HashMap<>();
    private ChatStatus chatStatus;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String firstName, String lastName, String cprNumber, LocalDate dateOfBirth) {
        this.cprNumber = cprNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.setJoiningDate(getCurrentLocalDate());
    }

    public User(String cprNumber, String firstName, String lastName, String email, String userName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cprNumber = cprNumber;
        this.userName = userName;
        this.setDateOfBirth(getDateOfBirthFromCPR(cprNumber));
        this.setJoiningDate(getCurrentLocalDate());
    }

    public User(String cprNumber, String firstName, String lastName, String email, String userName, boolean isUserNameUpdated) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cprNumber = cprNumber;
        this.userName = userName;
        this.userNameUpdated = isUserNameUpdated;
        this.setJoiningDate(getCurrentLocalDate());
    }

    public User(String userName, String firstName, String lastName, String email, ContactDetail contactDetail, String password) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactDetail = contactDetail;
        this.password = password;
        this.setJoiningDate(getCurrentLocalDate());
    }

    public String getUserName() {
        if(userName != null) {
            userName.toLowerCase();
        }
        return userName;
    }

    public void setBasicDetail(NextToKinDTO nextToKinDTO) {
        this.setFirstName(nextToKinDTO.getFirstName());
        this.setLastName(nextToKinDTO.getLastName());
        this.setNickName(nextToKinDTO.getNickName());
        this.setCprNumber(nextToKinDTO.getCprNumber());
        Integer ageVariable = Integer.valueOf(nextToKinDTO.getCprNumber().substring(nextToKinDTO.getCprNumber().length() - 1));
        this.setGender((ageVariable % 2 == 0) ? Gender.FEMALE : Gender.MALE);
    }

    public String getFullName() {
        return StringUtils.capitalize(this.firstName) + " " + StringUtils.capitalize(this.lastName);
    }

    public int getAge() {
        int age = 0;
        if(cprNumber == null) {
            return this.age;

        }
        if(cprNumber.length() == 9) {
            cprNumber = "0" + cprNumber;
        }
        Integer year = Integer.valueOf(cprNumber.substring(4, 6));
        Integer month = Integer.valueOf(cprNumber.substring(2, 4));
        Integer day = Integer.valueOf(cprNumber.substring(0, 2));
        Integer century = Integer.parseInt(cprNumber.substring(6, 7));
        if(century >= 0 && century <= 3) {
            century = 1900;
        }
        century = getCentury(year, century);
        year = century + year;
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(year, month, day);
        // Calculating age in yeas from DOB
        Period period = Period.between(birthday, today);
        age = period.getYears();
        return this.age = age;
    }

    private Integer getCentury(Integer year, Integer century) {
        if(century == 4) {
            if(year <= 36) {
                century = 2000;
            } else {
                century = 1900;
            }
        }
        if(century >= 5 && century <= 8) {
            if(year <= 57) {
                century = 2000;
            }
            if(year >= 58 && year <= 99) {
                century = 1800;
            }
        }
        if(century == 9) {
            if(year <= 36) {
                century = 2000;
            } else {
                century = 1900;
            }
        }
        return century;
    }

    @Override
    public String toString() {
        return "{User={" + "cprNumber='" + cprNumber + '\'' + ", userName='" + userName + '\'' + ", nickName='" + nickName + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", gender=" + gender + ", email='" + email + '\'' + ", age=" + age + ", accessToken='" + accessToken + '\'' + ", otp=" + otp + ", isPasswordUpdated=" + isPasswordUpdated + '}' + '}';
    }
}

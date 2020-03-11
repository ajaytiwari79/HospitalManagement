package com.kairos.utils.user_context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.kairos.constants.ActivityMessagesConstants.ERROR_USER_PASSCODE_NOTNULL;
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable{
    protected Long id;

    protected String cprNumber;
    private String userName;
    protected String nickName;
    protected String firstName;
    protected String lastName;
    protected Gender gender;
    private String email;

    //uniqueness of user
    private String timeCareExternalId;

    @NotNull(message = ERROR_USER_PASSCODE_NOTNULL)
    @Size(min = 8, max = 50, message = "error.User.password.size")
    private String password;

    protected int age;
    private String accessToken;
    private List<String> roles;

    private int otp;

    //define, first time password changed or not
    private boolean isPasswordUpdated;

    private String googleCalenderTokenId;
    private String googleCalenderAccessToken;


    public User(String name, String userName, String email, String password, int age) {
        this.firstName = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.age = age;
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
                ", password='" + password + '\'' +
                ", age=" + age +
                ", accessToken='" + accessToken + '\'' +
                ", otp=" + otp +
                ", isPasswordUpdated=" + isPasswordUpdated +
                '}'+
                '}';
    }
}

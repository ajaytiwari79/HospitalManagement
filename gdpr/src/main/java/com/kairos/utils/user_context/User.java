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

@Getter
@Setter
@NoArgsConstructor
class User implements Serializable{
    private Long id;

    private String cprNumber;
    private String userName;
    private String nickName;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String email;

    //uniqueness of user
    private String timeCareExternalId;

    @NotNull(message = "error.User.password.notnull")
    @Size(min = 8, max = 50, message = "error.User.password.size")
    private String password;

    private int age;
    private String accessToken;
    private List<String> roles;

    private int otp;

    //define, first time password changed or not
    private boolean isPasswordUpdated;

    private String googleCalenderTokenId;
    private String googleCalenderAccessToken;

    /**
     * getUserName
     *
     * @return
     */
    public String getUserName() {
        if (userName!=null){
            return userName.toLowerCase();
        }
        return userName;

    }

    /**
     * setUserName
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName.toLowerCase();
    }

    public int getAge() {
        int age = 0;
        if (cprNumber==null){
            return this.age;

        }
        if (cprNumber.length()==9){
            cprNumber = "0"+cprNumber;
        }
        //System.out.print("\n CPR: ----"+cprNumber+"---\n");
        if (cprNumber!=null){
            Integer year= Integer.valueOf(cprNumber.substring(4,6));
            Integer month = Integer.valueOf(cprNumber.substring(2,4));
            Integer day= Integer.valueOf(cprNumber.substring(0,2));
            Integer century = Integer.parseInt(cprNumber.substring(6,7));

            if (century>=0 && century<=3){
                century = 1900;
            }
            if (century==4){
                if (year<=36){
                    century = 2000;
                }
                else {
                    century = 1900;
                }
            }
            if (century>=5 && century<=8){
                if (year<=57){
                    century =2000;
                }
                if (year>=58 && year<=99){
                    century = 1800;
                }
            }
            if (century==9){
                if (year<=36){
                    century = 2000;
                }
                else {
                    century = 1900;
                }
            }
            year = century+year;
            LocalDate today = LocalDate.now();
            LocalDate birthday = LocalDate.of(year, month, day);
            // Calculating age in yeas from DOB
            Period period = Period.between(birthday, today);
            age = period.getYears();
            this.age = age;
        }
        return this.age;
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

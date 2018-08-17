package com.kairos.util.user_context;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

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

    @NotNull(message = "error.User.password.notnull")
    @Size(min = 8, max = 50, message = "error.User.password.size")
    private String password;

    protected int age;
    private String accessToken;
    private List<String> roles;

    private int otp;

    //define, first time password changed or not
    private boolean isPasswordUpdated;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    /**
     * For Jackson parsing
     */
    public User() {
        //default constructor
    }


    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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

    /**
     * getUserName
     *
     * @return
     */
    public String getUserName() {
        if (userName!=null){
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


    public void setAge(int age) {
        this.age = age;
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
                ", password='" + password + '\'' +
                ", age=" + age +
                ", accessToken='" + accessToken + '\'' +
                ", otp=" + otp +
                ", isPasswordUpdated=" + isPasswordUpdated +
                '}'+
                '}';
    }
}

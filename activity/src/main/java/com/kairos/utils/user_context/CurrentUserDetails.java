package com.kairos.utils.user_context;

public class CurrentUserDetails {
    private Long id;
    private String userName;
    protected String nickName;
    protected String firstName;
    protected String lastName;
    private String email;
    private boolean passwordUpdated;
    private  int age;
    private Long countryId;
    private boolean hubMember;
    private Long languageId;
    private Long lastSelectedOrganizationId;

    public boolean isPasswordUpdated() {
        return passwordUpdated;
    }

    public void setPasswordUpdated(boolean passwordUpdated) {
        this.passwordUpdated = passwordUpdated;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public CurrentUserDetails(){
        //default constructor
    }

    public CurrentUserDetails(Long id, String userName, String nickName,
                              String firstName, String lastName, String email,
                              boolean hubMember) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hubMember =  hubMember;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHubMember() {
        return hubMember;
    }

    public void setHubMember(boolean hubMember) {
        this.hubMember = hubMember;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public Long getLastSelectedOrganizationId() {
        return lastSelectedOrganizationId;
    }

    public void setLastSelectedOrganizationId(Long lastSelectedOrganizationId) {
        this.lastSelectedOrganizationId = lastSelectedOrganizationId;
    }
}

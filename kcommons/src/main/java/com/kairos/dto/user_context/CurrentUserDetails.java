package com.kairos.dto.user_context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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


    public CurrentUserDetails(Long id, String userName, String nickName,
                              String firstName, String lastName, String email,boolean passwordUpdated) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordUpdated=passwordUpdated;
    }

    @JsonIgnore
    public String getFullName(){
        return this.firstName+" "+this.lastName;
    }

}

package com.kairos.dto.user_context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.OrganizationCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.kairos.dto.user.access_permission.AccessGroupRole.MANAGEMENT;
import static com.kairos.dto.user.access_permission.AccessGroupRole.STAFF;

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
    private Long languageId;
    private Long lastSelectedOrganizationId;
    private OrganizationCategory lastSelectedOrganizationCategory;
    private Map<String, String> unitWiseAccessRole=new HashMap<>();
    private String profilePic;
    private boolean systemAdmin;


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


    @JsonIgnore
    public boolean isManagement(){
        return MANAGEMENT.name().equalsIgnoreCase(unitWiseAccessRole.get(lastSelectedOrganizationId.toString()));
    }

    @JsonIgnore
    public boolean isStaff(){
        return STAFF.name().equalsIgnoreCase(unitWiseAccessRole.get(lastSelectedOrganizationId.toString()));
    }

}

package com.kairos.utils.user_context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CurrentUserDetails {
    private Long id;
    private String userName;
    private String nickName;
    private String firstName;
    private String lastName;
    private String email;
    private boolean passwordUpdated;
    private  int age;
    private Long countryId;
    private boolean hubMember;
    private Long languageId;
    private Long lastSelectedOrganizationId;
    private String googleCalenderTokenId;
    private String googleCalenderAccessToken;

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
}

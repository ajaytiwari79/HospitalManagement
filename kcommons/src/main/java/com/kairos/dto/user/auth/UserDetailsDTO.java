package com.kairos.dto.user.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsDTO {

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
}

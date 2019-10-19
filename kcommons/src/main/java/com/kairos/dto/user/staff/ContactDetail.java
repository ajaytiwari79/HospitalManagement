package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ContactDetail {

    private String workEmail;
    private String privateEmail;
    private String workPhone;
    private String landLinePhone;
    private String privatePhone;
    private String mobilePhone;
    private String facebookAccount;
    private String messenger;
    private String twitterAccount;
    private String linkedInAccount;
    private boolean hidePrivatePhone;
    private boolean hideLandlinePhone;
    private boolean hideWorkPhone;
    private boolean hideMobilePhone;
    private boolean protectedIdentity;


    public ContactDetail(String workEmail, String privateEmail, String workPhone, String landLinePhone, String privatePhone, String mobilePhone, String facebookAccount, String twitterAccount) {
        this.workEmail = workEmail;
        this.privateEmail = privateEmail;
        this.workPhone = workPhone;
        this.landLinePhone = landLinePhone;
        this.privatePhone = privatePhone;
        this.mobilePhone = mobilePhone;
        this.facebookAccount = facebookAccount;
        this.twitterAccount = twitterAccount;
    }

    public ContactDetail(String workEmail, String privateEmail, String privatePhone, String facebookAccount) {
        this.workEmail = workEmail;
        this.privateEmail = privateEmail;
        this.privatePhone = privatePhone;
        this.facebookAccount = facebookAccount;
    }


}

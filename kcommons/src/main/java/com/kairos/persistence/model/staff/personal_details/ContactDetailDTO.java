package com.kairos.persistence.model.staff.personal_details;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDetailDTO {


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
    private String emergencyPhone;
    private boolean hideEmergencyPhone;
}

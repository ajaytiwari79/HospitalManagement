package com.kairos.dto.user.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By G.P.Ranjan on 3/9/19
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCalenderTokenDTO {
    private String googleCalenderTokenId;
    private String googleCalenderAccessToken;
}

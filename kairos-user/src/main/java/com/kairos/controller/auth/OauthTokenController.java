package com.kairos.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class OauthTokenController {

    private static final String BEARER_AUTHENTICATION = "bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Autowired
    private TokenStore tokenStore;

    @RequestMapping(method = RequestMethod.DELETE, value = "/oauth/logout")
    @ResponseBody
    public void revokeToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZATION);
        Optional<String> authorizationToken= Optional.ofNullable(token);
        authorizationToken.filter(s->s.contains(BEARER_AUTHENTICATION)).ifPresent(s->{
            String tokenId =token.split(" ")[1];
            //getToken
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(tokenId);
                 System.out.print("accessToken "+oAuth2AccessToken);
                     Optional.ofNullable(oAuth2AccessToken).ifPresent(oAuth2AccessToken1 -> {
                          //getting refresh token
                           OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken1.getRefreshToken();

                           Optional.ofNullable(oAuth2RefreshToken).ifPresent(oAuth2RefreshToken1 ->  tokenStore.removeRefreshToken(oAuth2RefreshToken));
                         System.out.print("oAuth2RefreshToken "+oAuth2RefreshToken);
                       tokenStore.removeAccessToken(oAuth2AccessToken);

            });

        });
    }

}

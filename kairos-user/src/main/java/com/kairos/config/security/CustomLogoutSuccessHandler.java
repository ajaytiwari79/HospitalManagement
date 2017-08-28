package com.kairos.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
@Component
public class CustomLogoutSuccessHandler implements LogoutHandler {
    @Resource(name = "tokenServices")
    ConsumerTokenServices tokenServices;
   @Override
   public void logout(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication){
       Optional<String>authorization=Optional.ofNullable(request.getHeader("Authorization"));
       authorization.filter(s->s.contains("Bearer")).ifPresent(s->{
           String tokenId = s.substring("Bearer".length()+1);
           tokenServices.revokeToken(tokenId);

       });

       }

    }


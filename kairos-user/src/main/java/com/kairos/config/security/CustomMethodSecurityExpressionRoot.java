package com.kairos.config.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Set;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {

        super(authentication);

    }

    //
    public boolean hasPermission(Long tabId) {
        OAuth2Authentication oAuth2Authentication= (OAuth2Authentication)this.authentication;
        Set<String> authorities= AuthorityUtils.authorityListToSet(oAuth2Authentication.getUserAuthentication().getAuthorities());
        return authorities.contains(tabId.toString());
    }

    //

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }

}


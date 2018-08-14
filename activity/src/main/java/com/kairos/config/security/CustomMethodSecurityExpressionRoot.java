package com.kairos.config.security;

import com.kairos.util.user_context.CurrentUserDetails;
import com.kairos.util.user_context.UserContext;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {

        super(authentication);

    }

    //
    public boolean hasPermission() {
        OAuth2Authentication oAuth2Authentication= (OAuth2Authentication)this.authentication;
        Set<String> authorities= AuthorityUtils.authorityListToSet(oAuth2Authentication.getUserAuthentication().getAuthorities());
        CurrentUserDetails currentUserDetails= UserContext.getUserDetails();
        Long organizationId = extractIdOrgIdFromUrl();
        Assert.notNull(currentUserDetails,"User can not be null");
        Assert.notNull(UserContext.getTabId(),"Tab id can not be null");
        Assert.notNull(organizationId,"Organization id can not be null");
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        String permissionString;

        if("GET".equals(req.getMethod())){
            permissionString = organizationId + "_" + UserContext.getTabId() + "_" + "r";
        } else {
            permissionString = organizationId + "_" + UserContext.getTabId() + "_" + "rw";
        }
        Optional<String> permission = authorities.stream().filter(s -> s.startsWith(permissionString)).findFirst();

        return permission.isPresent();
    }

    private Long extractIdOrgIdFromUrl(){


        if(Optional.ofNullable(UserContext.getUnitId()).isPresent()){
            return UserContext.getUnitId();
        } else if(Optional.ofNullable(UserContext.getOrgId()).isPresent()){
            return UserContext.getOrgId();
        }else {
            return null;
        }
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


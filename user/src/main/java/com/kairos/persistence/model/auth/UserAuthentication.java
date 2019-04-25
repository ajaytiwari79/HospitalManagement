package com.kairos.persistence.model.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Created by prabjot on 1/5/17.
 */
public class UserAuthentication implements Authentication {

    private static final long serialVersionUID = 1L;

    private final User user;
    private boolean authenticated = true;

    public UserAuthentication(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return user;
    }

    @Override
    public Object getPrincipal() {
        return user.getUserName();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        //this is overridden method
    }

    @Override
    public String getName() {
        return null;
    }

    public static User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }
}

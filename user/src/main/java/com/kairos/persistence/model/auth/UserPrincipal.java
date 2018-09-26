package com.kairos.persistence.model.auth;

import com.kairos.config.security.CurrentUserDetails;
import com.kairos.persistence.model.country.Country;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class UserPrincipal implements UserDetails, Authentication {
    private static final long serialVersionUID = 1L;
    private final List<GrantedAuthority> authorities ;
    private final User user;
    private boolean authenticated = true;

    //

    public UserPrincipal(User user, List<GrantedAuthority> authorities) {
        this.authorities = authorities;
        this.user = user;
    }

    //

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }

    @Override
    public Object getDetails() {

        CurrentUserDetails details = new CurrentUserDetails(this.user.getId(), this.user.getUserName(), this.user.nickName,
                this.user.firstName, this.user.getLastName(), this.user.getEmail(), this.user.isPasswordUpdated());
        details.setAge(this.user.getAge());
        Optional<Country> country = Optional.ofNullable(this.user.getCountryList()).map(countryList -> countryList.get(0));
        country.ifPresent(c -> details.setCountryId(c.getId()));
        return details;
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
    public String getName() {
        return user.getUserName();
    }

    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;

    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "authorities=" + authorities +
                ", user=" + user +
                ", authenticated=" + authenticated +
                '}';
    }
}

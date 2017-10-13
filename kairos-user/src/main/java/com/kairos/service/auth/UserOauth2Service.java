package com.kairos.service.auth;

import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.auth.UserPrincipal;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.util.HttpRequestHolder;
import com.kairos.util.OptionalUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserOauth2Service implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(UserOauth2Service.class);
    @Autowired
    private UserGraphRepository userGraphRepository;
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         User user=  userGraphRepository.findByUserNameIgnoreCase(username.toLowerCase());
         user.setHubMember(userService.isHubMember(user.getId()));
         Optional<User> loggedUser=Optional.ofNullable(user);
         String otpString=HttpRequestHolder.getCurrentRequest().getParameter("verificationCode");
         Optional<Integer>optInt=OptionalUtility.stringToInt(otpString);

        if (loggedUser.filter(u->optInt.get().equals(u.getOtp())).isPresent()) {
            logger.info("user opt match{}",user.getOtp());
            return new UserPrincipal(user,getPermission(user));
        }else{
            // Not found...
            throw new UsernameNotFoundException(
                    "User " + username + " not found.");
        }
    }

    private List<GrantedAuthority> getPermission(User user){
       List<GrantedAuthority> permissions = userService.getTabPermission(user.getId());
        return permissions;
    }
}

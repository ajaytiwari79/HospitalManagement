package com.kairos.service.auth;

import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.auth.UserPrincipal;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.HttpRequestHolder;
import com.kairos.utils.OptionalUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserOauth2Service implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(UserOauth2Service.class);
    @Autowired
    private UserGraphRepository userGraphRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AccessPageService accessPageService;
    @Inject
    private ExceptionService exceptionService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         User user=  userGraphRepository.findByEmail("(?i)"+username.toLowerCase());
         user.setHubMember(accessPageService.isHubMember(user.getId()));
         Optional<User> loggedUser=Optional.ofNullable(user);
         String otpString=HttpRequestHolder.getCurrentRequest().getParameter("verificationCode");
         Optional<Integer>optInt=OptionalUtility.stringToInt(otpString);

        if (loggedUser.filter(u->optInt.get().equals(u.getOtp())).isPresent()) {
            logger.info("user opt match{}",user.getOtp());
            return new UserPrincipal(user,getPermission(user));
        }else{
            // Not found...
            exceptionService.usernameNotFoundException("message.user.userName.notFound",username);
        }
       return  null;
    }

    private List<GrantedAuthority> getPermission(User user){
        // TODO As discussed with Arvind Das, We dont need to append tab permissions in AuthToken. 
       List<GrantedAuthority> permissions = Collections.emptyList();//userService.getTabPermission(user.getId());
        return permissions;
    }
}

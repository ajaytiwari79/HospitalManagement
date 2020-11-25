package com.kairos.service.auth;

import com.kairos.config.env.EnvConfig;
import com.kairos.enums.user.UserType;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.auth.UserPrincipal;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.HttpRequestHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_USER_USERNAME_NOTFOUND;

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
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject private StaffGraphRepository staffGraphRepository;
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;
    @Inject
    private EnvConfig envConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userGraphRepository.findUserByUserName("(?i)" + username);
        if (!Optional.ofNullable(user).isPresent()) {
            exceptionService.usernameNotFoundException(MESSAGE_USER_USERNAME_NOTFOUND, username);
        }
        user.setHubMember(accessPageService.isHubMember(user.getId()));
        user.setSystemAdmin(userGraphRepository.isSystemAdmin(user.getId()));
        SystemLanguage systemLanguage = userGraphRepository.getUserSystemLanguage(user.getId());
        if(isNull(systemLanguage)){
            systemLanguage = new SystemLanguage("English","en",true,true);
        }
        user.setUserLanguage(systemLanguage);
        updateLastSelectedOrganization(user);
        Optional<User> loggedUser = Optional.ofNullable(user);
        String otpString = HttpRequestHolder.getCurrentRequest().getParameter("verificationCode");
        String password = HttpRequestHolder.getCurrentRequest().getParameter("password");
        if (passwordEncoder.matches(password, user.getPassword()) && user.getUserType().toString().
                equals(UserType.SYSTEM_ACCOUNT.toString())) {
            return new UserPrincipal(user, getPermission(user));
        }
        if(isNotNull(user.getLastSelectedOrganizationId())){
            Staff staff = staffGraphRepository.getByUser(user.getId());
            if(isNotNull(staff)){
                user.setProfilePic(envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + staff.getProfilePic());
            }
        }
        //Todo please uncomment the code when threefactor authentication enabled
        /*Optional<Integer> optInt = OptionalUtility.stringToInt(otpString);
        if (loggedUser.filter(u -> optInt.get().equals(u.getOtp())).isPresent()) {
            logger.info("user opt match{}", user.getOtp());
            return new UserPrincipal(user, getPermission(user));
        } else {
            // Not found...
            exceptionService.usernameNotFoundException(MESSAGE_USER_USERNAME_NOTFOUND, username);
        }*/
        return new UserPrincipal(user, getPermission(user));
    }

    private void updateLastSelectedOrganization(User user) {
        if(isNull(user.getLastSelectedOrganizationId())){
            Long lastSelectedOrgId=userGraphRepository.getLastSelectedOrganizationId(user.getId());
            user.setLastSelectedOrganizationId(lastSelectedOrgId);
            userGraphRepository.save(user);
        }
    }

    private List<GrantedAuthority> getPermission(User user) {
        return Collections.emptyList();
    }
}

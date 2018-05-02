package com.kairos.service;

import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.user_personalized_settings.SelfRosteringView;
import com.kairos.persistence.model.user.user_personalized_settings.UserPersonalizedSettings;
import com.kairos.persistence.model.user.user_personalized_settings.UserPersonalizedSettingsDto;
import com.kairos.persistence.repository.user.UserPersonalizedSettingsRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by yatharth on 1/5/18.
 */
@Service
public class UserPersonalizedSettingsService extends UserBaseService{
    @Inject
    private UserPersonalizedSettingsRepository userPersonalizedSettingsRepository;

    @Inject
    private UserGraphRepository userGraphRepository;


    public UserPersonalizedSettings getAllSettingsByUser(Long userId) {

        UserPersonalizedSettings userPersonalizedSettings =  userPersonalizedSettingsRepository.findByUser(userId);
        return userPersonalizedSettings;
    }


    public UserPersonalizedSettings updateUserPersonalizedSettings(Long userId, UserPersonalizedSettingsDto userPersonalizedSettingsDto) {

        UserPersonalizedSettings userPersonalizedSettings = userPersonalizedSettingsRepository.findByUser(userId);
        if(Optional.ofNullable(userPersonalizedSettings).isPresent()) {
            BeanUtils.copyProperties(userPersonalizedSettingsDto,userPersonalizedSettings);
            save(userPersonalizedSettings);

        }
        else {
            userPersonalizedSettings = new UserPersonalizedSettings( new SelfRosteringView(userPersonalizedSettingsDto.getSelfRosteringView().getAbsenceViewSettings()));
            User user = userGraphRepository.findOne(userId);
            user.setUserPersonalizedSettings(userPersonalizedSettings);
            save(user);
        }
        return userPersonalizedSettings;
    }
}

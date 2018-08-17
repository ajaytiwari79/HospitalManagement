package com.kairos.service;

import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.user_personalized_settings.*;
import com.kairos.persistence.repository.user.UserPersonalizedSettingsRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.user.user.user_personalized_settings.SelfRosteringViewDto;
import com.kairos.user.user.user_personalized_settings.UserPersonalizedSettingsDto;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by yatharth on 1/5/18.
 */
@Service
public class UserPersonalizedSettingsService{
    @Inject
    private UserPersonalizedSettingsRepository userPersonalizedSettingsRepository;

    @Inject
    private UserGraphRepository userGraphRepository;


    public UserPersonalizedSettingsDto getAllSettingsByUser(Long userId) {

        UserPersonalizedSettingsQueryResult userPersonalizedSettingsQueryResult =  userPersonalizedSettingsRepository.findAllByUser(userId);
        UserPersonalizedSettingsDto userPersonalizedSettingsDto;
        if(Optional.ofNullable(userPersonalizedSettingsQueryResult).isPresent()) {
            userPersonalizedSettingsDto = new UserPersonalizedSettingsDto( new SelfRosteringViewDto(userPersonalizedSettingsQueryResult.getSelfRosteringView().getAbsenceViewSettings()));

        }
        else {
            userPersonalizedSettingsDto = new UserPersonalizedSettingsDto( new SelfRosteringViewDto());
        }
        return userPersonalizedSettingsDto;
    }


    public UserPersonalizedSettings updateUserPersonalizedSettings(Long userId, UserPersonalizedSettingsDto userPersonalizedSettingsDto) {

        UserPersonalizedSettingsQueryResult userPersonalizedSettingsQueryResult =  userPersonalizedSettingsRepository.findAllByUser(userId);
        UserPersonalizedSettings userPersonalizedSettings;
        if(Optional.ofNullable(userPersonalizedSettingsQueryResult).isPresent()) {
            userPersonalizedSettings = userPersonalizedSettingsQueryResult.getUserPersonalizedSettings();
            userPersonalizedSettings.setSelfRosteringView(userPersonalizedSettingsQueryResult.getSelfRosteringView());
           userPersonalizedSettings.getSelfRosteringView().setAbsenceViewSettings(userPersonalizedSettingsDto.getSelfRosteringView().getAbsenceViewSettings());
            userPersonalizedSettingsRepository.save(userPersonalizedSettings);

        }
        else {
            userPersonalizedSettings = new UserPersonalizedSettings( new SelfRosteringView(userPersonalizedSettingsDto.getSelfRosteringView().getAbsenceViewSettings()));
            User user = userGraphRepository.findOne(userId);
            user.setUserPersonalizedSettings(userPersonalizedSettings);
            userGraphRepository.save(user);
        }
        return userPersonalizedSettings;
    }
}

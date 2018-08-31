package com.kairos.service.staff;

import com.kairos.persistence.model.staff.StaffTimezoneQueryResult;
import com.kairos.persistence.repository.organization.default_data.SickConfigurationRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import com.kairos.service.organization_meta_data.SickConfigurationService;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * CreatedBy vipulpandey on 30/8/18
 **/
@Service
@Transactional
public class UserSickService {
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private SickConfigurationService sickConfigurationService;

    public List<StaffResultDTO> getStaffAndUnitSickSettings(Long UserId) {
        List<StaffTimezoneQueryResult> staffUnitWrappers = staffGraphRepository.getAllStaffsAndUnitDetailsByUserId(UserId);
        List<StaffResultDTO> staffResults;
        if (Optional.ofNullable(staffUnitWrappers).isPresent()) {
            staffResults = ObjectMapperUtils.copyPropertiesOfListByMapper(staffUnitWrappers, StaffResultDTO.class);
            // if the size id one then also calculating allowed time type for sick
            if (staffUnitWrappers.size() == 1) {
                Set<BigInteger> allowedTimeTypes = sickConfigurationService.getSickSettingsOfUnit(staffUnitWrappers.get(0).getUnitId());
                staffResults.get(0).setAllowedTimeTypesForSick(allowedTimeTypes);
            }
        } else {
            staffResults = new ArrayList<>();
        }
        return staffResults;

    }

}

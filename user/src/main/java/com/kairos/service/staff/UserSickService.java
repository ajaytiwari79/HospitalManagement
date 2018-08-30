package com.kairos.service.staff;

import com.kairos.persistence.model.staff.StaffTimezoneQueryResult;
import com.kairos.persistence.repository.organization.default_data.SickConfigurationRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CreatedBy vipulpandey on 30/8/18
 **/
@Service
@Transactional
public class UserSickService {
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    SickConfigurationRepository sickConfigurationRepository;
    public List<StaffResultDTO> getStaffAndUnitSickSettings(Long UserId) {
        List<StaffTimezoneQueryResult> staffUnitWrappers = staffGraphRepository.getAllStaffsAndUnitDetailsByUserId(UserId);
        List<StaffResultDTO> staffResults;
        if (Optional.ofNullable(staffUnitWrappers).isPresent()){
            staffResults= ObjectMapperUtils.copyPropertiesOfListByMapper(staffUnitWrappers, StaffResultDTO.class);
            if (staffUnitWrappers.size()==1){


            }
        }else {
            staffResults= new ArrayList<>();
        }
        return staffResults;

    }

}

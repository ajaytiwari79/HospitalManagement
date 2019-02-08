package com.kairos.service;

import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.persistence.model.CustomTimeScale;
import com.kairos.persistence.repository.CustomTimeScaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by oodles on 17/4/17.
 */
@Service
public class CustomTimeScaleService extends MongoBaseService {

    @Inject
    CustomTimeScaleRepository customTimeScaleRepository;

    @Autowired
    GenericIntegrationService genericIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(CustomTimeScaleService.class);


    public  CustomTimeScale createCitizenTimeScale(Long staffId,Long citizenId,Long unitId, int numberOfAdditionalScales){

        return save(new CustomTimeScale(staffId, citizenId, unitId, numberOfAdditionalScales));

    }

    public  CustomTimeScale updateCitizenTimeScale(Long citizenId,Long unitId,Map<String, Object> payload){

        //StaffDTO staff = staffRestClient.getStaff(UserContext.getUserDetails().getId());
        ClientStaffInfoDTO clientStaffInfoDTO = genericIntegrationService.getStaffInfo();

        int numberOfAdditionalScales = (int) payload.get("numberOfAdditionalScales");

        CustomTimeScale customTimeScale = customTimeScaleRepository.findByStaffIdAndCitizenIdAndUnitId(clientStaffInfoDTO.getStaffId(),citizenId,unitId);
        if(customTimeScale != null){
            customTimeScale.setNumberOfAdditionalScales(numberOfAdditionalScales);
            return save(customTimeScale);
        } else {
            return createCitizenTimeScale(clientStaffInfoDTO.getStaffId(),citizenId,unitId, numberOfAdditionalScales);
        }
    }

    public Integer getNumberOfAdditionalScales(Long staffId,Long citizenId,Long unitId){

        CustomTimeScale customTimeScale = customTimeScaleRepository.findByStaffIdAndCitizenIdAndUnitId(staffId,citizenId,unitId);
        //logger.debug("For Citizen "+citizenId+ " staffId, "+staffId+ " unitId, "+unitId+ " customTimeScale "+customTimeScale);
        if (customTimeScale != null)
            return customTimeScale.getNumberOfAdditionalScales();
        else
            return null;

    }

}

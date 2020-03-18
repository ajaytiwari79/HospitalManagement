package com.kairos.controller.external_citizen_import;

import com.kairos.commons.utils.DateUtils;
import com.kairos.service.external_citizen_import.AuthService;
import com.kairos.service.external_citizen_import.CitizenService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static com.kairos.constants.ApiConstants.API_EXTERNAL_CITIZEN_URL;

/**
 * Created by oodles on 18/4/17.
 */
@RestController
@RequestMapping(API_EXTERNAL_CITIZEN_URL)
@Api(API_EXTERNAL_CITIZEN_URL)
public class CitizenController {
    @Inject
    private AuthService authService;

    @Inject
    private CitizenService citizenService;

    private static final Logger logger = LoggerFactory.getLogger(CitizenController.class);

    /**
     * Get Client data from KMD Nexus
     *
     * @return
     * @params
     */
    @GetMapping(value = "/preferences/{unitId}")
    public String preferences(@PathVariable long unitId)  {
        authService.kmdAuth();
        logger.info("Importing Citizen from KMD Nexus !!!!----> " );
        return citizenService.getCitizensFromKMD(unitId);
    }

    /**
     * Get Citizen Grants data from KMD Nexus
     *
     * @return
     * @params
     */
   @GetMapping(value = "/grants")
    public String getCitizenGrants(){
        logger.info("Start syncing grants---------> {}",DateUtils.getCurrentDate());
        authService.kmdAuth();
        citizenService.getCitizenGrantsFromKMD();
        logger.info("End syncing grants---------> {}",DateUtils.getCurrentDate());
        return "Citizen Grants Sync";
    }

    /**
     * Get Citizen Relative data from KMD Nexus
     *
     * @return
     * @params
     */
   @GetMapping(value = "/nextToKin")
    public String getCitizensRelativeData(){
        authService.kmdAuth();
        citizenService.getCitizensRelativeContact();
        return "Citizen Relative Data Sync";
    }


    /**
     *
     * @param filterId
     * @param unitId
     * @return
     */
   @GetMapping(value = "/unit/{unitId}/getShifts/{filterId}")
    public String getShifts(@PathVariable Long filterId, @PathVariable Long unitId){
        authService.kmdAuth();
        citizenService.getShifts(filterId, unitId);
        return "Citizen Relative Data Sync";
    }

    /**
     *
     * @param unitId
     * @return
     */
   @GetMapping(value = "/unit/{unitId}/getTimeSlots")
    public String getTimeSlots( @PathVariable Long unitId){
        authService.kmdAuth();
        citizenService.getTimeSlots( unitId);
        return "KMD Time slot Data Sync";
    }

}

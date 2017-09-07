package com.kairos.controller.kmdNexus;

import com.kairos.service.kmdNexus.AuthService;
import com.kairos.service.kmdNexus.CitizenService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Date;

import static com.kairos.constants.ApiConstants.API_KMD_NEXUS_CITIZEN_URL;

/**
 * Created by oodles on 18/4/17.
 */
@RestController
@RequestMapping(API_KMD_NEXUS_CITIZEN_URL)
@Api(API_KMD_NEXUS_CITIZEN_URL)
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
    @RequestMapping(value = "/preferences/{unitId}", method = RequestMethod.GET)
    public String preferences(@PathVariable long unitId)  {
        authService.kmdAuth();
        logger.info("Importing Citizen from KMD Nexus !!!!----> " );
        String status = citizenService.getCitizensFromKMD(unitId);
        return status;
    }

    /**
     * Get Citizen Grants data from KMD Nexus
     *
     * @return
     * @params
     */
    @RequestMapping(value = "/grants", method = RequestMethod.GET)
    public String getCitizenGrants(){
        logger.info("Start syncing grants---------> "+new Date());
        authService.kmdAuth();
        citizenService.getCitizenGrantsFromKMD();
        logger.info("End syncing grants---------> "+new Date());
        return "Citizen Grants Sync";
    }

    /**
     * Get Citizen Relative data from KMD Nexus
     *
     * @return
     * @params
     */
    @RequestMapping(value = "/nextToKin", method = RequestMethod.GET)
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
    @RequestMapping(value = "/unit/{unitId}/getShifts/{filterId}", method = RequestMethod.GET)
    public String getShifts(@PathVariable Long filterId, @PathVariable Long unitId){
        authService.kmdAuth();
        citizenService.getShifts(filterId, unitId);
        return "Citizen Relative Data Sync";
    }

}

package com.kairos.service.integration;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kairos.persistence.model.user.integration.TimeCare;
import com.kairos.persistence.model.user.integration.Twillio;
import com.kairos.persistence.model.user.integration.Visitour;
import com.kairos.persistence.repository.user.integration.TimeCareGraphRepository;
import com.kairos.persistence.repository.user.integration.TwillioGraphRepository;
import com.kairos.persistence.repository.user.integration.VisitourGraphRepository;

/**
 * Created by oodles on 21/2/17.
 */
@Transactional
@Service
public class IntegrationService {

    @Inject
    private TimeCareGraphRepository timeCareGraphRepository;

    @Inject
    private TwillioGraphRepository twillioGraphRepository;

    @Inject
    private VisitourGraphRepository visitourGraphRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TimeCare saveTimeCareIntegrationData(Long unitId, TimeCare timeCare){
        logger.info("timecare------> "+timeCare.getIntegrationId());
            TimeCare timeCare1 = timeCareGraphRepository.findByOrganizationId(unitId);
            if(timeCare1 == null) timeCare1 = new TimeCare();
            timeCare1.setIntegrationId(timeCare.getIntegrationId());
            timeCare1.setOrganizationId(unitId);
            timeCareGraphRepository.save(timeCare1);
            return timeCare1;
    }

    public TimeCare fetchTimeCareIntegrationData(Long unitId){
        TimeCare timeCare = timeCareGraphRepository.findByOrganizationId(unitId);

        return timeCare;
    }

    public Twillio saveTwillioIntegrationData (Long unitId, Twillio twillio){
       try {
           logger.info("twillio----account--> "+twillio.getAccountId());
           Twillio twillio1 = twillioGraphRepository.findByOrganizationId(unitId);
           if (twillio1 == null) twillio1 = new Twillio();
           twillio1 = (Twillio) twillio.clone();
           twillio1.setOrganizationId(unitId);
           twillioGraphRepository.save(twillio1);
           return twillio1;
       }catch (CloneNotSupportedException exception){
           return null;
       }

    }

    public Twillio fetchTwillioIntegrationData(Long unitId){
        Twillio twillio = twillioGraphRepository.findByOrganizationId(unitId);

        return twillio;
    }

    public Visitour saveVisitourIntegrationData(Long unitId, Visitour visitour){
        try {
            Visitour visitour1 = visitourGraphRepository.findByOrganizationId(unitId);
            if (visitour1 == null) visitour1 = new Visitour();
            visitour1 = (Visitour) visitour.clone();
            visitour1.setOrganizationId(unitId);
            visitourGraphRepository.save(visitour1);
            return visitour1;
        }catch (CloneNotSupportedException exception){
            return null;
        }
    }

    public Visitour fetchVisitourIntegrationData(Long unitId){
        Visitour visitour = visitourGraphRepository.findByOrganizationId(unitId);

        return visitour;
    }

    public Map<String, String> getFLS_Credentials(long organizationId){
        Visitour visitour = visitourGraphRepository.findByOrganizationId(organizationId);
        Map<String, String> credentials = new HashMap<>();
        String url = (visitour != null) ? visitour.getServerName(): "";
        String userPass = (visitour != null)?visitour.getUsername()+":"+visitour.getPassword():":";
        credentials.put("flsDefaultUrl",url);
        credentials.put("userpassword",userPass);
        return credentials;
    }
}

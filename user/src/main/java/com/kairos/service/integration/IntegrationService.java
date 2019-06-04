package com.kairos.service.integration;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.integration.TimeCare;
import com.kairos.persistence.model.user.integration.Twillio;
import com.kairos.persistence.model.user.integration.Visitour;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.integration.TimeCareGraphRepository;
import com.kairos.persistence.repository.user.integration.TwillioGraphRepository;
import com.kairos.persistence.repository.user.integration.VisitourGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.kairos.constants.UserMessagesConstants.ERROR_INTEGRATIONSERVICE_DATA_ISEMPTY;

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

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private ExceptionService exceptionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TimeCare saveTimeCareIntegrationData(Long unitId, TimeCare timeCare){
        logger.info("timecare------> "+timeCare.getIntegrationId());
            TimeCare timeCare1 = timeCareGraphRepository.findByOrganizationId(unitId);
            if(timeCare1 == null) timeCare1 = new TimeCare();
            timeCare1.setIntegrationId(timeCare.getIntegrationId());
            timeCare1.setTimeCareExternalId(timeCare.getTimeCareExternalId());
            timeCare1.setOrganizationId(unitId);

            timeCareGraphRepository.save(timeCare1);

            // TODO Need to remove external ID field from organization domain
            // Set organization's time care ID
            Organization organization = organizationGraphRepository.findOne(unitId);
            organization.setExternalId(timeCare.getTimeCareExternalId());
            organizationGraphRepository.save(organization);

            return timeCare1;
    }

    public TimeCare fetchTimeCareIntegrationData(Long unitId){
        TimeCare timeCare = timeCareGraphRepository.findByOrganizationId(unitId);

        return timeCare;
    }

    public Twillio saveTwillioIntegrationData (Long unitId, Twillio twillio){
        logger.info("twillio----account--> "+twillio.getAccountId());
        Twillio twillio1 = twillioGraphRepository.findByOrganizationId(unitId);
        if (twillio1 == null) twillio1 = new Twillio();
        twillio1 = Twillio.copyProperties(twillio,Twillio.getInstance());
        twillio1.setOrganizationId(unitId);
        twillioGraphRepository.save(twillio1);
        return twillio1;
    }

    public Twillio fetchTwillioIntegrationData(Long unitId){
        Twillio twillio = twillioGraphRepository.findByOrganizationId(unitId);

        return twillio;
    }

    public Visitour saveVisitourIntegrationData(Long unitId, Visitour visitour){
        if( ! Optional.ofNullable(visitour.getUsername()).isPresent() ||
                ! Optional.ofNullable(visitour.getServerName()).isPresent() ||
                ! Optional.ofNullable(visitour.getPassword()).isPresent()){
            exceptionService.internalServerError(ERROR_INTEGRATIONSERVICE_DATA_ISEMPTY);
            
        }

        Visitour visitourCredential = visitourGraphRepository.findByOrganizationId(unitId);
        if(! Optional.ofNullable(visitourCredential).isPresent()){
            visitourCredential = Visitour.getInstance();
        }

        visitourCredential.setServerName(visitour.getServerName());
        visitourCredential.setUsername(visitour.getUsername());
        visitourCredential.setPassword(visitour.getPassword());
        visitourCredential.setOrganizationId(unitId);
        visitourGraphRepository.save(visitourCredential);
        return visitourCredential;
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

    public ConcurrentMap<Long, ConcurrentMap<String,String>> getFLSCredentials(List<Long> unitIds){
        ConcurrentMap<Long,ConcurrentMap<String,String>> flsCredentials = new ConcurrentHashMap<>();
        unitIds.forEach(unitId->{
            ConcurrentMap<String, String> credentailsForUnit = new ConcurrentHashMap<>(getFLS_Credentials(unitId));
            flsCredentials.put(unitId,credentailsForUnit);
        });
        return flsCredentials;
    }
}

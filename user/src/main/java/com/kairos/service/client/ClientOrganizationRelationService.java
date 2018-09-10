package com.kairos.service.client;
import com.kairos.persistence.model.client.relationships.ClientOrganizationRelation;
import com.kairos.persistence.repository.user.client.ClientOrganizationRelationGraphRepository;
import com.kairos.utils.BadgeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by oodles on 15/11/16.
 */
@Service
@Transactional
public class ClientOrganizationRelationService {

    @Inject
    ClientOrganizationRelationGraphRepository repository;

    public ClientOrganizationRelation createRelation(ClientOrganizationRelation clientOrganizationRelation){
        if (clientOrganizationRelation.getEmploymentId()==null){
            clientOrganizationRelation.setEmploymentId(BadgeGenerator.generateBadgeNumber());
        }
      return repository.save(clientOrganizationRelation);
    }

    public int checkClientOrganizationRelation(Long clientId, Long organizationId) {
        return repository.checkClientOrganizationRelationship(clientId,organizationId);
    }
}

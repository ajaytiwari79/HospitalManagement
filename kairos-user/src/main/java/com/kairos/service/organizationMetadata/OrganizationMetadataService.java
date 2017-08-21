package com.kairos.service.organizationMetadata;
import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.region.LocalAreaTag;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationMetadataRepository;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neuron on 12/6/17.
 */
@Service
@Transactional
public class OrganizationMetadataService extends UserBaseService {


    @Inject
    OrganizationMetadataRepository organizationMetadataRepository;

    @Inject
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private EnvConfig envConfig;


    private static final Logger logger = LoggerFactory.getLogger(OrganizationMetadataService.class);


    public Map<String, Object> findAllLocalAreaTags(long unitId) {
        Map<String, Object> localAreaTagData = new HashMap<String, Object>();
        List<Object> clientList = new ArrayList<>();
        List<Object> localAreaTagsList = new ArrayList<>();
        List<Map<String, Object>> mapList = organizationGraphRepository.getClientsOfOrganization(unitId,envConfig.getServerHost() + File.separator);
        for (Map<String, Object> map : mapList) {
            clientList.add(map.get("Client"));
        }
        localAreaTagData.put("citizenList", clientList);
        List<Map<String, Object>> tagList = organizationMetadataRepository.findAllByIsDeletedAndUnitId(unitId);
        for (Map<String, Object> map : tagList) {
            localAreaTagsList.add(map.get("tags"));
        }
        localAreaTagData.put("localAreaTags", localAreaTagsList);
        return localAreaTagData;
    }

    public LocalAreaTag createNew(LocalAreaTag localAreaTag, long unitId) {
        logger.info("local area tag is" + localAreaTag.toString());
        Organization organization = organizationGraphRepository.findOne(unitId);


        if (organization != null) {

            List<LocalAreaTag> localAreaTagList = organization.getLocalAreaTags();
            LocalAreaTag areaTag = new LocalAreaTag();
            areaTag.setName(localAreaTag.getName());
            areaTag.setPaths(localAreaTag.getPaths());
           // areaTag.setColor(localAreaTag.getColor());
            organizationMetadataRepository.save(areaTag);
            localAreaTagList.add(areaTag);
            organization.setLocalAreaTags(localAreaTagList);
            logger.debug("organization.getLocalAreaTags  " + organization.getLocalAreaTags());
            organizationGraphRepository.save(organization);
            return areaTag;
        } else {
            return null;
        }
    }


    public LocalAreaTag updateTagData(LocalAreaTag localAreaTag) {
        LocalAreaTag localAreaTag1 = organizationMetadataRepository.findOne(localAreaTag.getId());
        localAreaTag1.setPaths(localAreaTag.getPaths());
        localAreaTag1.setName(localAreaTag.getName());
       // localAreaTag1.setColor(localAreaTag.getColor());
        return save(localAreaTag1);
    }

    public boolean deleteTagData(Long localAreaTagId) {
        LocalAreaTag localAreaTag = organizationMetadataRepository.findOne(localAreaTagId);
        localAreaTag.setDeleted(true);
        organizationMetadataRepository.save(localAreaTag);
        if (localAreaTag.isDeleted()) {
            return true;
        } else {
            return false;
        }
    }


}

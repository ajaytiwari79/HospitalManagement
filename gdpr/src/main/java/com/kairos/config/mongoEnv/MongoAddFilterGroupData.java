package com.kairos.config.mongoEnv;


import com.kairos.dto.gdpr.master_data.ModuleIdDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.filter.FilterGroup;
import com.kairos.persistence.repository.filter.FilterMongoRepository;
import com.kairos.service.common.MongoBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static com.kairos.constants.AppConstant.COUNTRY_ID;
import static com.kairos.constants.AppConstant.CLAUSE_MODULE_NAME;
import static com.kairos.constants.AppConstant.ASSET_MODULE_NAME;
import static com.kairos.constants.AppConstant.MASTER_PROCESSING_ACTIVITY_MODULE_NAME;
import static com.kairos.constants.AppConstant.CLAUSE_MODULE_ID;
import static com.kairos.constants.AppConstant.ASSET_MODULE_ID;
import static com.kairos.constants.AppConstant.MASTER_PROCESSING_ACTIVITY_MODULE_ID;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class MongoAddFilterGroupData extends MongoBaseService implements CommandLineRunner {


    Logger LOGGER = LoggerFactory.getLogger(MongoAddFilterGroupData.class);
    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private FilterMongoRepository filterMongoRepository;

    @Override
    public void run(String... args) {

        LOGGER.info("creating filter group data");

        Query clauseQuery = new Query(Criteria.where(COUNTRY_ID).is(4L).and("accessModule.active").is(true).and("accessModule.moduleId").is(CLAUSE_MODULE_ID));
        Query assetQuery = new Query(Criteria.where(COUNTRY_ID).is(4L).and("accessModule.active").is(true).and("accessModule.moduleId").is(ASSET_MODULE_ID));
        Query processingActivityQuery = new Query(Criteria.where(COUNTRY_ID).is(4L).and("accessModule.active").is(true).and("accessModule.moduleId").is(MASTER_PROCESSING_ACTIVITY_MODULE_ID));
        FilterGroup clauseFilterGroup = mongoTemplate.findOne(clauseQuery, FilterGroup.class);
        FilterGroup assetFilterGroup = mongoTemplate.findOne(assetQuery, FilterGroup.class);
        FilterGroup processingActivityFilterGroup = mongoTemplate.findOne(processingActivityQuery, FilterGroup.class);
        List<FilterGroup> createFilterGroups = new ArrayList<>();

        if (clauseFilterGroup==null) {
            List<ModuleIdDTO> moduleIdDTOs = new ArrayList<>();
            ModuleIdDTO moduleIdDto = new ModuleIdDTO(CLAUSE_MODULE_NAME, CLAUSE_MODULE_ID, false, true);
            moduleIdDTOs.add(moduleIdDto);
            List<FilterType> filterTypes = new ArrayList<>();
            filterTypes.add(FilterType.ORGANIZATION_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SERVICES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_SERVICES);
            filterTypes.add(FilterType.ACCOUNT_TYPES);
            FilterGroup filterGroup = new FilterGroup(moduleIdDTOs, filterTypes, 4L);
            createFilterGroups.add(filterGroup);

        }
        if (assetFilterGroup==null ) {
            List<ModuleIdDTO> moduleIdDTOs = new ArrayList<>();
            ModuleIdDTO moduleIdDto = new ModuleIdDTO(ASSET_MODULE_NAME, ASSET_MODULE_ID, false, true);
            moduleIdDTOs.add(moduleIdDto);
            List<FilterType> filterTypes = new ArrayList<>();
            filterTypes.add(FilterType.ORGANIZATION_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SERVICES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_SERVICES);
            FilterGroup filterGroup = new FilterGroup(moduleIdDTOs, filterTypes, 4L);
            createFilterGroups.add(filterGroup);

        }
        if ( processingActivityFilterGroup==null) {
            List<ModuleIdDTO> moduleIdDtoList = new ArrayList<>();
            ModuleIdDTO moduleIdDto = new ModuleIdDTO(MASTER_PROCESSING_ACTIVITY_MODULE_NAME, MASTER_PROCESSING_ACTIVITY_MODULE_ID, false, true);
            moduleIdDtoList.add(moduleIdDto);
            List<FilterType> filterTypes = new ArrayList<>();
            filterTypes.add(FilterType.ORGANIZATION_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SERVICES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_SERVICES);
            FilterGroup filterGroup = new FilterGroup(moduleIdDtoList, filterTypes, 4L);
            createFilterGroups.add(filterGroup);

        }

        if (createFilterGroups.size() != 0) {
            filterMongoRepository.saveAll(getNextSequence(createFilterGroups));
        }
        LOGGER.info("Filter group save Successfully");


    }

    public MongoAddFilterGroupData() {


    }

}

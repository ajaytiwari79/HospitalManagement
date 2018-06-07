package com.kairos.config.mongoEnv_config;


import com.kairos.dto.ModuleIdDto;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.repository.filter.FilterMongoRepository;
import com.kairos.service.MongoBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static com.kairos.constant.AppConstant.COUNTRY_ID;
import static com.kairos.constant.AppConstant.CLAUSE_MODULE_NAME;
import static com.kairos.constant.AppConstant.ASSET_MODULE_NAME;
import static com.kairos.constant.AppConstant.MASTER_PROCESSING_ACTIVITY_MODULE_NAME;
import static com.kairos.constant.AppConstant.CLAUSE_MODULE_ID;
import static com.kairos.constant.AppConstant.ASSET_MODULE_ID;
import static com.kairos.constant.AppConstant.MASTER_PROCESSING_ACTIVITY_MODULE_ID;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MongoAddFilterGroupData extends MongoBaseService implements CommandLineRunner  {


    Logger LOGGER = LoggerFactory.getLogger(MongoAddFilterGroupData.class);
    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private FilterMongoRepository filterMongoRepository;

    @Override
    public void run(String... args) throws Exception {

        LOGGER.info("creating filter group data");

        Query clauseQquery = new Query(Criteria.where(COUNTRY_ID).is(4L).and("accessModule.active").is(true).and("accessModule.moduleId").is(CLAUSE_MODULE_ID));
        Query assetQuery = new Query(Criteria.where(COUNTRY_ID).is(4L).and("accessModule.active").is(true).and("accessModule.moduleId").is(ASSET_MODULE_ID));
        Query processingActivityQuery = new Query(Criteria.where(COUNTRY_ID).is(4L).and("accessModule.active").is(true).and("accessModule.moduleId").is(MASTER_PROCESSING_ACTIVITY_MODULE_ID));
        FilterGroup clauseFilterGroup = mongoTemplate.findOne(clauseQquery, FilterGroup.class);
        FilterGroup assetFilterGroup = mongoTemplate.findOne(assetQuery, FilterGroup.class);
        FilterGroup processingActivityFilterGroup = mongoTemplate.findOne(processingActivityQuery, FilterGroup.class);
        List<FilterGroup> createfilterGroups = new ArrayList<>();

        if (!Optional.ofNullable(clauseFilterGroup).isPresent()) {
            List<ModuleIdDto> moduleIdDtos = new ArrayList<>();
            ModuleIdDto moduleIdDto = new ModuleIdDto(CLAUSE_MODULE_NAME, CLAUSE_MODULE_ID, false, true);
            moduleIdDtos.add(moduleIdDto);
            List<FilterType> filterTypes = new ArrayList<FilterType>();
            filterTypes.add(FilterType.ORGANIZATION_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SERVICES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_SERVICES);
            filterTypes.add(FilterType.ACCOUNT_TYPES);
            FilterGroup filterGroup = new FilterGroup(moduleIdDtos, filterTypes, 4L);
            createfilterGroups.add(filterGroup);

        }

        if (!Optional.ofNullable(assetFilterGroup).isPresent()) {
            List<ModuleIdDto> moduleIdDtos = new ArrayList<>();
            ModuleIdDto moduleIdDto = new ModuleIdDto(ASSET_MODULE_NAME, ASSET_MODULE_ID, false, true);
            moduleIdDtos.add(moduleIdDto);
            List<FilterType> filterTypes = new ArrayList<FilterType>();
            filterTypes.add(FilterType.ORGANIZATION_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SERVICES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_SERVICES);
            FilterGroup filterGroup = new FilterGroup(moduleIdDtos, filterTypes, 4L);
            createfilterGroups.add(filterGroup);


        }

        if (!Optional.ofNullable(processingActivityFilterGroup).isPresent()) {
            List<ModuleIdDto> moduleIdDtos = new ArrayList<>();
            ModuleIdDto moduleIdDto = new ModuleIdDto(MASTER_PROCESSING_ACTIVITY_MODULE_NAME, MASTER_PROCESSING_ACTIVITY_MODULE_ID, false, true);
            moduleIdDtos.add(moduleIdDto);
            List<FilterType> filterTypes = new ArrayList<FilterType>();
            filterTypes.add(FilterType.ORGANIZATION_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_TYPES);
            filterTypes.add(FilterType.ORGANIZATION_SERVICES);
            filterTypes.add(FilterType.ORGANIZATION_SUB_SERVICES);
            FilterGroup filterGroup = new FilterGroup(moduleIdDtos, filterTypes, 4L);
            createfilterGroups.add(filterGroup);
        }

        if (createfilterGroups.size() != 0) {
            save(createfilterGroups);
        }

        LOGGER.info("Filter gorup save Succesfully");


    }

    public MongoAddFilterGroupData() {


    }

}
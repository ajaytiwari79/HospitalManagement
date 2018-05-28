package com.kairos.service.master_data_management.asset_management;


import com.kairos.persistance.repository.filter.FilterGroupMongoRepository;
import com.kairos.service.MongoBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class MasterAssetFilterService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetFilterService.class);

    @Inject
    private FilterGroupMongoRepository  filterGroupMongoRepository;

}

package com.kairos.service.master_data_management.asset_management;


import com.kairos.persistance.repository.filter.FilterGroupMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class MasterAssetFilterService extends MongoBaseService {


    @Inject
    private FilterGroupMongoRepository  filterGroupMongoRepository;

}

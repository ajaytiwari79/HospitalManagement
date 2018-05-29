package com.kairos.service.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.persistance.repository.master_data_management.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.filter.FilterQueryResult;
import com.kairos.service.MongoBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class MasterAssetFilterService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetFilterService.class);


    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;


    public FilterQueryResult getAllMasterAssetFilter(Long countryId) {
       return masterAssetMongoRepository.getMasterAssetFilter(countryId);

    }


    /*public List<MasterAsset> getFilteredMasterData(FilterQueryResult filterQueryResult)
    {






    }
*/







}

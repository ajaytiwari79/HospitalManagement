package com.kairos.service.asset;


import com.kairos.ExceptionHandler.DataNotFoundByIdException;
import com.kairos.ExceptionHandler.DuplicateDataException;
import com.kairos.ExceptionHandler.NotExists;
import com.kairos.ExceptionHandler.RequestDataNull;
import com.kairos.persistance.model.asset.GlobalAsset;
import com.kairos.persistance.repository.asset.GlobalAssetMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class GlobalAssetService extends MongoBaseService {


    @Inject
    private GlobalAssetMongoRepository globalAssetMongoRepository;


    public GlobalAsset addAsset(GlobalAsset globalAsset) {

        if (StringUtils.isEmpty(globalAsset.getDescription())||StringUtils.isEmpty(globalAsset.getName())) {
            throw new RequestDataNull("Global asset description and name cannotbe null");
        } else {

            if(globalAssetMongoRepository.findByName(globalAsset.getName())!=null)
            {
                throw new DuplicateDataException("asset for name "+globalAsset.getName()+" already exists");
            }
            GlobalAsset newAsset=new GlobalAsset();
            List<Long> organizationType, organizationSubType, organizationService, organizationSubService;
            organizationType = globalAsset.getOrganisationType();
            organizationSubType = globalAsset.getOrganisationSubType();
            organizationService = globalAsset.getOrganisationService();
            organizationSubService = globalAsset.getOrganisationSubService();

            if (organizationType != null && !organizationType.isEmpty()) {
                newAsset.setOrganisationType(organizationType);
            }
            if (organizationSubType != null && !organizationSubType.isEmpty()) {
                newAsset.setOrganisationSubType(organizationSubType);
            }

            if (organizationService != null && !organizationService.isEmpty()) {
                newAsset.setOrganisationService(organizationService);
            }

            if (organizationSubService != null && !organizationSubService.isEmpty()) {
                newAsset.setOrganisationSubService(organizationSubService);
            }
            newAsset.setName(globalAsset.getName());
            System.err.println(globalAsset.getName());
            newAsset.setDescription(globalAsset.getDescription());
            return save(newAsset);

        }


    }


    public List<GlobalAsset> getAllAsset() {
        List<GlobalAsset> assets = globalAssetMongoRepository.findAll();
        if (assets.size() != 0) {
            return assets;
        } else
            throw new NotExists("no Assets found create assets");

    }


    public GlobalAsset updateAsset(BigInteger id, GlobalAsset globalasset) {
        GlobalAsset exists = globalAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else {
            List<Long> organizationType, organizationSubType, organizationService, organizationSubService;
            organizationType = globalasset.getOrganisationType();
            organizationSubType = globalasset.getOrganisationSubType();
            organizationService = globalasset.getOrganisationService();
            organizationSubService = globalasset.getOrganisationSubService();
            if (StringUtils.isEmpty(globalasset.getDescription()))
            {
                throw new RequestDataNull("description cannot be null");
            }
            if (organizationType != null && !organizationType.isEmpty()) {
                exists.setOrganisationType(organizationType);
            }
            if (organizationSubType != null && !organizationSubType.isEmpty()) {
                exists.setOrganisationSubType(organizationSubType);
            }
            if (organizationService != null && !organizationService.isEmpty()) {
                exists.setOrganisationService(organizationService);
            }
            if (organizationSubService != null && !organizationSubService.isEmpty()) {
                exists.setOrganisationSubService(organizationSubService);
            }
        }
        exists.setDescription(globalasset.getDescription());
        return save(exists);
    }

    public GlobalAsset getAssetById(BigInteger id) {
        GlobalAsset exists = globalAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            return exists;

    }


    public Boolean deleteAssetById(BigInteger id) {
        GlobalAsset exists = globalAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            globalAssetMongoRepository.delete(exists);
        return true;

    }

    public GlobalAsset getGlobalAssetById(BigInteger id) {
        GlobalAsset exists = globalAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else

        return exists;

    }





}

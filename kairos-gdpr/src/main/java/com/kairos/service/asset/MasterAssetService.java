package com.kairos.service.asset;


import com.kairos.client.OrganizationTypeRestClient;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.asset.MasterAsset;
import com.kairos.persistance.model.asset.dto.MasterAssetDto;
import com.kairos.persistance.repository.asset.MasterAssetMongoRepository;
import com.kairos.response.dto.OrganizationTypeRestClientDto;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class MasterAssetService extends MongoBaseService {


    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;

    @Inject
    private OrganizationTypeRestClient  organizationTypeRestClient;

    public MasterAsset addAsset(MasterAssetDto masterAsset) {

        if (StringUtils.isEmpty(masterAsset.getDescription())||StringUtils.isEmpty(masterAsset.getName())) {
            throw new RequestDataNull("Global asset description and name cannotbe null");
        } else {

            if(masterAssetMongoRepository.findByName(masterAsset.getName())!=null)
            {
                throw new DuplicateDataException("master asset for name "+masterAsset.getName()+" exists");
            }
            Set<Long> organizationTypeAndSubIds=new HashSet<>();
            organizationTypeAndSubIds.addAll(masterAsset.getOrganisationType());
            organizationTypeAndSubIds.addAll(masterAsset.getOrganisationSubType());
           Map<Long,OrganizationTypeRestClientDto> organizationType= organizationTypeRestClient.getOrganizationType(organizationTypeAndSubIds);

          System.err.print(organizationType.get(345).getName());
            System.err.print(organizationType.get(344).getName());






            MasterAsset newAsset=new MasterAsset();
            newAsset.setName(masterAsset.getName());
            newAsset.setDescription(masterAsset.getDescription());
            return save(newAsset);

        }


    }


    public List<MasterAsset> getAllGlobalAsset() {
        List<MasterAsset> assets = masterAssetMongoRepository.findAll();
        if (assets.size() != 0) {
            return assets;
        } else
            throw new DataNotExists("no Assets found create assets");

    }


    public MasterAsset updateAsset(BigInteger id, MasterAsset globalasset) {
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        }
        exists.setName(globalasset.getName());
        exists.setDescription(globalasset.getDescription());
        return save(exists);
    }

    public MasterAsset getAssetById(BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            return exists;

    }


    public Boolean deleteAsset(BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (exists==null) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            masterAssetMongoRepository.delete(exists);
        return true;

    }

    public MasterAsset getGlobalAssetById(BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else {
/*

            List<OrganizationTypeRestClientDto> organizationTypes = organizationTypeRestClient.getOrganizationType(new HashSet<>(exists.getOrganisationType()));
            List<OrganizationTypeRestClientDto> organizationSubTypes = organizationTypeRestClient.getOrganizationType(new HashSet<>(exists.getOrganisationSubType()));
            System.err.println(organizationTypes.get(0).getName());
            System.err.println(organizationSubTypes.get(1).getName());

      */  }

        return exists;
    }








}

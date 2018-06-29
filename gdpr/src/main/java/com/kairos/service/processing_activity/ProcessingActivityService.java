package com.kairos.service.processing_activity;

import com.kairos.persistance.repository.master_data_management.asset_management.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.persistance.repository.master_data_management.asset_management.TechnicalSecurityMeasureMongoRepository;
import com.kairos.persistance.repository.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.master_data_management.processing_activity_masterdata.ProcessingPurposeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.inject.Inject;

@Service
public class ProcessingActivityService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingActivityService.class);

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;


    @Inject
    private ProcessingPurposeService processingPurposeService;


    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;

/*

    ProcessingActivity createProcessingActivity(ProcessingActivityDTO processingActivityDto) {

        List<BigInteger> dataSubectids, processingPurposeids;
        dataSubectids = processingActivityDto.getDataSubjects();
        processingPurposeids = processingActivityDto.getProcessingPurposes();

        ProcessingActivity exists = processingActivityMongoRepository.findByName(processingActivityDto.getName());
        if (!Optional.ofNullable(exists).isPresent()) {
            ProcessingActivity processingActivity = new ProcessingActivity();

            AssetType assetType = assetTypeMongoRepository.findByIdAndNonDeleted(processingActivityDto.getAssetTypeid());
            OrganizationalSecurityMeasure organizationalSecurityMeasure = organizationalSecurityMeasureMongoRepository.findByIdAndNonDeleted(processingActivityDto.getOrgSecurityMeasureid());

            if (assetType == null) {
                throw new DataNotExists("asset type not exist for id " + processingActivityDto.getAssetTypeid());

            }
            if (organizationalSecurityMeasure == null) {
                throw new DataNotExists("organizationalSecurityMeasure  not exist for id " + processingActivityDto.getOrgSecurityMeasureid());

            }
            TechnicalSecurityMeasure technicalSecurityMeasure = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(processingActivityDto.getTechnicalSecurityMeasure());
            if (technicalSecurityMeasure == null) {
                throw new DataNotExists("technical SecurityMeasure  not exist for id " + processingActivityDto.getTechnicalSecurityMeasure());


            }
            processingActivity.setName(processingActivityDto.getName());
            processingActivity.setDescription(processingActivityDto.getDescription());
            processingActivity.setManagingDepartmentOrganization(processingActivityDto.getManagingDepartmentOrganization());
            processingActivity.setDataRetentionPeriod(processingActivityDto.getDataRetentionPeriod());
            processingActivity.setAssetType(assetType);
            processingActivity.setOrganizationalSecurityMeasure(organizationalSecurityMeasure);
            processingActivity.setTechnicalSecurityMeasure(technicalSecurityMeasure);
            processingActivity.setDataSubjects(dataSubjectService.getDataSubjectList(dataSubectids));
            processingActivity.setProcessingPurposes(processingPurposeService.geProcessingPurposeList(processingPurposeids));
            processingActivity.setOrganisationId(processingActivity.getOrganisationId());
            processingActivity.setHostingCountryId(processingActivityDto.getHostingCountryId());
            processingActivity.setProcessOwnerStaff(processingActivityDto.getProcessOwnerStaff());
            return save(processingActivity);

        } else
            throw new DuplicateDataException("ProcessingActivity for " + processingActivityDto.getName() + "exists");
    }
*/


}

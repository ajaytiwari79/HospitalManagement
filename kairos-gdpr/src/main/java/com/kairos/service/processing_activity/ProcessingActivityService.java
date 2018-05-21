package com.kairos.service.processing_activity;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.dto.ProcessingActivityDto;
import com.kairos.persistance.model.processing_activity.AssetType;
import com.kairos.persistance.model.master_data_management.asset_management.OrganizationalSecurityMeasure;
import com.kairos.persistance.model.master_data_management.asset_management.TechnicalSecurityMeasure;
import com.kairos.persistance.model.processing_activity.ProcessingActivity;
import com.kairos.persistance.repository.processing_activity.AssetTypeMongoRepository;
import com.kairos.persistance.repository.master_data_management.asset_management.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.persistance.repository.master_data_management.asset_management.TechnicalSecurityMeasureMongoRepository;
import com.kairos.persistance.repository.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.master_data_management.processing_activity_masterdata.DataSubjectService;
import com.kairos.service.master_data_management.processing_activity_masterdata.ProcessingPurposeService;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessingActivityService extends MongoBaseService {


    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;

    @Inject
    private DataSubjectService dataSubjectService;

    @Inject
    private ProcessingPurposeService processingPurposeService;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;


    ProcessingActivity createProcessingActivity(ProcessingActivityDto processingActivityDto) {

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
            processingActivity.setDataSubjects(dataSubjectService.dataSubjectList(dataSubectids));
            processingActivity.setProcessingPurposes(processingPurposeService.processingPurposeList(processingPurposeids));
            processingActivity.setOrganisationId(processingActivity.getOrganisationId());
            processingActivity.setHostingCountryId(processingActivityDto.getHostingCountryId());
            processingActivity.setProcessOwnerStaff(processingActivityDto.getProcessOwnerStaff());
            return save(processingActivity);

        } else
            throw new DuplicateDataException("ProcessingActivity for " + processingActivityDto.getName() + "exists");
    }


}

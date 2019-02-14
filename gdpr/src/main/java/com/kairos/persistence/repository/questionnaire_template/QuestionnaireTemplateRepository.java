package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
////@JaversSpringDataAuditable
public interface QuestionnaireTemplateRepository extends CustomGenericRepository<QuestionnaireTemplate> ,CustomQuestionnaireTemplateRepository {


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.templateType = ?2 and QT.isDefaultAssetTemplate =?3 and QT.deleted = false")
    QuestionnaireTemplate findQuestionnaireTemplateByCountryIdAndTemplateTypeAndDefaultAssetTemplate(Long countryId, QuestionnaireTemplateType templateType, boolean isDefaultAssetTemplate);

    @Query(value = "Select QT from QuestionnaireTemplate QT where  QT.templateType = ?1 and QT.riskAssociatedEntity =?2 and QT.countryId = ?3 and QT.deleted = false")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndRiskAssociatedEntityAndCountryId(QuestionnaireTemplateType templateType, QuestionnaireTemplateType riskAssociatedEntity, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.templateType = ?2 and QT.deleted = false")
    QuestionnaireTemplate findQuestionnaireTemplateByCountryIdAndTemplateType(Long countryId, QuestionnaireTemplateType templateType);


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId=?1 and QT.deleted = false and QT.isDefaultAssetTemplate= true")
    QuestionnaireTemplate findDefaultAssetQuestionnaireTemplateByCountryId(long countryId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?3 and QT.assetType.id = ?2 and QT.assetSubType IS NULL and QT.deleted = false and QT.templateStatus = ?4")
    QuestionnaireTemplate findQuestionnaireTemplateByUnitIdAssetTypeIdAndTemplateStatus(Long orgId, Long assetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus );

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?2 and QT.templateStatus = ?3")
    QuestionnaireTemplate findQuestionnaireTemplateByUnitIdAndTemplateTypeAndTemplateStatus(Long orgId, QuestionnaireTemplateType templateType , QuestionnaireTemplateStatus templateStatus );


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.assetType.id = ?2 and QT.assetSubType IS NULL and QT.deleted = false and QT.templateType = ?3  and QT.templateStatus = ?4")
    QuestionnaireTemplate findQuestionnaireTemplateByAssetTypeAndByCountryId(Long countryId, Long assetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?4 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.deleted = false and QT.templateStatus = ?5")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeId(Long orgId, Long assetTypeId, Long subAssetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus );

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.templateType = ?4 and QT.deleted = false and QT.templateStatus = ?5")
    QuestionnaireTemplate findQuestionnaireTemplateByAssetTypeAndSubAssetTypeByCountryId(Long countryId, Long assetTypeId, Long subAssetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus);


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.id = ?2 and QT.deleted = false")
    QuestionnaireTemplate getMasterQuestionnaireTemplateWithSectionsByCountryId(Long countryId, Long questionnaireTemplateId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.id = ?2 and QT.deleted = false")
    QuestionnaireTemplate getQuestionnaireTemplateWithSectionsByOrganizationId(Long orgId, Long questionnaireTemplateId);

   /* @Modifying
    @Transactional
    @Query(value = "delete from questionnaire_templatemd_sections where questionnaire_templatemd_id = ?1 and sections_id = ?2", nativeQuery = true)
    Integer removeSectionFromQuestionnaireTemplate(Long templateId, Long sectionId);*/

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.templateType = ?1 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.countryId = ?4   and  QT.deleted = false")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndSubAssetTypeAndCountryId(QuestionnaireTemplateType templateType, Long assetTypeId, Long subAssetTypeId, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.templateType = ?1 and QT.assetType.id = ?2 and QT.countryId = ?3   and  QT.deleted = false")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndAndCountryId(QuestionnaireTemplateType templateType, Long assetTypeId, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.templateType = ?1 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.organizationId = ?4   and  QT.deleted = false")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndSubAssetTypeAndOrganizationId(QuestionnaireTemplateType templateType, Long assetTypeId, Long subAssetTypeId, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.templateType = ?1 and QT.assetType.id = ?2 and QT.organizationId = ?3   and  QT.deleted = false")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndAndOrganizationId(QuestionnaireTemplateType templateType, Long assetTypeId, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.templateType = ?1 and QT.isDefaultAssetTemplate = ?2 and QT.organizationId = ?3 and QT.templateStatus = ?4 and QT.deleted = false")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByTemplateTypeAndDefaultAssetTemplateAndOrganizationId(QuestionnaireTemplateType templateType, boolean defaultAssetTemplate, Long orgId, QuestionnaireTemplateStatus status);


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?2 and  QT.templateStatus = ?3  and QT.deleted = false")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByOrganizationIdAndTemplateType(Long orgId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus status);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.templateType = ?4  and QT.templateStatus = ?5 and QT.riskAssociatedEntity = ?6 and QT.deleted = false")
    QuestionnaireTemplate findPublishedRiskTemplateByUnitIdAndAssetTypeAndSubAssetTypeAndTemplateType(Long orgId, Long assetTypeId, Long subAssetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus status, QuestionnaireTemplateType riskAssociatedEntity);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?2 and QT.templateStatus = ?3  and QT.deleted = false")
    QuestionnaireTemplate getQuestionnaireTemplateByTemplateTypeAndUnitId(QuestionnaireTemplateType templateType, Long orgId, QuestionnaireTemplateStatus status);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.riskAssociatedEntity = ?2 and QT.templateType = ?3 and QT.templateStatus = ?4 and QT.deleted = false")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByProcessingActivityAndByUnitId(Long orgId, QuestionnaireTemplateType riskAssociatedEntity, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus);
}

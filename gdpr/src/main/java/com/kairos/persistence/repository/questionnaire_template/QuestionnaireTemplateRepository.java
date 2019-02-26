package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
////@JaversSpringDataAuditable
public interface QuestionnaireTemplateRepository extends CustomGenericRepository<QuestionnaireTemplate> {


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1  and QT.riskAssociatedEntity = ?2 and QT.templateType = ?3 and QT.templateStatus = ?4 and QT.deleted = false")
    QuestionnaireTemplate findTemplateByUnitIdAndRiskAssociatedEntityAndTemplateTypeAndStatus(Long orgId, QuestionnaireTemplateType riskAssociatedEntity, QuestionnaireTemplateType templateType , QuestionnaireTemplateStatus templateStatus);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.deleted = false")
    List<QuestionnaireTemplate> getAllQuestionnaireTemplateByOrganizationId(Long orgId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.deleted = false")
    List<QuestionnaireTemplate> getAllMasterQuestionnaireTemplateByCountryId(Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?2 and QT.templateStatus = ?3 and QT.isDefaultAssetTemplate = true and QT.deleted = false")
    QuestionnaireTemplate findDefaultTemplateByUnitIdAndTemplateTypeAndStatus(Long unitId, QuestionnaireTemplateType templateType,QuestionnaireTemplateStatus templateStatus);

    @Query(value = "Select QT from QuestionnaireTemplate QT where  QT.countryId = ?1 and QT.templateType = ?2 and QT.riskAssociatedEntity = ?2 and  QT.deleted = false")
    QuestionnaireTemplate findTemplateByCountryIdAndTemplateTypeAndRiskAssociatedEntity( Long countryId,QuestionnaireTemplateType templateType, QuestionnaireTemplateType riskAssociatedEntity);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.templateType = ?2 and QT.deleted = false")
    QuestionnaireTemplate findQuestionnaireTemplateByCountryIdAndTemplateType(Long countryId, QuestionnaireTemplateType templateType);


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId=?1 and QT.deleted = false and QT.isDefaultAssetTemplate= true")
    QuestionnaireTemplate findDefaultAssetQuestionnaireTemplateByCountryId(long countryId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1  and QT.assetType.id = ?2 and QT.templateType = ?3 and QT.templateStatus = ?4 and QT.assetSubType IS NULL and QT.deleted = false ")
    QuestionnaireTemplate findTemplateByUnitIdAssetTypeIdAndTemplateTypeAndTemplateStatus(Long orgId, Long assetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus );

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?2 and QT.templateStatus = ?3")
    QuestionnaireTemplate findQuestionnaireTemplateByUnitIdAndTemplateTypeAndTemplateStatus(Long orgId, QuestionnaireTemplateType templateType , QuestionnaireTemplateStatus templateStatus );


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.assetType.id = ?2 and QT.templateType = ?3  and QT.assetSubType IS NULL and QT.deleted = false ")
    QuestionnaireTemplate findTemplateByCountryIdAndAssetTypeIdAndTemplateType(Long countryId, Long assetTypeId, QuestionnaireTemplateType templateType);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1  and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.templateType = ?4 and QT.deleted = false and QT.templateStatus = ?5")
    QuestionnaireTemplate findTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeIdTemplateTypeAndStatus(Long orgId, Long assetTypeId, Long subAssetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus );

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3  and QT.templateType = ?4  and QT.deleted = false ")
    QuestionnaireTemplate findTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeIdAndTemplateType(Long countryId, Long assetTypeId, Long subAssetTypeId,QuestionnaireTemplateType templateType);


    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.id = ?2 and QT.deleted = false")
    QuestionnaireTemplate getMasterQuestionnaireTemplateWithSectionsByCountryId(Long countryId, Long questionnaireTemplateId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.id = ?2 and QT.deleted = false")
    QuestionnaireTemplate getQuestionnaireTemplateWithSectionsByOrganizationId(Long orgId, Long questionnaireTemplateId);

   /* @Modifying
    @Transactional
    @Query(value = "delete from questionnaire_templatemd_sections where questionnaire_templatemd_id = ?1 and sections_id = ?2", nativeQuery = true)
    Integer removeSectionFromQuestionnaireTemplate(Long templateId, Long sectionId);*/

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?2 and  QT.templateStatus = ?3  and QT.deleted = false")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByOrganizationIdAndTemplateType(Long orgId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus status);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.templateType = ?2 and QT.templateStatus = ?3  and QT.deleted = false")
    QuestionnaireTemplate getQuestionnaireTemplateByTemplateTypeAndUnitId(QuestionnaireTemplateType templateType, Long orgId, QuestionnaireTemplateStatus status);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.riskAssociatedEntity = ?2 and QT.templateType = ?3 and QT.templateStatus = ?4 and QT.deleted = false")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByProcessingActivityAndByUnitId(Long orgId, QuestionnaireTemplateType riskAssociatedEntity, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.organizationId = ?1 and QT.deleted = false")
    List<QuestionnaireTemplate> getAllQuestionnaireTemplateWithSectionsAndQuestionsByOrganizationId(Long orgId);

    @Query(value = "Select QT from QuestionnaireTemplate QT where QT.countryId = ?1 and QT.deleted = false")
    List<QuestionnaireTemplate> getAllMasterQuestionnaireTemplateWithSectionsAndQuestionsByCountryId(Long countryId);
}

package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplateMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface QuestionnaireTemplateRepository extends CustomGenericRepository<QuestionnaireTemplateMD> {


    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.countryId = ?1 and QT.templateType = ?2 and QT.isDefaultAssetTemplate =?3 and QT.deleted = false")
    QuestionnaireTemplateMD findQuestionnaireTemplateByCountryIdAndTemplateTypeAndDefaultAssetTemplate(Long countryId, QuestionnaireTemplateType templateType, boolean isDefaultAssetTemplate);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where  QT.templateType = ?1 and QT.riskAssociatedEntity =?2 and QT.countryId = ?3 and QT.deleted = false")
    QuestionnaireTemplateMD findQuestionnaireTemplateByTemplateTypeAndRiskAssociatedEntityAndCountryId(QuestionnaireTemplateType templateType, QuestionnaireTemplateType riskAssociatedEntity, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.countryId = ?1 and QT.templateType = ?2 and QT.deleted = false")
    QuestionnaireTemplateMD findQuestionnaireTemplateByCountryIdAndTemplateType(Long countryId, QuestionnaireTemplateType templateType);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.organizationId = ?1 and QT.templateType = ?2 and QT.templateStatus = ?3 and QT.deleted = false and QT.isDefaultAssetTemplate = true")
    QuestionnaireTemplateMD findDefaultAssetQuestionnaireTemplateByUnitId(Long orgId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.countryId=?1 and QT.deleted = false and QT.isDefaultAssetTemplate= true")
    QuestionnaireTemplateMD findDefaultAssetQuestionnaireTemplateByCountryId(long countryId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.organizationId = ?1 and QT.templateType = ?3 and QT.assetType.id = ?2 and QT.assetSubType IS NULL and QT.deleted = false and QT.templateStatus = ?4")
    QuestionnaireTemplateMD findPublishedQuestionnaireTemplateByAssetTypeAndByUnitId(Long orgId, Long assetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus );

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.countryId = ?1 and QT.assetType.id = ?2 and QT.assetSubType IS NULL and QT.deleted = false and QT.templateType = ?3  and QT.templateStatus = ?4")
    QuestionnaireTemplateMD findQuestionnaireTemplateByAssetTypeAndByCountryId(Long countryId, Long assetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.organizationId = ?1 and QT.templateType = ?4 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.deleted = false and QT.templateStatus = ?5")
    QuestionnaireTemplateMD findPublishedQuestionnaireTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeId(Long orgId, Long assetTypeId, Long subAssetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus );

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.countryId = ?1 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.templateType = ?4 and QT.deleted = false and QT.templateStatus = ?5")
    QuestionnaireTemplateMD findQuestionnaireTemplateByAssetTypeAndSubAssetTypeByCountryId(Long countryId, Long assetTypeId, Long subAssetTypeId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus templateStatus);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.countryId = ?1 and QT.deleted = false")
    List<QuestionnaireTemplateMD> getAllMasterQuestionnaireTemplateWithSectionsAndQuestionsByCountryId(Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.countryId = ?1 and QT.id = ?2 and QT.deleted = false")
    QuestionnaireTemplateMD getMasterQuestionnaireTemplateWithSectionsByCountryId(Long countryId, Long questionnaireTemplateId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.organizationId = ?1 and QT.deleted = false")
    List<QuestionnaireTemplateMD> getAllQuestionnaireTemplateWithSectionsAndQuestionsByOrganizationId(Long orgId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.organizationId = ?1 and QT.id = ?2 and QT.deleted = false")
    QuestionnaireTemplateMD getQuestionnaireTemplateWithSectionsByOrganizationId(Long orgId, Long questionnaireTemplateId);

    @Modifying
    @Transactional
    @Query(value = "delete from questionnaire_templatemd_sections where questionnaire_templatemd_id = ?1 and sections_id = ?2", nativeQuery = true)
    Integer removeSectionFromQuestionnaireTemplate(Long templateId, Long sectionId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.templateType = ?1 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.countryId = ?4   and  QT.deleted = false")
    QuestionnaireTemplateMD findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndSubAssetTypeAndCountryId(QuestionnaireTemplateType templateType,Long assetTypeId, Long subAssetTypeId, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.templateType = ?1 and QT.assetType.id = ?2 and QT.countryId = ?3   and  QT.deleted = false")
    QuestionnaireTemplateMD findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndAndCountryId(QuestionnaireTemplateType templateType,Long assetTypeId, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.templateType = ?1 and QT.assetType.id = ?2 and QT.assetSubType.id = ?3 and QT.organizationId = ?4   and  QT.deleted = false")
    QuestionnaireTemplateMD findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndSubAssetTypeAndOrganizationId(QuestionnaireTemplateType templateType,Long assetTypeId, Long subAssetTypeId, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.templateType = ?1 and QT.assetType.id = ?2 and QT.organizationId = ?3   and  QT.deleted = false")
    QuestionnaireTemplateMD findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndAndOrganizationId(QuestionnaireTemplateType templateType,Long assetTypeId, Long countryId);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.templateType = ?1 and QT.isDefaultAssetTemplate = ?2 and QT.organizationId = ?3 and QT.templateStatus = ?4 and QT.deleted = false")
    QuestionnaireTemplateMD findPublishedQuestionnaireTemplateByTemplateTypeAndDefaultAssetTemplateAndOrganizationId(QuestionnaireTemplateType templateType,boolean defaultAssetTemplate, Long orgId, QuestionnaireTemplateStatus status);


    @Query(value = "Select QT from QuestionnaireTemplateMD QT where  QT.templateType = ?1 and QT.riskAssociatedEntity =?2 and QT.organizationId = ?3 and QT.templateStatus = ?4  and QT.deleted = false")
    QuestionnaireTemplateMD findPublishedQuestionnaireTemplateByTemplateTypeAndRiskAssociatedEntityAndOrganizationId(QuestionnaireTemplateType templateType, QuestionnaireTemplateType riskAssociatedEntity, Long orgId, QuestionnaireTemplateStatus status);

    @Query(value = "Select QT from QuestionnaireTemplateMD QT where QT.organizationId = ?1 and QT.templateType = ?2 and  QT.templateStatus = ?3  and QT.deleted = false")
    QuestionnaireTemplateMD findPublishedQuestionnaireTemplateByOrganizationIdAndTemplateType(Long orgId, QuestionnaireTemplateType templateType, QuestionnaireTemplateStatus status);

}

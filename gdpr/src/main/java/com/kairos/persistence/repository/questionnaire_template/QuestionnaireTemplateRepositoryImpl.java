package com.kairos.persistence.repository.questionnaire_template;

import com.kairos.custom_exception.JpaCustomDatabaseException;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class QuestionnaireTemplateRepositoryImpl implements CustomQuestionnaireTemplateRepository {


    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionnaireTemplateRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private static String selectConstant = "Select QT from QuestionnaireTemplate QT ";


    @Override
    public QuestionnaireTemplate findPublishedRiskTemplateByAssociatedEntityAndOrgId(Long orgId, QuestionnaireTemplateType riskAssociatedEntity) {

        LOGGER.debug("findPublishedRiskTemplateByAssociatedEntityAndOrgId() method call");
        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(selectConstant +
                    " where QT.organizationId = :orgId " +
                    " and QT.deleted = false " +
                    " and QT.riskAssociatedEntity = :riskAssociatedEntity " +
                    " and QT.templateType = :templateType " +
                    " and QT.templateStatus = :templateStatus", QuestionnaireTemplate.class);
            query.setParameter("orgId", orgId);
            query.setParameter("templateType", QuestionnaireTemplateType.RISK);
            query.setParameter("riskAssociatedEntity", riskAssociatedEntity);
            query.setParameter("templateStatus", QuestionnaireTemplateStatus.PUBLISHED);
            return query.getSingleResult();

        } catch (Exception e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method findPublishedRiskTemplateByAssociatedEntityAndOrgId");
            throw new JpaCustomDatabaseException(e.getMessage());
        }

    }


    @Override
    public QuestionnaireTemplate findPublishedRiskTemplateByAssetTypeIdAndOrgId(Long orgId, Long assetTypeId) {
        LOGGER.debug("findPublishedRiskTemplateByAssetTypeIdAndOrgId() method call");

        QuestionnaireTemplate questionnaireTemplate = null;
        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(selectConstant +
                    " where QT.organizationId = :orgId " +
                    " and QT.deleted = false " +
                    " and QT.assetType.id = :assetTypeId " +
                    " and QT.templateType = :templateType " +
                    " and QT.riskAssociatedEntity = :riskAssociatedEntity " +
                    " and QT.templateStatus = :templateStatus", QuestionnaireTemplate.class);
            query.setParameter("orgId", orgId);
            query.setParameter("assetTypeId", assetTypeId);
            query.setParameter("riskAssociatedEntity", QuestionnaireTemplateType.ASSET_TYPE);
            query.setParameter("templateType", QuestionnaireTemplateType.RISK);
            query.setParameter("templateStatus", QuestionnaireTemplateStatus.PUBLISHED);
            questionnaireTemplate = query.getSingleResult();

        } catch (NoResultException e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method findPublishedRiskTemplateByAssetTypeIdAndOrgId");
        }
        return questionnaireTemplate;

    }

    @Override
    public QuestionnaireTemplate findPublishedRiskTemplateByOrgIdAndAssetTypeIdAndSubAssetTypeId(Long orgId, Long assetTypeId, Long assetSubTypeId) {
        LOGGER.debug("findPublishedRiskTemplateByOrgIdAndAssetTypeIdAndSubAssetTypeId() method call");

        QuestionnaireTemplate questionnaireTemplate = null;
        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(selectConstant +
                    " where QT.organizationId = :orgId " +
                    " and QT.deleted = false " +
                    " and QT.assetType.id = :assetTypeId " +
                    " and QT.assetSubType.id = :assetSubTypeId " +
                    " and QT.templateType = :templateType " +
                    " and QT.templateStatus = :templateStatus", QuestionnaireTemplate.class);
            query.setParameter("orgId", orgId);
            query.setParameter("assetTypeId", assetTypeId);
            query.setParameter("assetSubTypeId", assetSubTypeId);
            query.setParameter("templateType", QuestionnaireTemplateType.RISK);
            query.setParameter("templateStatus", QuestionnaireTemplateStatus.PUBLISHED);
            questionnaireTemplate= query.getSingleResult();

        } catch (NoResultException e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method findPublishedRiskTemplateByOrgIdAndAssetTypeIdAndSubAssetTypeId");
        }
        return questionnaireTemplate;
    }

    @Override
    public QuestionnaireTemplate findRiskTemplateByAssociatedEntityAndCountryId(Long countryId, QuestionnaireTemplateType riskAssociatedEntity) {
        LOGGER.debug("findRiskTemplateByAssociatedEntityAndCountryId() method call");
        QuestionnaireTemplate questionnaireTemplate = null;

        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(selectConstant +
                    " where QT.countryId = :countryId " +
                    " and QT.deleted = false " +
                    " and QT.riskAssociatedEntity = :riskAssociatedEntity " +
                    " and QT.templateType = :templateType ", QuestionnaireTemplate.class);
            query.setParameter("templateType", QuestionnaireTemplateType.RISK);
            query.setParameter("countryId", countryId);
            query.setParameter("riskAssociatedEntity", riskAssociatedEntity);
            questionnaireTemplate = query.getSingleResult();

        } catch (NoResultException e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method findRiskTemplateByAssociatedEntityAndCountryId");
        }
        return questionnaireTemplate;
    }

    @Override
    public QuestionnaireTemplate findRiskTemplateByCountryIdAndAssetTypeId(Long countryId, Long assetTypeId) {
        LOGGER.debug("findRiskTemplateByCountryIdAndAssetTypeId() method call");
        QuestionnaireTemplate questionnaireTemplate = null;
        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(selectConstant +
                    " where QT.countryId = :countryId " +
                    " and QT.deleted = false " +
                    " and QT.assetType.id = :assetTypeId " +
                    " and QT.templateType = :templateType ", QuestionnaireTemplate.class);
            query.setParameter("templateType", QuestionnaireTemplateType.RISK);
            query.setParameter("countryId", countryId);
            query.setParameter("assetTypeId", assetTypeId);
            questionnaireTemplate= query.getSingleResult();

        } catch (NoResultException e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method findRiskTemplateByCountryIdAndAssetTypeId",e.getMessage());
        }
        return questionnaireTemplate;
    }

    @Override
    public QuestionnaireTemplate findRiskTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeId(Long countryId, Long assetTypeId, Long assetSubTypeId) {
        LOGGER.debug("findRiskTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeId() method call");
        QuestionnaireTemplate questionnaireTemplate = null;
        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(selectConstant +
                    " where QT.countryId = :countryId " +
                    " and QT.deleted = false " +
                    " and QT.assetType.id = :assetTypeId " +
                    " and QT.assetSubType.id = :assetSubTypeId " +
                    " and QT.templateType = :templateType ", QuestionnaireTemplate.class);
            query.setParameter("templateType", QuestionnaireTemplateType.RISK);
            query.setParameter("countryId", countryId);
            query.setParameter("assetTypeId", assetTypeId);
            questionnaireTemplate= query.getSingleResult();

        } catch (NoResultException e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method findRiskTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeId");
        }
        return questionnaireTemplate;
    }

    @Override
    public QuestionnaireTemplate getDefaultPublishedAssetQuestionnaireTemplateByUnitId(Long orgId) {

        QuestionnaireTemplate questionnaireTemplate = null;
        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(
                    "select QT FROM QuestionnaireTemplate QT " +
                            " where QT.organizationId = :orgId " +
                            " and QT.deleted = false " +
                            " and QT.isDefaultAssetTemplate = true " +
                            " and QT.templateStatus = :templateStatus " +
                            " and QT.templateType = :templateType ", QuestionnaireTemplate.class);
            query.setParameter("templateType", QuestionnaireTemplateType.ASSET_TYPE);
            query.setParameter("orgId", orgId);
            query.setParameter("templateStatus", QuestionnaireTemplateStatus.PUBLISHED);
            questionnaireTemplate = query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.info(" Message in QuestionnaireTemplateRepositoryImpl  method findRiskTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeId", e.getCause());
        }
        return questionnaireTemplate;
    }

    @Override
    public List<QuestionnaireTemplate> getAllQuestionnaireTemplateByOrganizationId(Long orgId) {
        LOGGER.debug("getAllQuestionnaireTemplateByOrganizationId() method call");
        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(
                    "select QT FROM QuestionnaireTemplate QT " +
                            " where QT.organizationId = :orgId " +
                            " and QT.deleted = false "
                    , QuestionnaireTemplate.class);
            query.setParameter("orgId", orgId);
            return query.getResultList();

        } catch (Exception e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method getAllQuestionnaireTemplateByOrganizationId");
            throw new JpaCustomDatabaseException(e.getMessage());
        }
    }

    @Override
    public List<QuestionnaireTemplate> getAllMasterQuestionnaireTemplateByCountryId(Long countryId) {
        LOGGER.debug("getAllMasterQuestionnaireTemplateByCountryId() method call");
        try {
            TypedQuery<QuestionnaireTemplate> query = entityManager.createQuery(
                    "select QT FROM QuestionnaireTemplate QT " +
                            " where QT.countryId = :countryId " +
                            " and QT.deleted = false ", QuestionnaireTemplate.class);
            query.setParameter("countryId", countryId);
            return query.getResultList();

        } catch (Exception e) {
            LOGGER.info(" Error in QuestionnaireTemplateRepositoryImpl  method getAllMasterQuestionnaireTemplateByCountryId");
            throw new JpaCustomDatabaseException(e.getMessage());
        }
    }
}

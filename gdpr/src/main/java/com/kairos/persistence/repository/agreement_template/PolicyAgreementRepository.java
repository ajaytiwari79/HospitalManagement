package com.kairos.persistence.repository.agreement_template;

import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface PolicyAgreementRepository extends CustomGenericRepository<PolicyAgreementTemplate> {

    @Query(value = "Select PAT from PolicyAgreementTemplate PAT where PAT.countryId = ?1 and PAT.id = ?2 and PAT.deleted = false")
    List<PolicyAgreementTemplate> getAllAgreementSectionsAndSubSectionByCountryIdAndAgreementTemplateId(Long countryId, Long agreementTemplateId);


    @Query(value = "Select PAT from PolicyAgreementTemplate PAT where PAT.organizationId = ?1 and PAT.id = ?2 and PAT.deleted = false")
    List<PolicyAgreementTemplate> getAllAgreementSectionsAndSubSectionByOrganizationIdAndAgreementTemplateId(Long orgId, Long agreementTemplateId);

    @Query(value = "Select PAT from PolicyAgreementTemplate PAT JOIN PAT.agreementSections AGS left join AGS.agreementSubSections AGSS on AGS.id = AGSS.agreementSection.id " +
            " left Join AGS.clauses AGSC left join AGSS.clauses AGSSC where PAT.organizationId = ?1  and (AGSC.id = ?2 or AGSSC.id =?2)  and PAT.deleted = false group by PAT.id")
    List<PolicyAgreementTemplate> getAllPolicyAgreementTemplateContainClauseByUnitIdAndClauseId(Long unitId, Long clauseId);

    @Query(value = "Select PAT from PolicyAgreementTemplate PAT JOIN PAT.agreementSections AGS left join AGS.agreementSubSections AGSS on AGS.id = AGSS.agreementSection.id " +
            " left Join AGS.clauses AGSC left join AGSS.clauses AGSSC where PAT.countryId = ?1  and (AGSC.id = ?2 or AGSSC.id =?2)  and PAT.deleted = false group by PAT.id")
    List<PolicyAgreementTemplate> getAllPolicyAgreementTemplateContainClauseByCountryIdAndClauseId(Long countryId, Long clauseId);

    @Query(value = "SELECT EN FROM PolicyAgreementTemplate EN WHERE EN.countryId = ?1 and EN.deleted = false and lower(EN.name) = lower(?2) and EN.generalAgreementTemplate=true")
    PolicyAgreementTemplate findByCountryIdAndNameAndDataHandlerAgreementTrue(Long countryId, String name);

    @Query(value = "SELECT EN FROM PolicyAgreementTemplate EN WHERE EN.id = ?1 and EN.countryId = ?2 and EN.deleted = false and EN.generalAgreementTemplate = true")
    PolicyAgreementTemplate findByIdAndCountryIdAndDeletedFalseAndDataHandlerTrue(Long id, Long countryId);

    @Query(value = "Select EN from PolicyAgreementTemplate EN WHERE EN.countryId = ?1 and EN.deleted = false  order by EN.createdAt desc")
    List<PolicyAgreementTemplate> findAllAgreementTemplateAndGeneralAgreementByCountryId(Long countryId);

    @Query(value = "Select EN from PolicyAgreementTemplate EN WHERE EN.deleted = false and  EN.generalAgreementTemplate = true order by EN.createdAt desc")
    List<PolicyAgreementTemplate> findAllGeneralAgreementTemplate();

    @Query(value = "Select EN from PolicyAgreementTemplate EN WHERE EN.deleted = false and  EN.countryId = ?1 and EN.templateType.id = ?2 order by EN.createdAt desc")
    List<PolicyAgreementTemplate> findAllDataHandlerAgreementTemplateByCountry(Long countryId,Long templateTypeId);

}

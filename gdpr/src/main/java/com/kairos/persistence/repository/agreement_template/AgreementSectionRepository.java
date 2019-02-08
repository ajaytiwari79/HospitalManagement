package com.kairos.persistence.repository.agreement_template;


import com.kairos.persistence.model.agreement_template.AgreementSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
////@JaversSpringDataAuditable
public interface AgreementSectionRepository extends JpaRepository<AgreementSection, Long> {

    @Transactional
    @Modifying
    @Query(value = "update agreement_sectionmd_clauses set deleted = true , agreement_sectionmd_id=null where agreement_sectionmd_id =?1 and id = ?2", nativeQuery = true)
    Integer removeClauseIdFromAgreementSection(Long sectionId, Long clauseId);

    @Query(value = "SELECT hp FROM AgreementSection hp WHERE hp.id = ?1 and hp.countryId = ?2 and hp.deleted = false")
    AgreementSection findByIdAndCountryIdAndDeleted(Long id, Long countryId);

    @Query(value = "SELECT hp FROM AgreementSection hp WHERE hp.id = ?1 and hp.organizationId = ?2 and hp.deleted = false")
    AgreementSection findByIdAndOrganizationIdAndDeleted(Long id, Long orgId);

    @Transactional
    @Modifying
    @Query(value = "update  agreement_sectionmd set deleted =true ,agreement_section_id = null WHERE id = ?2 and agreement_section_id = ?1", nativeQuery = true)
    Integer deleteAgreementSubSection(Long sectionId, Long subSectionId);

}

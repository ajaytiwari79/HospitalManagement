package com.kairos.persistence.repository.public_legal_document;

import com.kairos.persistence.model.public_legal_document.PublicLegalDocument;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created By G.P.Ranjan on 26/6/19
 **/
@Repository
@JaversSpringDataAuditable
public interface PublicLegalDocumentRepository extends JpaRepository<PublicLegalDocument, Long> {
    @Query(value = "Select p from PublicLegalDocument p where p.id = ?1 and p.deleted = false")
    PublicLegalDocument findByIdAndDeletedFalse(Long id);

    @Query(value = "Select p from PublicLegalDocument p where p.name = ?1 and p.deleted = false")
    PublicLegalDocument findByNameAndDeletedFalse(String name);

    @Query(value = "Select p from PublicLegalDocument p where p.deleted = false")
    List<PublicLegalDocument> findAllAndDeletedFalse();
}

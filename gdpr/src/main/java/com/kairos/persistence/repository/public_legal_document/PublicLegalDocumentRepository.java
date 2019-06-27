package com.kairos.persistence.repository.public_legal_document;

import com.kairos.persistence.model.public_legal_document.PublicLegalDocument;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created By G.P.Ranjan on 26/6/19
 **/
@Repository
public interface PublicLegalDocumentRepository extends JpaRepository<PublicLegalDocument, Long> {
}

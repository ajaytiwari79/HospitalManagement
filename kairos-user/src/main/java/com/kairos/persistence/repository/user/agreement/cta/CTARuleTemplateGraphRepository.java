package com.kairos.persistence.repository.user.agreement.cta;
import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplate;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CTARuleTemplateGraphRepository  extends GraphRepository<CTARuleTemplate> {
}

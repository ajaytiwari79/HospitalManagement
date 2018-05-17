package com.kairos.service.agreement_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document(collection = "agreement_section")
public class AgreementSection extends MongoBaseEntity {

private Set<Long> clauses;

    public Set<Long> getClauses() {
        return clauses;
    }

    public void setClauses(Set<Long> clauses) {
        this.clauses = clauses;
    }
}

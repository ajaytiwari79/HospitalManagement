package com.kairos.persistance.repository.agreement_template;

import com.kairos.persistance.model.agreement_template.AgreementTemplate;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigInteger;

@Repository
public interface AgreementTemplateMongoRepository extends MongoRepository<AgreementTemplate,Serializable>,CustomAgreementTemplateRepository {

    @Query("{'_id':?0}")
    AgreementTemplate findAgreementById(BigInteger id);

}

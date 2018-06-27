package com.kairos.activity.persistence.repository.night_worker;

import com.kairos.activity.persistence.model.night_worker.StaffQuestionnaire;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface StaffQuestionnaireMongoRepository extends MongoBaseRepository<StaffQuestionnaire, BigInteger> {

    @Query(value = "{ id:?0, deleted:false }")
    StaffQuestionnaire findByIdAndDeleted(BigInteger id);
}

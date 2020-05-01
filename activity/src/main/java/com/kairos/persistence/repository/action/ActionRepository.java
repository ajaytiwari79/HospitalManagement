package com.kairos.persistence.repository.action;

import com.kairos.persistence.model.action.Action;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.wrapper.action.ActionDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created By G.P.Ranjan on 2/4/20
 **/
@Repository
public interface ActionRepository extends MongoBaseRepository<Action, BigInteger> {
    @Query("{'deleted' : false,'unitId':?0}")
    List<ActionDTO> getAllByUnitId(Long unitId);
}

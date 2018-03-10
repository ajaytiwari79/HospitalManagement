package com.kairos.persistence.repository.user.pay_level;

import com.kairos.persistence.model.user.pay_level.MunicipalityPayGroupAreaWrapper;
import com.kairos.persistence.model.user.pay_level.PayGroupArea;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by prabjot on 21/12/17.
 */
@Repository
public interface PayGroupAreaGraphRepository extends Neo4jBaseRepository<PayGroupArea, Long> {
    @Query("Match(municipality:Municipality{isEnable:true}) where id(municipality) IN {0}\n" +
            "MATCH (municipality)<-[r:HAS_MUNICIPALITY]-(payGroupArea:PayGroupArea{deleted:false})\n" +
            "RETURN municipality,payGroupArea")
    List<MunicipalityPayGroupAreaWrapper> getMunicipalitiesAndPayGroup(Set<Long> municipalitiesIds);
}

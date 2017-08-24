package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.user.country.CountryTimeType;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by oodles on 23/11/16.
 */
@Repository
public interface CountryTimeTypeGraphRepository extends GraphRepository<CountryTimeType>{

    @Override
    List<CountryTimeType> findAll();
}

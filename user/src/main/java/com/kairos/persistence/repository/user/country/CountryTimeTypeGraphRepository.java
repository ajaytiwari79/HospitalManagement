package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.CountryTimeType;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by oodles on 23/11/16.
 */
@Repository
public interface CountryTimeTypeGraphRepository extends Neo4jBaseRepository<CountryTimeType,Long>{

    @Override
    List<CountryTimeType> findAll();
}

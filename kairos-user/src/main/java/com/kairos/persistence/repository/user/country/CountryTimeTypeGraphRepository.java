package com.kairos.persistence.repository.user.country;

import java.util.List;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.country.CountryTimeType;

/**
 * Created by oodles on 23/11/16.
 */
@Repository
public interface CountryTimeTypeGraphRepository extends GraphRepository<CountryTimeType>{

    @Override
    List<CountryTimeType> findAll();
}

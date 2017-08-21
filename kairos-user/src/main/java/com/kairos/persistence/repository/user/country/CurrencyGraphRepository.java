package com.kairos.persistence.repository.user.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.country.Currency;

/**
 * Created by prabjot on 9/1/17.
 */
@Repository
public interface CurrencyGraphRepository extends GraphRepository<Currency> {

    @Query("Match (n:Currency{isEnabled:true})-[:RELATED_TO]->(country:Country) where id(country)={0} return {id:id(n),name:n.name, currencyCode:n.currencyCode } as result")
    List<Map<String,Object>> getCurrencies(long countryId);
}

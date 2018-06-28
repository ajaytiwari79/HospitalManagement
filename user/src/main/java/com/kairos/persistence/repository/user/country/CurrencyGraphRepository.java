package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.Currency;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 9/1/17.
 */
@Repository
public interface CurrencyGraphRepository extends Neo4jBaseRepository<Currency,Long> {

    @Query("Match (n:Currency{deleted:false})-[:RELATED_TO]->(basic_details:Country) where id(basic_details)={0} return {id:id(n),name:n.name, currencyCode:n.currencyCode } as result")
    List<Map<String,Object>> getCurrencies(long countryId);
    @Query("Match (n:Currency{deleted:false})-[:RELATED_TO]->(basic_details:Country) where id(basic_details)={0} return n limit 1")
    Currency findFirstByCountryIdAndDeletedFalse(Long countryId);

}

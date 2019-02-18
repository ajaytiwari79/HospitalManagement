package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.Currency;
import com.kairos.persistence.model.country.default_data.CurrencyDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by prabjot on 9/1/17.
 */
@Repository
public interface CurrencyGraphRepository extends Neo4jBaseRepository<Currency,Long> {

    @Query("MATCH (country:Country)<-[:"+ BELONGS_TO +"]-(currency:Currency {deleted:false}) where id(country)={0} " +
            "RETURN id(currency) as id, currency.name as name, currency.description as description, currency.currencyCode as currencyCode ORDER BY currency.creationDate DESC")
    List<CurrencyDTO> findCurrencyByCountry(long countryId);


    @Query("Match (n:Currency{deleted:false})-[:"+ BELONGS_TO +"]->(country:Country) where id(country)={0} return n limit 1")
    Currency findFirstByCountryIdAndDeletedFalse(Long countryId);


    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(currency:Currency {deleted:false}) WHERE id(country)={0} AND id(currency)<>{3} AND (currency.name =~{1} OR currency.currencyCode={2}) " +
            " WITH count(currency) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean currencyExistInCountryByNameOrCode(Long countryId, String name, String currencyCode, Long currentCurrencyId);

}

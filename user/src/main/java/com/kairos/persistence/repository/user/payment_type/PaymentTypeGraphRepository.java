package com.kairos.persistence.repository.user.payment_type;
import com.kairos.persistence.model.country.default_data.PaymentType;
import com.kairos.persistence.model.country.default_data.PaymentTypeDTO;
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
public interface PaymentTypeGraphRepository extends Neo4jBaseRepository<PaymentType,Long>{

    @Query("MATCH (country:Country)<-[:"+ BELONGS_TO +"]-(paymentType:PaymentType {isEnabled:true}) where id(country)={0} " +
            "RETURN id(paymentType) as id, paymentType.name as name, paymentType.description as description ORDER BY paymentType.creationDate DESC")
    List<PaymentTypeDTO> findPaymentTypeByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(paymentType:PaymentType {isEnabled:true}) WHERE id(country)={0} AND id(paymentType)<>{2} AND paymentType.name =~{1}  " +
            " WITH count(paymentType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean paymentTypeExistInCountryByName(Long countryId, String name, Long currentPaymentTypeId);
}

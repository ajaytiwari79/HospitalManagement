package com.kairos.persistence.repository.user.payment_type;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.payment_type.PaymentType;

/**
 * Created by prabjot on 9/1/17.
 */
@Repository
public interface PaymentTypeGraphRepository extends GraphRepository<PaymentType>{

    @Query("Match (n:PaymentType{isEnabled:true})-[:BELONGS_TO]->(country:Country) where id(country)={0} return {id:id(n),name:n.name,description:n.description} as data")
    List<Map<String,Object>> getPaymentTypes(long countryId);
}

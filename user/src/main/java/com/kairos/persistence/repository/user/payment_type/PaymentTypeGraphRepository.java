package com.kairos.persistence.repository.user.payment_type;
import com.kairos.persistence.model.user.payment_type.PaymentType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 9/1/17.
 */
@Repository
public interface PaymentTypeGraphRepository extends Neo4jBaseRepository<PaymentType,Long>{

    @Query("Match (n:PaymentType{isEnabled:true})-[:BELONGS_TO]->(basic_details:Country) where id(basic_details)={0} return {id:id(n),name:n.name,description:n.description} as data")
    List<Map<String,Object>> getPaymentTypes(long countryId);
}

package com.kairos.persistence.repository.user.country;


import com.kairos.persistence.model.enums.ReasonCodeType;
import com.kairos.persistence.model.user.country.ReasonCode;
import com.kairos.persistence.model.user.country.ReasonCodeResponseDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by pavan on 23/3/18.
 */
@Repository
public interface ReasonCodeGraphRepository extends Neo4jBaseRepository<ReasonCode,Long> {

    @Query("MATCH (country:Country)-[:"+BELONGS_TO+"]-(reasonCode:ReasonCode{deleted:false}) where id(country)={0} AND reasonCode.reasonCodeType={1} return id(reasonCode) as id, reasonCode.name as name," +
            "reasonCode.code as code, reasonCode.description as description,reasonCode.reasonCodeType as reasonCodeType ORDER BY id")
    List<ReasonCodeResponseDTO> findReasonCodesByCountry(long countryId,ReasonCodeType reasonCodeType);

    @Query("MATCH (country:Country)-[:"+BELONGS_TO+"]-(reasonCode:ReasonCode{deleted:false}) where id(country)={0} AND id(reasonCode) <> {1} AND reasonCode.name=~{2} AND reasonCode.reasonCodeType={3}" +
            "with count(reasonCode) as reasonCodeCount return CASE when reasonCodeCount>0 THEN  true ELSE false END as response ")
    boolean findByNameExcludingCurrent(Long countryId, Long reasonCodeId, String name, ReasonCodeType reasonCodeType);
}


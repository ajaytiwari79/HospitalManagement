package com.kairos.persistence.repository.user.country;


import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by pavan on 23/3/18.
 */
@Repository
public interface ReasonCodeGraphRepository extends Neo4jBaseRepository<ReasonCode, Long> {

    @Query("MATCH (country:Country)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) where id(country)={0} AND reasonCode.reasonCodeType={1} return id(reasonCode) as id, reasonCode.name as name," +
            "reasonCode.code as code, reasonCode.description as description,reasonCode.reasonCodeType as reasonCodeType,reasonCode.timeTypeId as timeTypeId ORDER BY reasonCode.creationDate DESC")
    List<ReasonCodeResponseDTO> findReasonCodesByCountry(long countryId, ReasonCodeType reasonCodeType);

    @Query("MATCH (country:Country)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) where id(country)={0} AND id(reasonCode) <> {1} AND reasonCode.name=~{2} AND reasonCode.reasonCodeType={3}" +
            "with count(reasonCode) as reasonCodeCount return CASE when reasonCodeCount>0 THEN  true ELSE false END as response ")
    boolean findByNameExcludingCurrent(Long countryId, Long reasonCodeId, String name, ReasonCodeType reasonCodeType);

    @Query("MATCH (country:Country)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) where id(country)={0} AND id(reasonCode)= {1} return reasonCode")
    ReasonCode findByCountryAndReasonCode(long countryId, long reasonCodeId);

    @Query("MATCH (organization:Unit)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) where id(organization)={0} AND reasonCode.reasonCodeType={1} return id(reasonCode) as id, reasonCode.name as name," +
            "reasonCode.code as code, reasonCode.description as description,reasonCode.reasonCodeType as reasonCodeType,reasonCode.timeTypeId as timeTypeId ORDER BY reasonCode.creationDate  DESC")
    List<ReasonCodeResponseDTO> findReasonCodesByUnitIdAndReasonCodeType(long unitId, ReasonCodeType reasonCodeType);


    @Query("MATCH (organization:Organization)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) where id(organization)={0} return id(reasonCode) as id, reasonCode.name as name," +
            "reasonCode.code as code, reasonCode.description as description,reasonCode.reasonCodeType as reasonCodeType,reasonCode.timeTypeId as timeTypeId ORDER BY reasonCode.creationDate  DESC")
    List<ReasonCodeResponseDTO> findReasonCodeByUnitId(long orgId);

    @Query("MATCH (organization:Unit)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) where id(organization)={0} AND id(reasonCode) <> {1} AND reasonCode.name=~{2} AND reasonCode.reasonCodeType={3}" +
            "with count(reasonCode) as reasonCodeCount return CASE when reasonCodeCount>0 THEN  true ELSE false END as response ")
    boolean findByUnitIdAndNameExcludingCurrent(Long unitId, Long reasonCodeId, String name, ReasonCodeType reasonCodeType);

    @Query("MATCH (organization:Unit)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) where id(organization)={0} AND id(reasonCode)= {1} return reasonCode")
    ReasonCode findByUnitIdAndReasonCode(long unitId, long reasonCodeId);

    @Query("MATCH(reasonCode:ReasonCode{deleted:false}) WHERE id(reasonCode) IN {0} RETURN  reasonCode")
    List<ReasonCode> findByIds(Set<Long> reasonCodeIds);

    @Query("MATCH(reasonCode:ReasonCode{deleted:false}) where reasonCode.timeTypeId=toString({0}) RETURN CASE WHEN COUNT(reasonCode)>0 THEN TRUE ELSE FALSE END AS result")
    boolean existsByTimeTypeIdAndDeletedFalse(BigInteger timeTypeId);


}


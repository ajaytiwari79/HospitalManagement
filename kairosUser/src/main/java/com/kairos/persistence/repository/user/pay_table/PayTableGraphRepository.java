package com.kairos.persistence.repository.user.pay_table;

import com.kairos.persistence.model.user.pay_table.*;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 26/12/17.
 */
@Repository
public interface PayTableGraphRepository extends Neo4jBaseRepository<PayTable, Long> {

    @Query("MATCH (level:Level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false,published:true}) where id(level)={0} AND id(payTable)<>{1}" +
            " RETURN id(payTable) as id,payTable.name as name,payTable.published as published,payTable.startDateMillis as startDateMillis,payTable.endDateMillis as endDateMillis,payTable.description as description,payTable.shortName as shortName")
    List<PayTableResponse> findPayTableByOrganizationLevel(Long organizationLevelId, Long payTableToExclude);

    @Query("MATCH (c:Country) where id(c)={0}\n" +
            " MATCH(c)-[:HAS_LEVEL]->(level:Level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false,hasTempCopy:false}) where (payTable.name =~{2} OR payTable.shortName=~{3}) AND id(payTable)<>{1} " +
            " with count(payTable) as payTableCount\n" +
            " RETURN case when payTableCount>0 THEN  true ELSE false END as response")
    Boolean checkPayTableNameAlreadyExitsByNameOrShortName(Long countryId, Long currentPayTableId, String payTableName, String payTableShortName);

    @Query("MATCH (c:Country) where id(c)={0} \n" +
            "MATCH(c)-[:HAS_LEVEL]->(level:Level{isEnabled:true})\n" +
            "OPTIONAL MATCH (level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false,hasTempCopy:false})\n" +
            "with level,Case when payTable IS NOT NULL THEN collect({id:id(payTable),name:payTable.name,published:payTable.published,startDateMillis:payTable.startDateMillis,endDateMillis:payTable.endDateMillis,description:payTable.description,shortName:payTable.shortName}) else [] end as payTables\n" +
            "with level,payTables \n OPTIONAL MATCH  (level:Level)-[:IN_LEVEL]-(payGroupArea:PayGroupArea{deleted:false})\n" +
            "return id(level) as id,level.name as name ,level.description as description, Case when payGroupArea IS NOT NULL THEN \n" +
            "collect({ payGroupAreaId:id(payGroupArea),name:payGroupArea.name}) else [] end as payGroupAreas,payTables as payTables")
    List<OrganizationLevelPayTableDTO> getOrganizationLevelWisePayTables(Long countryId);

    @Query("MATCH (payTable:PayTable{deleted:false})-[:" + HAS_PAY_GRADE + "]->(payGrade:PayGrade{deleted:false}) where id(payTable)={0} AND payGrade.payGradeLevel={1} "
            + "with count(payGrade) as payGradeCount \n" +
            " RETURN case when payGradeCount>0 THEN  true ELSE false END as response")
    Boolean checkPayGradeLevelAlreadyExists(Long payTableId, Long payGradeLevel);

    @Query("MATCH (payTable:PayTable{deleted:false})-[:" + HAS_PAY_GRADE + "]->(payGrade:PayGrade{deleted:false}) where id(payTable)={0} \n" +
            "Match(payGrade)-[rel:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea{deleted:false})\n" +
            "return id(payTable) as payTableId,id(payGrade) as payGradeId,payGrade.payGradeLevel as payGradeLevel,payGrade.published as published," +
            "collect({id:id(rel),payGroupAreaId:id(pga),payGroupAreaAmount:rel.payGroupAreaAmount}) as payGroupAreas ")
    List<PayGradeResponse> getPayGradesByPayTableId(Long payTableId);

    @Query("MATCH (payTable:PayTable{deleted:false})-[rel:" + HAS_TEMP_PAY_TABLE + "]-(payTable1:PayTable{deleted:false}) where id(payTable)={0} \n" +
            " set payTable.endDateMillis={1} set payTable.hasTempCopy=false set payTable.published=true detach delete rel")
    void changeStateOfRelationShip(Long payTableId, Long endDateMillis);

    @Query("MATCH (payTable:PayTable{deleted:false})-[:" + HAS_TEMP_PAY_TABLE + "]-(payTable1:PayTable{deleted:false}) where id(payTable)={0} \n" +
            "return payTable1")
    PayTable getPermanentPayTableByPayTableId(Long payTableId);

    @Query("MATCH (level:Level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false,published:true}) where id(level)={0}" +
            "OPTIONAL MATCH(payTable)-[:" + HAS_PAY_GRADE + "]->(payGrade:PayGrade{deleted:false})\n" +
            "RETURN id(payTable) as id,payTable.startDateMillis as startDateMillis,payTable.endDateMillis as endDateMillis," +
            " payTable.name as name ,collect({id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}) as payGrades")
    List<PayTableResponse> findActivePayTableByOrganizationLevel(Long organizationLevelId, Long startDate);


}
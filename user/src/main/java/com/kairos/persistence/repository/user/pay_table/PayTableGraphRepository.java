package com.kairos.persistence.repository.user.pay_table;

import com.kairos.persistence.model.pay_table.OrganizationLevelPayGroupAreaDTO;
import com.kairos.persistence.model.pay_table.PayGradeResponse;
import com.kairos.persistence.model.pay_table.PayTable;
import com.kairos.persistence.model.pay_table.PayTableResponse;
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

    @Query("MATCH (level:Level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false}) where id(level)={0} AND id(payTable)<>{1}" +
            " RETURN payTable ORDER BY payTable.startDateMillis DESC LIMIT 1")
    PayTable findPayTableByOrganizationLevel(Long organizationLevelId, Long payTableToExclude);

    @Query("MATCH (c:Country) where id(c)={0}\n" +
            " MATCH(c)-[:HAS_LEVEL]->(level:Level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false}) where (payTable.name =~{2}) AND id(payTable)<>{1} " +
            " with count(payTable) as payTableCount\n" +
            " RETURN case when payTableCount>0 THEN  true ELSE false END as response")
    Boolean checkPayTableNameAlreadyExitsByName(Long countryId, Long currentPayTableId, String payTableName);

    @Query("MATCH (c:Country) where id(c)={0} \n" +
            "MATCH(c)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true,deleted:false})\n" +
            "OPTIONAL MATCH (level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false})\n" +
            "with level,count(payTable) as totalPayTable \n" +
            "OPTIONAL MATCH  (level:Level)-[:"+IN_LEVEL+"]-(payGroupArea:PayGroupArea{deleted:false})\n" +
            "return level.translations as translations,\n" +
            "id(level) as id,level.name as name ,level.description as description, Case when payGroupArea IS NOT NULL THEN \n" +
            "collect({ payGroupAreaId:id(payGroupArea),name:payGroupArea.name}) else [] end as payGroupAreas,totalPayTable as payTablesCount ORDER BY name")
    List<OrganizationLevelPayGroupAreaDTO> getOrganizationLevelWisePayGroupAreas(Long countryId);

    @Query("MATCH (payTable:PayTable{deleted:false})-[:" + HAS_PAY_GRADE + "]->(payGrade:PayGrade{deleted:false}) where id(payTable)={0} AND payGrade.payGradeLevel={1} "
            + "with count(payGrade) as payGradeCount \n" +
            " RETURN case when payGradeCount>0 THEN  true ELSE false END as response")
    Boolean checkPayGradeLevelAlreadyExists(Long payTableId, Long payGradeLevel);

    @Query("MATCH (payTable:PayTable{deleted:false})-[:"+HAS_PAY_GRADE+"]->(payGrade:PayGrade{deleted:false}) WHERE id(payTable)={0}\n" +
            "MATCH(payGrade)-[rel:"+HAS_PAY_GROUP_AREA+"]-(pga:PayGroupArea{deleted:false})\n" +
            "RETURN id(payTable) as payTableId,id(payGrade) as payGradeId,payGrade.payGradeLevel as payGradeLevel,payGrade.published as published,collect({id:id(rel),payGroupAreaId:id(pga),payGroupAreaAmount:rel.payGroupAreaAmount}) as payGroupAreas ORDER BY payGradeLevel")
    List<PayGradeResponse> getPayGradesByPayTableId(Long payTableId);

    @Query("MATCH (payTable:PayTable{deleted:false})-[rel:" + HAS_TEMP_PAY_TABLE + "]-(payTable1:PayTable{deleted:false}) where id(payTable)={0} \n" +
            " set payTable.endDateMillis={1} set payTable.published=true detach delete rel")
    void changeStateOfRelationShip(Long payTableId, String endDateMillis);

    @Query("MATCH (payTable:PayTable{deleted:false})-[:" + HAS_TEMP_PAY_TABLE + "]-(payTable1:PayTable{deleted:false}) where id(payTable)={0} \n" +
            "return payTable1")
    PayTable getPermanentPayTableByPayTableId(Long payTableId);

    @Query("MATCH (level:Level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false,published:true}) where id(level)={0} AND DATE(payTable.startDateMillis) <= DATE({1}) AND (payTable.endDateMillis IS NULL OR DATE(payTable.endDateMillis) >= DATE({1}))" +
            "OPTIONAL MATCH(payTable)-[:" + HAS_PAY_GRADE + "]->(payGrade:PayGrade{deleted:false})\n" +
            "RETURN id(payTable) as id,payTable.startDateMillis as startDateMillis,payTable.endDateMillis as endDateMillis," +
            " payTable.name as name ,payTable.percentageValue as percentageValue,collect({id:id(payGrade),payGradeLevel:payGrade.payGradeLevel}) as payGrades")
    List<PayTableResponse> findActivePayTablesByOrganizationLevel(Long organizationLevelId, String startDate);

    @Query("MATCH (payTable:PayTable)<-[rel:" + HAS_TEMP_PAY_TABLE + "]-(parentPayTable:PayTable{deleted:false}) where id(payTable)={0}  detach delete rel \n" +
            " set parentPayTable.editable=true " +
            "RETURN id(parentPayTable) as id,parentPayTable.name as name,parentPayTable.published as published,parentPayTable.startDateMillis as startDateMillis,parentPayTable.endDateMillis as endDateMillis,parentPayTable.description as description,parentPayTable.shortName as shortName, parentPayTable.paymentUnit as paymentUnit ORDER BY startDateMillis DESC LIMIT 1")
    PayTableResponse getParentPayTableByPayTableId(Long payTableId);

    @Query("MATCH (level:Level)<-[:" + IN_ORGANIZATION_LEVEL + "]-(payTable:PayTable{deleted:false}) where id(level)={0} " +
            "RETURN payTable.translations as translations,\n" +
            "id(payTable) as id,payTable.name as name,payTable.published as published,payTable.percentageValue as percentageValue,payTable.editable as editable,payTable.startDateMillis as startDateMillis,payTable.endDateMillis as endDateMillis,payTable.description as description,payTable.shortName as shortName, payTable.paymentUnit as paymentUnit ORDER BY startDateMillis,payTable.creationDate")
    List<PayTableResponse> findActivePayTablesByOrganizationLevel(Long organizationLevelId);

    @Query("MATCH(pt:PayTable)-[:"+IN_ORGANIZATION_LEVEL+"]-(level:Level) WHERE id(pt)={0} \n" +
            "MATCH(level)-[:"+IN_ORGANIZATION_LEVEL+"]-(payTables:PayTable{deleted:false}) WHERE DATE(payTables.startDateMillis)<=DATE({1}) AND (payTables.endDateMillis IS NULL OR DATE(payTables.endDateMillis)>=DATE({1})) AND id(payTables)<>{0}\n" +
            "RETURN CASE WHEN COUNT(payTables)>0 then true else false end as result")
    boolean existsByDate(Long id,String publishedDate);

    @Query("MATCH(level:Level)<-[:"+IN_ORGANIZATION_LEVEL+"]-(payTable:PayTable{published:true}) \n" +
            "WHERE id(level)={0} \n" +
            "AND  (payTable.endDateMillis IS NULL OR (DATE(payTable.endDateMillis)) >= DATE({3})) \n" +
            "AND (( {2} IS NULL AND (payTable.endDateMillis IS NULL OR DATE(payTable.endDateMillis) >= DATE({1}))) \n" +
            "OR ({2} IS NOT NULL AND  (DATE({2}) >= DATE(payTable.startDateMillis) AND (payTable.endDateMillis is null OR DATE({1}) <= DATE(payTable.endDateMillis)) ))) \n" +
            "RETURN payTable")
    List<PayTable> findAllActivePayTable(Long levelId,String startDate,String endDate,String employmentStartDate);
}
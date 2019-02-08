package com.kairos.persistence.repository.organization.union;

import java.util.List;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.IN_COUNTRY;

@Repository
public interface SectorGraphRepository extends Neo4jBaseRepository<Sector, Long> {

    @Query("MATCH(sector:Sector)-[:"+IN_COUNTRY+"]->(country:Country) WHERE id(country)={0} RETURN sector")
    List<Sector> findAllSectorsByCountryAndDeletedFalse(Long countryId);

    @Query("MATCH(sector:Sector{deleted:false}) WHERE id(sector)={0} RETURN sector")
    Sector findSectorById(Long sectorId);

    @Query("MATCH(sector:Sector{deleted:false}) WHERE id(sector)<>{1} AND sector.name=~{0} RETURN count(sector)>0")
    boolean existsByName(String name,Long sectorId);

    @Query("MATCH(sector:Sector{deleted:false}) WHERE id(sector) IN {0} RETURN sector")
    List<Sector> findSectorsById(List<Long> sectorIds);

}
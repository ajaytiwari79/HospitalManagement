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

    @Query("match(sector:Sector)-[:"+IN_COUNTRY+"]->(country:Country) where id(country)={0} return sector")
    List<Sector> findAllSectorsByCountryAndDeletedFalse(Long countryId);

    @Query("Match(sector:Sector{deleted:false}) where id(sector)={0} return sector")
    Sector findSectorById(Long sectorId);

    @Query("Match(sector:Sector{deleted:false}) where sector.name={0} return count(sector)>0")
    boolean existsByName(String name);

    @Query("Match(sector:Sector{deleted:false}) where id(sector) in {0} return sector")
    List<Sector> findSectorsById(List<Long> sectorIds);

}
package com.kairos.persistence.repository.master_data.processing_activity_masterdata;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
//TODO make generic for metadata only and create a generic for rest of the application

@NoRepositoryBean
public interface CustomGenericRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.countryId = ?1 and EN.deleted = false and lower(EN.name) IN ?2")
    List<T>  findByCountryIdAndDeletedAndNameIn(Long countryId, List<String> userNames);

    @Query(value = "SELECT LOWER(EN.name) FROM #{#entityName} EN WHERE EN.countryId = ?1 and EN.deleted = false")
    Set<String>  findNameByCountryIdAndDeleted(Long countryId);

    @Query(value = "SELECT LOWER(EN.name) FROM #{#entityName} EN WHERE EN.organizationId = ?1 and EN.deleted = false")
    Set<String>  findNameByOrganizationIdAndDeleted(Long orgId);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set name = ?1 where id= ?2 and countryId = ?3")
    Integer updateMasterMetadataName(String name, Long id , Long countryId);


    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.id = ?1 and EN.countryId = ?2 and EN.deleted = false")
    T findByIdAndCountryIdAndDeletedFalse(Long id, Long countryId);
    

    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.countryId = ?1 and EN.deleted = false and lower(EN.name) = lower(?2)")
    T findByCountryIdAndName(Long countryId, String name);



    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.organizationId = ?1 and EN.deleted = ?2 and lower(EN.name) IN ?3")
    List<T> findByOrganizationIdAndDeletedAndNameIn(Long orgId, boolean deleted, List<String> name);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateMetadataName(String name, Long id, Long orgId);

    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set suggestedDataStatus = ?3 where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateMetadataStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);


    @Transactional
    @Modifying
    @Query(value = "update #{#entityName} set deleted = true where id = ?1 and organizationId = ?2")
    Integer deleteByIdAndOrganizationId(Long id, Long orgId);

    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.id = ?1 and EN.organizationId = ?2 and EN.deleted = false")
    T findByIdAndOrganizationIdAndDeletedFalse(Long id, Long orgId);


    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.organizationId = ?1 and EN.deleted = false and lower(EN.name) = lower(?2)")
    T findByOrganizationIdAndDeletedAndName(Long orgId, String name);

    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.id IN (?1) and EN.deleted = false")
    List<T>  findAllByIds( Set<Long> ids);

    @Query(value = "Select EN from #{#entityName} EN WHERE EN.organizationId = ?1 and EN.deleted = false order by EN.createdAt desc")
    List<T> findAllByOrganizationId(Long orgId);

    @Query(value = "Select EN from #{#entityName} EN WHERE EN.countryId = ?1 and EN.deleted = false order by EN.createdAt desc")
    List<T> findAllByCountryId(Long countryId);

    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.deleted = false and EN.id = ?1")
    T findByIdAndDeletedFalse(Long id);

    @Query(value = "SELECT EN FROM #{#entityName} EN WHERE EN.id IN (?1) and EN.deleted = false")
    List<T>  findAllByIds( List<Long> ids);
}

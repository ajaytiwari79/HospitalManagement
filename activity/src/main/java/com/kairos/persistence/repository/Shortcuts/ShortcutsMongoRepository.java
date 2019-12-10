package com.kairos.persistence.repository.Shortcuts;

import com.kairos.dto.activity.ShortCuts.ShortcutDTO;
import com.kairos.persistence.model.shortcuts.Shortcut;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ShortcutsMongoRepository extends MongoRepository<Shortcut, BigInteger> {

    @Query(value = "{deleted:false,id:?0}")
    ShortcutDTO findShortcutById(BigInteger shortcutId);

    @Query(value = "{deleted:false,staffId:?0,unitId:?1}")
    List<ShortcutDTO> findShortcutByUnitIdAndStaffId(Long staffId , Long unitId);

    @Query(value = "{deleted:false,staffId:?0,unitId:?1,name:?2}")
    ShortcutDTO findShortcutByUnitIdAndStaffIdAndName(Long staffId , Long unitId , String name);

    boolean existsByNameIgnoreCaseAndDeletedFalseAndStaffIdAndUnitIdAndIdNot(String name, Long staffId , Long unitId, BigInteger id);

}

package com.kairos.persistence.repository.Shortcuts;

import com.kairos.dto.activity.ShortCuts.ShortcutsDTO;
import com.kairos.persistence.model.shortcuts.Shortcuts;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ShortcutsMongoRepository extends MongoRepository<Shortcuts, BigInteger> {

    @Query(value = "{deleted:false,id:?0}")
    ShortcutsDTO findShortcutById(BigInteger shortcutId);

    @Query(value = "{deleted:false,staffId:?0,unitId:?1}")
    List<ShortcutsDTO> findShortcutByUnitIdAndStaffId(Long staffId , Long unitId);

    @Query(value = "{deleted:false,staffId:?0,unitId:?1,name:?2}")
    ShortcutsDTO findShortcutByUnitIdAndStaffIdAndName(Long staffId , Long unitId , String name);

}

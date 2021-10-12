package com.kairos.persistence.repository.keyboard_shortcuts;

import com.kairos.persistence.model.keyboard_shortcuts.KeyboardShortcuts;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
@Repository
public interface KeyboardShortcutsRepository extends MongoBaseRepository<KeyboardShortcuts,BigInteger> {

    @Query(value = "{_id:?0 ,countryId:?1 ,deleted:false}")
    public KeyboardShortcuts findKeyboardShortcutsByIdAndCountryId(BigInteger id, BigInteger countryId);

    @Query(value = "{_id:?0 ,unitId:?1 ,deleted:false}")
    public KeyboardShortcuts findKeyboardShortcutsByIdAndUnitId(BigInteger id, BigInteger unitId);




}

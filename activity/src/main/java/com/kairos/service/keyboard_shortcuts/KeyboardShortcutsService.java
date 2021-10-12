package com.kairos.service.keyboard_shortcuts;

import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.persistence.model.keyboard_shortcuts.KeyboardShortcuts;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.keyboard_shortcuts.KeyboardShortcutsRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_COUNTRY_PHASE_SEQUENCE;

@Service
public class KeyboardShortcutsService {
    @Inject
    private KeyboardShortcutsRepository keyboardShortcutsRepository;
    @Inject
    private ExceptionService exceptionService;

    public KeyboardShortcuts createShortcutKeyInCountry(BigInteger countryId, KeyboardShortcuts keyboardShortcuts) {
        KeyboardShortcuts key = keyboardShortcutsRepository.findKeyboardShortcutsByIdAndCountryId(keyboardShortcuts.getId(), countryId);
        if (isNotNull(key)) {

            exceptionService.dataNotFoundByIdException("All ready exists with CountryId is", key.getCountryId());
        }

        return keyboardShortcutsRepository.save(keyboardShortcuts);
    }

    public KeyboardShortcuts createShortcutKeyInUnit(BigInteger unitId, KeyboardShortcuts keyboardShortcuts) {
        KeyboardShortcuts key = keyboardShortcutsRepository.findKeyboardShortcutsByIdAndUnitId(keyboardShortcuts.getId(), unitId);
        if (isNotNull(key)) {

            exceptionService.dataNotFoundByIdException("All ready exists with UnitId is", key.getUnitId());
        }

        return keyboardShortcutsRepository.save(keyboardShortcuts);
    }

    public KeyboardShortcuts updateShortcutKeyInCountry(BigInteger countryId, KeyboardShortcuts keyboardShortcuts)
    {
        return keyboardShortcutsRepository.save(keyboardShortcuts);
    }

    public KeyboardShortcuts updateShortcutKeyInUnit(BigInteger unitId,KeyboardShortcuts keyboardShortcuts)
    {
        return keyboardShortcutsRepository.save(keyboardShortcuts);
    }

    public KeyboardShortcuts getShortcutKeyInCountry(BigInteger id, BigInteger countryId)
    {
        return keyboardShortcutsRepository.findKeyboardShortcutsByIdAndCountryId(id, countryId);
    }

    public KeyboardShortcuts getShortcutKeyInUnit(BigInteger id, BigInteger unitId)
    {
        return keyboardShortcutsRepository.findKeyboardShortcutsByIdAndUnitId(id, unitId);
    }

    public KeyboardShortcuts deleteShortcutKeyInCountry(BigInteger id, BigInteger countryId)
    {
          KeyboardShortcuts keyboardShortcuts = keyboardShortcutsRepository.findKeyboardShortcutsByIdAndCountryId(id, countryId);
          keyboardShortcuts.setDeleted(true);
          //keyboardShortcutsRepository.deleteById(id);
        return keyboardShortcutsRepository.save(keyboardShortcuts);
    }

    public KeyboardShortcuts deleteShortcutKeyInUnit(BigInteger id, BigInteger unitId)
    {
        KeyboardShortcuts keyboardShortcuts = keyboardShortcutsRepository.findKeyboardShortcutsByIdAndUnitId(id, unitId);
        keyboardShortcuts.setDeleted(true);
        //keyboardShortcutsRepository.deleteById(id);
        return keyboardShortcutsRepository.save(keyboardShortcuts);
    }
}

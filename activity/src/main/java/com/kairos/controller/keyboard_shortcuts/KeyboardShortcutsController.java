package com.kairos.controller.keyboard_shortcuts;

import com.kairos.dto.activity.ShortCuts.ShortcutDTO;
import com.kairos.persistence.model.keyboard_shortcuts.KeyboardShortcuts;
import com.kairos.service.keyboard_shortcuts.KeyboardShortcutsService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;


@RestController
@RequestMapping(API_V1)
@Api(API_V1)

public class KeyboardShortcutsController {

    @Inject
    private KeyboardShortcutsService keyboardShortcutsService;

    @PostMapping(value = COUNTRY_URL+"/keyboardShortcuts")
    public ResponseEntity<Map<String, Object>> createShortcutKeyInCountry(@PathVariable BigInteger countryId, @RequestBody KeyboardShortcuts keyboardShortcuts) {
        keyboardShortcuts.setCountryId(countryId);
        keyboardShortcuts.setDeleted(false);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,keyboardShortcutsService.createShortcutKeyInCountry(countryId, keyboardShortcuts));
    }

    @PutMapping(value = COUNTRY_URL+"/keyboardShortcuts/{id}")
    public ResponseEntity<Map<String, Object>> updateShortcutKeyInCountry(@PathVariable BigInteger id,@RequestBody KeyboardShortcuts keyboardShortcuts) {
        keyboardShortcuts.setId(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, keyboardShortcutsService.updateShortcutKeyInCountry(keyboardShortcuts.getCountryId(), keyboardShortcuts));
    }

    @GetMapping(value = COUNTRY_URL+"/keyboardShortcuts/{id}")
    public ResponseEntity<Map<String, Object>> getShortcutKeyInCountry(@PathVariable BigInteger id, @PathVariable BigInteger countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, keyboardShortcutsService.getShortcutKeyInCountry(id, countryId));
    }

    @DeleteMapping(value = COUNTRY_URL+"/keyboardShortcuts/{id}")
    public ResponseEntity<Map<String, Object>> deleteShortcutKeyInCountry(@PathVariable BigInteger id, @PathVariable BigInteger countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, keyboardShortcutsService.deleteShortcutKeyInCountry(id, countryId));
    }

    //=====================================unit based=================================

    @PostMapping(value = UNIT_URL+"/keyboardShortcuts")
    public ResponseEntity<Map<String, Object>> createShortcutKeyInUnit(@PathVariable BigInteger unitId, @RequestBody KeyboardShortcuts keyboardShortcuts) {
        keyboardShortcuts.setUnitId(unitId);
        keyboardShortcuts.setDeleted(false);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,keyboardShortcutsService.createShortcutKeyInUnit(unitId, keyboardShortcuts));
    }

    @PutMapping(value = UNIT_URL+"/keyboardShortcuts/{id}")
    public ResponseEntity<Map<String, Object>> updateShortcutKeyInUnit(@PathVariable BigInteger id, @RequestBody KeyboardShortcuts keyboardShortcuts) {
        keyboardShortcuts.setId(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, keyboardShortcutsService.updateShortcutKeyInUnit(keyboardShortcuts.getUnitId(), keyboardShortcuts));
    }

    @GetMapping(value = UNIT_URL+"/keyboardShortcuts/{id}")
    public ResponseEntity<Map<String, Object>> getShortcutKeyInUnit(@PathVariable BigInteger unitId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, keyboardShortcutsService.getShortcutKeyInUnit(id, unitId));
    }

    @DeleteMapping(value = UNIT_URL+"/keyboardShortcuts/{id}")
    public ResponseEntity<Map<String, Object>> deleteShortcutKeyInUnit(@PathVariable BigInteger unitId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, keyboardShortcutsService.deleteShortcutKeyInUnit(id, unitId));
    }
}

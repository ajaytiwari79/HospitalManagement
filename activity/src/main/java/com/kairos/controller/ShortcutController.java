package com.kairos.controller;

import com.kairos.dto.activity.ShortCuts.ShortcutDTO;
import com.kairos.service.Shortcuts.ShortcutService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL)
@Api(API_UNIT_URL)
public class ShortcutController {

@Inject
private ShortcutService shortcutService;

    @ApiOperation("get shortcuts")
    @GetMapping("/shortcut/{shortcutId}")
    public ResponseEntity<Map<String, Object>> getShortcutById(@PathVariable BigInteger shortcutId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutService.getShortcutById(shortcutId));
    }

    @ApiOperation("get shortcuts")
    @DeleteMapping("/shortcut/{shortcutId}")
    public ResponseEntity<Map<String, Object>> deleteShortcutById(@PathVariable BigInteger shortcutId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutService.deleteShortcutById(shortcutId));
    }

    @ApiOperation("get shortcuts")
    @PostMapping("/copy_shortcut/{shortcutId}")
    public ResponseEntity<Map<String, Object>> createCopyOfShortcut(@PathVariable BigInteger shortcutId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutService.createCopyOfShortcut(shortcutId,name));
    }

    @ApiOperation("update shortcuts")
    @PutMapping("/shortcut/{shortcutId}")
    public ResponseEntity<Map<String, Object>> updateShortcutById(@PathVariable BigInteger shortcutId, @RequestBody ShortcutDTO shortcutDTO, @RequestParam(required = false , value = "name") String name ) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutService.updateShortcut(shortcutId,name, shortcutDTO));
    }

    @ApiOperation("get all shortcuts")
    @GetMapping("/staffId/{staffId}/shortcut/all")
    public ResponseEntity<Map<String, Object>> getAllShortcut(@PathVariable Long unitId,@PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutService.getAllShortcutByStaffIdAndUnitId(unitId,staffId));
    }

    @ApiOperation("save shortcut")
    @PostMapping("/shortcut")
    public ResponseEntity<Map<String, Object>> saveShortcut(@PathVariable Long unitId, @RequestBody ShortcutDTO shortcutDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutService.saveShortcut(shortcutDTO));
    }


}

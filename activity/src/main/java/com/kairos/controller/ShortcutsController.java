package com.kairos.controller;

import com.kairos.dto.activity.ShortCuts.ShortcutsDTO;
import com.kairos.service.Shortcuts.ShortcutsService;
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
public class ShortcutsController {

@Inject
private ShortcutsService shortcutsService;

    @ApiOperation("get shortcuts")
    @GetMapping("/shortcut/{shortcutId}")
    public ResponseEntity<Map<String, Object>> getShortcutById(@PathVariable BigInteger shortcutId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutsService.getShortcutById(shortcutId));
    }

    @ApiOperation("update shortcuts")
    @GetMapping("/shortcut")
    public ResponseEntity<Map<String, Object>> updateShortcutById(@RequestBody ShortcutsDTO shortcutsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutsService.updateShortcut(shortcutsDTO));
    }

    @ApiOperation("get all shortcuts")
    @GetMapping("/staffId/{staffId}/shortcut/all")
    public ResponseEntity<Map<String, Object>> getAllShortcut(@PathVariable Long unitId,@PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutsService.getAllShortcutByStaffIdAndUnitId(unitId,staffId));
    }

    @ApiOperation("save shortcut")
    @PostMapping("/shortcut")
    public ResponseEntity<Map<String, Object>> saveShortcut(@PathVariable Long unitId, @RequestBody ShortcutsDTO shortcutsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, shortcutsService.saveShortcut(shortcutsDTO));
    }


}

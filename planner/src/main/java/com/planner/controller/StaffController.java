package com.planner.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/staff")
public class StaffController {
    /*private static Logger logger= LoggerFactory.getLogger(StaffController.class);
    @Autowired
    private StaffService staffService;
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("Create staff")
    public ResponseEntity<Map<String, Object>> addStaffingLevel(@RequestBody StaffBasicDetailsDTO staffDTO,
                                                                @PathVariable Long unitId) {
        staffService.createStaff(unitId,staffDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }

    @RequestMapping(value = "/multiple", method = RequestMethod.POST)
    @ApiOperation("Create bulk staff")
    public ResponseEntity<Map<String, Object>> addStaffingLevel(@RequestBody List<StaffBasicDetailsDTO> staffDTOs,
                                                                @PathVariable Long unitId) {
        staffService.createStaff(unitId,staffDTOs);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{staffingLevelKairosId}", method = RequestMethod.PUT)
    @ApiOperation("update staffing_level")
    public ResponseEntity<Map<String, Object>> updateStaffingLevel(@RequestBody  StaffBasicDetailsDTO staffDTO,
                                                                   @PathVariable Long unitId, @PathVariable Long staffKairosId) {
        staffService.updateStaff(staffKairosId,unitId,staffDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }*/




}

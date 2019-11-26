package com.kairos.controller.country;

import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.skill.OrgTypeSkillDTO;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user.organization.OrganizationBasicDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.expertise.response.ExpertiseSkillDTO;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.service.country.CountryHolidayCalenderService;
import com.kairos.service.country.CountryService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.organization.CompanyCreationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.OrganizationTypeService;
import com.kairos.service.skill.SkillCategoryService;
import com.kairos.service.skill.SkillService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.wrapper.UpdateOrganizationTypeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@RequestMapping(API_V1)
@Api(API_V1)
@RestController
public class CountryController {
    @Inject
    private CountryService countryService;
    @Inject
    private SkillCategoryService skillCategoryService;
    @Inject
    private CountryHolidayCalenderService countryHolidayCalenderService;
    @Inject
    private OrganizationTypeService organizationTypeService;
    @Inject
    private SkillService skillService;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private CompanyCreationService companyCreationService;

    @RequestMapping(value = "/country", method = RequestMethod.POST)
    @ApiOperation("Create a new Country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createCountry(@Validated @RequestBody Country country) {
        if (country != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.createCountry(country));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @RequestMapping(value = "/country", method = RequestMethod.PUT)
    @ApiOperation("Create a new Country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCountry(@Validated @RequestBody Country country) {
        if (country != null) {
            Map<String, Object> createdCountry = countryService.updateCountry(country);
            if (createdCountry != null)
                return ResponseHandler.generateResponse(HttpStatus.OK, true, createdCountry);
            else
                return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @RequestMapping(value = "/country", method = RequestMethod.GET)
    @ApiOperation("Find all Countries")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllCountry() {
        List<Map<String, Object>> countryList = countryService.getAllCountries();
        if (countryList.size() != 0)
            return ResponseHandler.generateResponse(HttpStatus.OK, true, countryList);
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @RequestMapping(value = COUNTRY_URL, method = RequestMethod.GET)
    @ApiOperation("Get country  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryById(countryId));
    }

    @RequestMapping(value = "/countryId/{countryId}", method = RequestMethod.GET)
    @ApiOperation("Get country  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryById(@PathVariable Long countryId) {
        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryById(countryId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @RequestMapping(value = COUNTRY_URL, method = RequestMethod.DELETE)
    @ApiOperation("Delete country  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCountry(@PathVariable Long countryId) {
        if (countryId != null) {
            if (countryService.getCountryById(countryId) != null) {
                countryService.deleteCountry(countryId);
                return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "Get all Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrgTypesByCountryId(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrgTypesByCountryId(countryId));
    }

    @ApiOperation(value = "Add Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrgTypesByCountryId(@PathVariable Long countryId,
                                                                      @Validated @RequestBody OrganizationTypeDTO organizationTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.createOrganizationTypeForCountry(countryId, organizationTypeDTO));
    }

    @ApiOperation(value = "Update Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationType(@Validated @RequestBody UpdateOrganizationTypeDTO updateOrganizationTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.updateOrganizationType(updateOrganizationTypeDTO));
    }

    @ApiOperation(value = "Delete Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type/{organizationTypeId}", method = RequestMethod.DELETE)
//  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteOrganizationSubTypeById(@PathVariable Long organizationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.deleteOrganizationType(organizationTypeId));
    }

    //----// Organization Sub Type
    @ApiOperation(value = "Get all Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type/sub_type/{organizationTypeId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrgTypesSubByCountryId(@PathVariable Long countryId, @PathVariable long organizationTypeId) {
        List<Object> response = organizationTypeService.getOrgSubTypesByTypeId(organizationTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Add Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type/sub_type/{organizationTypeId}", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrgTypesSubByCountryId(@PathVariable Long countryId, @Validated @RequestBody OrganizationType data, @PathVariable long organizationTypeId) {
        Map<String, Object> response = organizationTypeService.addOrganizationTypeSubType(data, organizationTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = COUNTRY_URL + "/skill_category", method = RequestMethod.GET)
    @ApiOperation("Get a skillCategory by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllSkillCategory(@PathVariable Long countryId) {
        if (countryId != null) {
            List<Object> skillCategory = skillCategoryService.getAllSkillCategoryOfCountry(countryId);
            if (skillCategory != null) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true, skillCategory);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @RequestMapping(value = COUNTRY_URL + "/skill_category", method = RequestMethod.POST)
    @ApiOperation("Add a new skillCategory")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSkillCategory(@PathVariable long countryId, @Validated @RequestBody SkillCategory objectToSave) {
        Object response = skillCategoryService.createSkillCategory(countryId, objectToSave);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = COUNTRY_URL + "/skill_category", method = RequestMethod.PUT)
    @ApiOperation("Update a skillCategory  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSkillCategoryById(@RequestBody @Validated SkillCategory skillData, @PathVariable Long countryId) {
        Map<String, Object> updatedSkillCategory = skillCategoryService.updateSkillCategory(skillData, countryId);
        if (updatedSkillCategory == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, updatedSkillCategory);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedSkillCategory);
    }

    @RequestMapping(value = COUNTRY_URL + "/skill_category/{skillCategoryId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete a skillCategory  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteSkillCategoryById(@PathVariable long skillCategoryId) {
        skillCategoryService.deleteSkillCategorybyId(skillCategoryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation(value = "Get  skills by  Category id ")
    @RequestMapping(value = COUNTRY_URL + "/skill_category/{categoryId}/skill", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSkillsByCategoryId(@PathVariable Long categoryId) {
        if (categoryId != null) {
            List<Skill> skillList = skillService.getSkillsByCategoryId(categoryId);
            if (skillList.size() != 0) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true, skillList);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "Add a new skill")
    @RequestMapping(value = "/skill_category/{skillCategoryId}/skill", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSkill(@PathVariable long skillCategoryId, @RequestBody SkillDTO skillDTO) {
        Map<String, Object> response = skillService.createSkill(skillDTO, skillCategoryId);
        if (skillDTO == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Update a skill by id ")
    @RequestMapping(value = COUNTRY_URL + "/skill", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSkillById(@PathVariable long countryId, @RequestBody SkillDTO skillDTO) {
        Map<String, Object> updatedSkill = skillService.updateSkill(countryId, skillDTO);
        if (updatedSkill == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedSkill);
    }

    @ApiOperation(value = "delete skill by id")
    @RequestMapping(value = COUNTRY_URL + "/skill/{skillId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteSkill(@PathVariable long skillId) {
        if (skillService.deleteSkill(skillId)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
    }

    @ApiOperation(value = "Add a Parent Organization")
    @RequestMapping(value = COUNTRY_URL + "/parent_organization", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createParentOrganization(@PathVariable long countryId,
                                                                        @Valid @RequestBody OrganizationBasicDTO organizationBasicDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, companyCreationService.createCompany(organizationBasicDTO, countryId));
    }

    @ApiOperation(value = "Delete Parent Organization or unit ")
    @RequestMapping(value = COUNTRY_URL + "/parent_organization/{parentOrganizationId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteParentOrganization(@PathVariable long parentOrganizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.deleteOrganization(parentOrganizationId));
    }

    @ApiOperation(value = "Create Expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveExpertise(@PathVariable long countryId, @Validated @RequestBody ExpertiseDTO expertise) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, expertiseService.saveExpertise(countryId, expertise));
    }

    @ApiOperation(value = "Get Available expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllExpertise(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getAllExpertise(countryId));
    }

    @ApiOperation(value = "Get all union with Service")
    @RequestMapping(value = COUNTRY_URL + "/union_with_service", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnionsAndService(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getUnionsAndService(countryId));
    }

    @ApiOperation(value = "Update expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertise(@PathVariable long countryId, @RequestBody  ExpertiseDTO expertise,@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateExpertise(countryId, expertise,expertiseId));
    }

    @ApiOperation(value = "Update expertise Line")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/expertise_line/{expertiseLineId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertise(@PathVariable Long countryId, @RequestBody  ExpertiseDTO expertise,@PathVariable Long expertiseId,@PathVariable Long expertiseLineId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateExpertiseLine(countryId, expertise,expertiseId,expertiseLineId));
    }



    @ApiOperation(value = "Delete expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteExpertise(@PathVariable long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.deleteExpertise(expertiseId));
    }

    @ApiOperation(value = "Publish expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/publish", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> publishExpertise(@PathVariable long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.publishExpertise(expertiseId));
    }

    @ApiOperation(value = "Delete expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/seniority_level/{seniorityLevelId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> removeSeniorityLevelFromExpertise(@PathVariable Long expertiseId, @PathVariable Long seniorityLevelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.removeSeniorityLevelFromExpertise(expertiseId, seniorityLevelId));
    }

    @ApiOperation(value = "Add/remove expertise skill")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/skill", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSkillInExpertise(@PathVariable long expertiseId, @RequestBody ExpertiseSkillDTO expertiseSkillDTO) {
        expertiseService.addSkillInExpertise(expertiseId, expertiseSkillDTO.getSkillIds(), expertiseSkillDTO.isSelected());
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation(value = "get expertise skills")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/skill", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSkillInExpertise(@PathVariable long countryId, @PathVariable long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getExpertiseSkills(expertiseId, countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/organization_type/{orgTypeId}/skill_category", method = RequestMethod.POST)
    @ApiOperation("linking of skill with an organization type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addExpertiseInOrgType(@PathVariable long orgTypeId, @RequestBody OrgTypeSkillDTO orgTypeSkillDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.addSkillInOrgType(orgTypeId, orgTypeSkillDTO.getSkillId(), orgTypeSkillDTO.isSelected()));
    }

    @RequestMapping(value = COUNTRY_URL + "/organization_type/{orgTypeId}/skill_category", method = RequestMethod.GET)
    @ApiOperation("get Skill list for particular organization type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertise(@PathVariable long countryId, @PathVariable long orgTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getSkillsByOrganizationTypeId(countryId, orgTypeId));
    }

    @RequestMapping(value = "/country/organizaton_service/{organizationServiceId}", method = RequestMethod.GET)
    @ApiOperation("get country by organization service")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryByOrganizationService(@PathVariable long organizationServiceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryByOrganizationService(organizationServiceId));
    }

    @RequestMapping(value = "/country/{countryId}/task_type/skills", method = RequestMethod.GET)
    @ApiOperation("get skills by organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSkillsForTaskType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.getSkillsForTaskType(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicle", method = RequestMethod.POST)
    @ApiOperation("Add vehicle in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addVehicle(@PathVariable Long countryId, @Valid @RequestBody Vehicle vehicle) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.addVehicle(countryId, vehicle));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicle/{vehicleId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete vehicle from country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteVehicle(@PathVariable Long countryId, @PathVariable Long vehicleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.deleteVehicle(countryId, vehicleId));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicleList", method = RequestMethod.GET)
    @ApiOperation("Get resources of country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getVehicleList(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getAllVehicleListWithFeatures(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicle/{vehicleId}", method = RequestMethod.PUT)
    @ApiOperation("Update vehicle in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateVehicle(@PathVariable Long countryId, @PathVariable Long vehicleId, @Valid @RequestBody Vehicle vehicle) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.updateVehicle(countryId, vehicleId, vehicle));
    }

    @RequestMapping(value = COUNTRY_URL + "/cta/default-data", method = RequestMethod.GET)
    @ApiOperation("get default data for cta rule template")
    public ResponseEntity<Map<String, Object>> getDefaultDataForCTARuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getDefaultDataForCTATemplate(countryId, null));
    }

    // API to get Union And Levels
    @ApiOperation(value = "Get Unions and Levels by countryId")
    @RequestMapping(value = COUNTRY_URL + "/unions_and_levels", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> get(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getUnionAndOrganizationLevels(countryId));

    }

    @ApiOperation(value = "Get DayType and Presence Type")
    @RequestMapping(value = COUNTRY_URL + "/getWtaTemplateDefaultDataInfo", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getWtaTemplateDefaultDataInfo(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getWtaTemplateDefaultDataInfo(countryId));

    }

    @ApiOperation(value = "Map Selected Payroll Types to country ")
    @PutMapping(value = COUNTRY_URL + "/map_pay_rolls_country")
    public ResponseEntity<Map<String, Object>> mappingPayRollListToCountry(@PathVariable long countryId, @RequestBody Set<BigInteger> payRollTypeIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.mappingPayRollListToCountry(countryId, payRollTypeIds));

    }

    @ApiOperation(value = "get Default TimeSlot of Country")
    @GetMapping(value = COUNTRY_URL + "/get_default_timeSlot")
    public ResponseEntity<Map<String, Object>> mappingPayRollListToCountry(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getDefaultTimeSlot());

    }

    @ApiOperation(value = "get all units of Country")
    @GetMapping(value = COUNTRY_URL + "/get_all_units")
    public ResponseEntity<Map<String, Object>> getAllUnits(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getAllUnits(countryId));

    }

}


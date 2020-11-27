package com.kairos.controller.country;

import com.kairos.config.BootDataService;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.skill.OrgTypeSkillDTO;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user.organization.OrganizationBasicDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.OrgTypeLevelWrapper;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.model.user.skill.SkillCategoryQueryResults;
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

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
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
    @Inject private BootDataService bootDataService;

    @PostMapping(value = "/country")
    @ApiOperation("Create a new Country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createCountry(@Validated @RequestBody Country country) {
        if (country != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.createCountry(country));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @PutMapping(value = "/country")
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

    @GetMapping(value = "/country")
    @ApiOperation("Find all Countries")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllCountry() {
        List<Map<String, Object>> countryList = countryService.getAllCountries();
        if (isCollectionNotEmpty(countryList))
            return ResponseHandler.generateResponse(HttpStatus.OK, true, countryList);
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @GetMapping(value = COUNTRY_URL)
    @ApiOperation("Get country  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryById(countryId));
    }

    @GetMapping(value = "/countryId/{countryId}")
    @ApiOperation("Get country  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryById(@PathVariable Long countryId) {
        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryById(countryId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @DeleteMapping(value = COUNTRY_URL)
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
    @GetMapping(value = COUNTRY_URL + "/organization_type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrgTypesByCountryId(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getOrgTypesByCountryId(countryId));
    }

    @ApiOperation(value = "Add Organization Types")
    @PostMapping(value = COUNTRY_URL + "/organization_type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrgTypesByCountryId(@PathVariable Long countryId,
                                                                      @Validated @RequestBody OrganizationTypeDTO organizationTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.createOrganizationTypeForCountry(countryId, organizationTypeDTO));
    }

    @ApiOperation(value = "Update Organization Types")
    @PutMapping(value = COUNTRY_URL + "/organization_type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationType(@Validated @RequestBody UpdateOrganizationTypeDTO updateOrganizationTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.updateOrganizationType(updateOrganizationTypeDTO));
    }

    @ApiOperation(value = "Delete Organization Types")
    @DeleteMapping(value = COUNTRY_URL + "/organization_type/{organizationTypeId}")
//  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteOrganizationSubTypeById(@PathVariable Long organizationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.deleteOrganizationType(organizationTypeId));
    }

    //----// Organization Sub Type
    @ApiOperation(value = "Get all Organization Types")
    @GetMapping(value = COUNTRY_URL + "/organization_type/sub_type/{organizationTypeId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrgTypesSubByCountryId(@PathVariable Long countryId, @PathVariable long organizationTypeId) {
        List<OrgTypeLevelWrapper> response = organizationTypeService.getOrgSubTypesByTypeId(organizationTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Add Organization Types")
    @PostMapping(value = COUNTRY_URL + "/organization_type/sub_type/{organizationTypeId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrgTypesSubByCountryId(@PathVariable Long countryId, @Validated @RequestBody OrganizationType data, @PathVariable long organizationTypeId) {
        Map<String, Object> response = organizationTypeService.addOrganizationTypeSubType(data, organizationTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @GetMapping(value = COUNTRY_URL + "/skill_category")
    @ApiOperation("Get a skillCategory by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllSkillCategory(@PathVariable Long countryId) {
        if (countryId != null) {
            List<SkillCategoryQueryResults> skillCategory = skillCategoryService.getAllSkillCategoryOfCountryOrUnit(countryId, true);
            if (skillCategory != null) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true, skillCategory);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @PostMapping(value = COUNTRY_URL + "/skill_category")
    @ApiOperation("Add a new skillCategory")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSkillCategory(@PathVariable long countryId, @Validated @RequestBody SkillCategory objectToSave) {
        Object response = skillCategoryService.createSkillCategory(countryId, objectToSave);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @PutMapping(value = COUNTRY_URL + "/skill_category")
    @ApiOperation("Update a skillCategory  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSkillCategoryById(@RequestBody @Validated SkillCategory skillData) {
        Map<String, Object> updatedSkillCategory = skillCategoryService.updateSkillCategory(skillData);
        if (updatedSkillCategory == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, updatedSkillCategory);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedSkillCategory);
    }

    @DeleteMapping(value = COUNTRY_URL + "/skill_category/{skillCategoryId}")
    @ApiOperation("Delete a skillCategory  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteSkillCategoryById(@PathVariable long skillCategoryId) {
        skillCategoryService.deleteSkillCategorybyId(skillCategoryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation(value = "Get  skills by  Category id ")
    @GetMapping(value = COUNTRY_URL + "/skill_category/{categoryId}/skill")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSkillsByCategoryId(@PathVariable Long categoryId) {
        if (categoryId != null) {
            List<Skill> skillList = skillService.getSkillsByCategoryId(categoryId);
            if (isCollectionNotEmpty(skillList)) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true, skillList);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "Add a new skill")
    @PostMapping(value = "/skill_category/{skillCategoryId}/skill")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addSkill(@PathVariable long skillCategoryId, @RequestBody SkillDTO skillDTO) {
        Map<String, Object> response = skillService.createSkill(skillDTO, skillCategoryId);
        if (skillDTO == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Update a skill by id ")
    @PutMapping(value = COUNTRY_URL + "/skill")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSkillById(@PathVariable long countryId, @RequestBody SkillDTO skillDTO) {
        Map<String, Object> updatedSkill = skillService.updateSkill(countryId, skillDTO);
        if (updatedSkill == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedSkill);
    }

    @ApiOperation(value = "delete skill by id")
    @DeleteMapping(value = COUNTRY_URL + "/skill/{skillId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteSkill(@PathVariable long skillId) {
        if (skillService.deleteSkill(skillId)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
    }

    @ApiOperation(value = "Add a Parent Organization")
    @PostMapping(value = COUNTRY_URL + "/parent_organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createParentOrganization(@PathVariable long countryId,
                                                                        @Valid @RequestBody OrganizationBasicDTO organizationBasicDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, companyCreationService.createCompany(organizationBasicDTO, countryId));
    }

    @ApiOperation(value = "Delete Parent Organization or unit ")
    @DeleteMapping(value = COUNTRY_URL + "/parent_organization/{parentOrganizationId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteParentOrganization(@PathVariable long parentOrganizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.deleteOrganization(parentOrganizationId));
    }

    @ApiOperation(value = "Create Expertise")
    @PostMapping(value = COUNTRY_URL + "/expertise")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveExpertise(@PathVariable long countryId, @Validated @RequestBody ExpertiseDTO expertise) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, expertiseService.saveExpertise(countryId, expertise));
    }

    @ApiOperation(value = "Get Available expertise")
    @GetMapping(value = COUNTRY_URL + "/expertise")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllExpertise(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getAllExpertise(countryId));
    }

    @ApiOperation(value = "Get all union with Service")
    @GetMapping(value = COUNTRY_URL + "/union_with_service")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnionsAndService(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getUnionsAndService(countryId));
    }

    @ApiOperation(value = "Update expertise")
    @PutMapping(value = COUNTRY_URL + "/expertise/{expertiseId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertise(@PathVariable long countryId, @RequestBody  ExpertiseDTO expertise,@PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateExpertise(countryId, expertise,expertiseId));
    }

    @ApiOperation(value = "Update expertise Line")
    @PutMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/expertise_line/{expertiseLineId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertise(@PathVariable Long countryId, @RequestBody  ExpertiseDTO expertise,@PathVariable Long expertiseId,@PathVariable Long expertiseLineId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateExpertiseLine(expertise,expertiseId,expertiseLineId));
    }



    @ApiOperation(value = "Delete expertise")
    @DeleteMapping(value = COUNTRY_URL + "/expertise/{expertiseId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteExpertise(@PathVariable long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.deleteExpertise(expertiseId));
    }

    @ApiOperation(value = "Publish expertise")
    @PutMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/publish")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> publishExpertise(@PathVariable long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.publishExpertise(expertiseId));
    }



    @PostMapping(value = COUNTRY_URL + "/organization_type/{orgTypeId}/skill_category")
    @ApiOperation("linking of skill with an organization type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addExpertiseInOrgType(@PathVariable long orgTypeId, @RequestBody OrgTypeSkillDTO orgTypeSkillDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.addSkillInOrgType(orgTypeId, orgTypeSkillDTO.getSkillId(), orgTypeSkillDTO.isSelected()));
    }

    @GetMapping(value = COUNTRY_URL + "/organization_type/{orgTypeId}/skill_category")
    @ApiOperation("get Skill list for particular organization type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertise(@PathVariable long countryId, @PathVariable long orgTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getSkillsByOrganizationTypeId(orgTypeId));
    }

    @GetMapping(value = "/country/organizaton_service/{organizationServiceId}")
    @ApiOperation("get country by organization service")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryByOrganizationService(@PathVariable long organizationServiceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryByOrganizationService(organizationServiceId));
    }

    @GetMapping(value = "/country/{countryId}/task_type/skills")
    @ApiOperation("get skills by organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSkillsForTaskType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.getSkillsForTaskType(countryId));
    }

    @PostMapping(value = COUNTRY_URL + "/vehicle")
    @ApiOperation("Add vehicle in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addVehicle(@PathVariable Long countryId, @Valid @RequestBody Vehicle vehicle) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.addVehicle(countryId, vehicle));
    }

    @DeleteMapping(value = COUNTRY_URL + "/vehicle/{vehicleId}")
    @ApiOperation("Delete vehicle from country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteVehicle(@PathVariable Long countryId, @PathVariable Long vehicleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.deleteVehicle(countryId, vehicleId));
    }

    @GetMapping(value = COUNTRY_URL + "/vehicleList")
    @ApiOperation("Get resources of country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getVehicleList(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getAllVehicleListWithFeatures(countryId));
    }

    @PutMapping(value = COUNTRY_URL + "/vehicle/{vehicleId}")
    @ApiOperation("Update vehicle in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateVehicle(@PathVariable Long countryId, @PathVariable Long vehicleId, @Valid @RequestBody Vehicle vehicle) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.updateVehicle(countryId, vehicleId, vehicle));
    }

    @GetMapping(value = COUNTRY_URL + "/cta/default-data")
    @ApiOperation("get default data for cta rule template")
    public ResponseEntity<Map<String, Object>> getDefaultDataForCTARuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getDefaultDataForCTATemplate(countryId, null));
    }

    // API to get Union And Levels
    @ApiOperation(value = "Get Unions and Levels by countryId")
    @GetMapping(value = COUNTRY_URL + "/unions_and_levels")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> get(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getUnionAndOrganizationLevels(countryId));

    }

    @ApiOperation(value = "Get DayType and Presence Type")
    @GetMapping(value = COUNTRY_URL + "/getWtaTemplateDefaultDataInfo")
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
    public ResponseEntity<Map<String, Object>> getAllUnits() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getAllUnits());

    }

    @ApiOperation(value = "create Country Admin")
    @PostMapping(value = COUNTRY_URL + "/admin")
    public ResponseEntity<Map<String, Object>> createDefaultCountryAdmin(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, bootDataService.createDefaultCountryAdmin());
    }

    @RequestMapping(value = COUNTRY_URL+"/skill_Category/{id}/languageSettings", method = RequestMethod.PUT)
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfSkillCategory(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillCategoryService.updateTranslation(id,translations));
    }

    @RequestMapping(value = COUNTRY_URL+"/expertise/{id}/languageSettings", method = RequestMethod.PUT)
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfExpertise(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateTranslation(id,translations));
    }

    @RequestMapping(value = COUNTRY_URL+"/organization_type/{id}/languageSettings", method = RequestMethod.PUT)
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfOrganizationType(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.updateTranslation(id,translations));
    }

    @RequestMapping(value = COUNTRY_URL+"/organization_sub_type/{id}/languageSettings", method = RequestMethod.PUT)
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfOrganizationSubType(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.updateTranslation(id,translations));
    }



}


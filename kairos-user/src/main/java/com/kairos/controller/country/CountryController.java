package com.kairos.controller.country;

import com.kairos.persistence.model.dto.TimeTypeDTO;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrgTypeExpertiseDTO;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.organization.ParentOrganizationDTO;
import com.kairos.persistence.model.user.country.*;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseSkillDTO;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.language.LanguageLevel;
import com.kairos.persistence.model.user.payment_type.PaymentType;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.service.country.*;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.language.LanguageLevelService;
import com.kairos.service.language.LanguageService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.service.organization.OrganizationTypeService;
import com.kairos.service.payment_type.PaymentTypeService;
import com.kairos.service.country.TimeTypeService;
import com.kairos.service.skill.SkillCategoryService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.tpa_services.IntegrationConfigurationService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;


/**
 * CountryController
 * 1.Calls Country Service
 * 2. Call for CRUD operation on Country
 */
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
@RestController
public class CountryController {

    @Inject
    private CountryService countryService;
    @Inject
    private OrganizationServiceService organizationServiceService;
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
    private DayTypeService dayTypeService;
    @Inject
    private ClinicTypeService clinicTypeService;
    @Inject
    private IndustryTypeService industryTypeService;
    @Inject
    private OwnershipTypeService ownershipTypeService;
    @Inject
    private BusinessTypeService businessTypeService;
    @Inject
    private ContractTypeService contractTypeService;
    @Inject
    private VatTypeService vatTypeService;
    @Inject
    private EmployeeLimitService employeeLimitService;
    @Inject
    private PaymentTypeService paymentTypeService;
    @Inject
    private CurrencyService currencyService;
    @Inject
    private KairosStatusService kairosStatusService;
    @Inject
    private HousingTypeService housingTypeService;
    @Inject
    private LocationTypeService locationTypeService;
    @Inject
    private EngineerTypeService engineerTypeService;
    @Inject
    private LanguageService languageService;
    @Inject
    private LanguageLevelService languageLevelService;
    @Inject
    private CitizenStatusService citizenStatusService;
    @Inject
    private IntegrationConfigurationService integrationConfigurationService;
    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private TimeTypeService timeTypeService;

    // Country
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
    public ResponseEntity<Map<String, Object>> getAllCountry(@PathVariable Long organizationId) {
        List<Map<String, Object>> countryList = countryService.getAllCountries();
        if (countryList.size() != 0)
            return ResponseHandler.generateResponse(HttpStatus.OK, true, countryList);

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


    // Organization Type
    @ApiOperation(value = "Get all Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrgTypesByCountryId(@PathVariable Long countryId) {
        List<Map<String, Object>> response = organizationTypeService.getOrgTypesByCountryId(countryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Add Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrgTypesByCountryId(@PathVariable Long countryId, @Validated @RequestBody OrganizationType data) {
        Map<String, Object> response = organizationTypeService.createOrganizationTypeForCountry(countryId, data);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Update Organization Types")
    @RequestMapping(value = COUNTRY_URL + "/organization_type", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationType(@Validated @RequestBody OrganizationType data) {
        Map<String, Object> response = organizationTypeService.updateOrganizationType(data);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
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


    // Skill Category
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


    // Skill
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
    public ResponseEntity<Map<String, Object>> addSkill(@PathVariable long skillCategoryId, @RequestBody Skill skill) {

        Map<String, Object> response = skillService.createSkill(skill, skillCategoryId);
        if (skill == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Update a skill by id ")
    @RequestMapping(value = COUNTRY_URL + "/skill", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSkillById(@PathVariable long countryId, @RequestBody Skill skill) {
        Map<String, Object> updatedSkill = skillService.updateSkill(countryId, skill);
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


    // Expertise
    @ApiOperation(value = "Create Expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveExpertise(@PathVariable long countryId, @Validated @RequestBody Expertise expertise) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, expertiseService.saveExpertise(countryId, expertise));
    }

    @ApiOperation(value = "Get Available expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllExpertise(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getAllExpertise(countryId));
    }

    @ApiOperation(value = "Update expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertise(@RequestBody @Validated Expertise expertise) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateExpertise(expertise));
    }

    @ApiOperation(value = "Delete expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteExpertise(@PathVariable long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.deleteExpertise(expertiseId));
    }


    // Organization: Create Parent Level Organization
    @ApiOperation(value = "Add a Parent Organization")
    @RequestMapping(value = COUNTRY_URL + "/parent_organization", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createParentOrganization(@PathVariable long countryId, @RequestBody ParentOrganizationDTO organization) {
        if (organization == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.createParentOrganization(organization, countryId));
    }

    @ApiOperation(value = "Update Parent Organization")
    @RequestMapping(value = COUNTRY_URL + "/parent_organization/{parentOrganizationId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateParentOrganization(@PathVariable long countryId, @PathVariable long parentOrganizationId, @RequestBody ParentOrganizationDTO organization) {
        if (organization == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.updateParentOrganization(organization, parentOrganizationId, countryId));
    }

    @ApiOperation(value = "Delete Parent Organization")
    @RequestMapping(value = COUNTRY_URL + "/parent_organization/{parentOrganizationId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteParentOrganization(@PathVariable long parentOrganizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.deleteOrganization(parentOrganizationId));
    }


    // DayType
    @ApiOperation(value = "Get DayType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getAllDayTypeByCountryId(countryId));
    }

    @ApiOperation(value = "Add DayType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addDayType(@PathVariable long countryId, @Validated @RequestBody DayType dayType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.createDayType(dayType, countryId));
    }

    @ApiOperation(value = "Update DayType")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateDayType(@Validated @RequestBody DayType dayType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.updateDayType(dayType));
    }

    @ApiOperation(value = "Delete DayType by dayTypeId")
    @RequestMapping(value = COUNTRY_URL + "/dayType/{dayTypeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteDayType(@PathVariable long dayTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.deleteDayType(dayTypeId));

    }


    // ClinicType
    @ApiOperation(value = "Get DayType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/clinicType", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getClinicType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clinicTypeService.getClinicTypeByCountryId(countryId));

    }

    @ApiOperation(value = "Add DayType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/clinicType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addClinicType(@PathVariable long countryId, @Validated @RequestBody ClinicType clinicType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clinicTypeService.createClinicType(countryId, clinicType));
    }

    @ApiOperation(value = "Update DayType")
    @RequestMapping(value = COUNTRY_URL + "/clinicType", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateClinicType(@Validated @RequestBody ClinicType clinicType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clinicTypeService.updateClinicType(clinicType));
    }

    @ApiOperation(value = "Delete DayType by dayTypeId")
    @RequestMapping(value = COUNTRY_URL + "/clinicType/{clinicTypeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteClinicType(@PathVariable long clinicTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clinicTypeService.deleteClinicType(clinicTypeId));
    }


    // IndustryType
    @ApiOperation(value = "Get IndustryType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/industryType", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getIndustryType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, industryTypeService.getIndustryTypeByCountryId(countryId));

    }

    @ApiOperation(value = "Add IndustryType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/industryType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addIndustryType(@PathVariable long countryId, @Validated @RequestBody IndustryType industryType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, industryTypeService.createIndustryType(countryId, industryType));
    }

    @ApiOperation(value = "Update IndustryType")
    @RequestMapping(value = COUNTRY_URL + "/industryType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateIndustryType(@Validated @RequestBody IndustryType industryType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, industryTypeService.updateIndustryType(industryType));
    }

    @ApiOperation(value = "Delete IndustryType by industryTypeId")
    @RequestMapping(value = COUNTRY_URL + "/industryType/{industryTypeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteIndustryType(@PathVariable long industryTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, industryTypeService.deleteIndustryType(industryTypeId));
    }


    // Ownership type
    @ApiOperation(value = "Get OwnershipType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/ownershipType", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOwnershipType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ownershipTypeService.getOwnershipTypeByCountryId(countryId));

    }

    @ApiOperation(value = "Add OwnershipType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/ownershipType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOwnershipType(@PathVariable long countryId, @Validated @RequestBody OwnershipType ownershipType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ownershipTypeService.createOwnershipType(countryId, ownershipType));
    }

    @ApiOperation(value = "Update OwnershipType")
    @RequestMapping(value = COUNTRY_URL + "/ownershipType", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOwnershipType(@Validated @RequestBody OwnershipType ownershipType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ownershipTypeService.updateOwnershipType(ownershipType));
    }

    @ApiOperation(value = "Delete OwnershipType by ownershipTypeId")
    @RequestMapping(value = COUNTRY_URL + "/ownershipType/{ownershipTypeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteOwnershipType(@PathVariable long ownershipTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ownershipTypeService.deleteOwnershipType(ownershipTypeId));
    }


    // BusinessType
    @ApiOperation(value = "Get BusinessType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/businessType", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getBusinessType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, businessTypeService.getBusinessTypeByCountryId(countryId));

    }

    @ApiOperation(value = "Add BusinessType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/businessType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addBusinessType(@PathVariable long countryId, @Validated @RequestBody BusinessType businessType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, businessTypeService.createBusinessType(countryId, businessType));
    }

    @ApiOperation(value = "Update BusinessType")
    @RequestMapping(value = COUNTRY_URL + "/businessType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateBusinessType(@Validated @RequestBody BusinessType businessType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, businessTypeService.updateBusinessType(businessType));
    }

    @ApiOperation(value = "Delete BusinessType by businessTypeId")
    @RequestMapping(value = COUNTRY_URL + "/businessType/{businessTypeId}", method = RequestMethod.DELETE)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteBusinessType(@PathVariable long businessTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, businessTypeService.deleteBusinessType(businessTypeId));
    }


    //ContractType
    @ApiOperation(value = "Get ContractType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/contractType", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getContractType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, contractTypeService.getContractTypeByCountryId(countryId));

    }

    @ApiOperation(value = "Add ContractType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/contractType", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addContractType(@PathVariable long countryId, @Validated @RequestBody ContractType contractType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, contractTypeService.createContractType(countryId, contractType));
    }

    @ApiOperation(value = "Update ContractType")
    @RequestMapping(value = COUNTRY_URL + "/contractType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateContractType(@Validated @RequestBody ContractType contractType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, contractTypeService.updateContractType(contractType));
    }

    @ApiOperation(value = "Delete ContractType by contractTypeId")
    @RequestMapping(value = COUNTRY_URL + "/contractType/{contractTypeId}", method = RequestMethod.DELETE)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteContractType(@PathVariable long contractTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, contractTypeService.deleteContractType(contractTypeId));
    }


    // VatType
    @ApiOperation(value = "Get VatType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/vatType", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getVatType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, vatTypeService.getVatTypeByCountryId(countryId));
    }

    @ApiOperation(value = "Add VatType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/vatType", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addVatType(@PathVariable long countryId, @Validated @RequestBody VatType vatType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, vatTypeService.createVatType(countryId, vatType));
    }

    @ApiOperation(value = "Update VatType")
    @RequestMapping(value = COUNTRY_URL + "/vatType", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateVatType(@Validated @RequestBody VatType vatType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, vatTypeService.updateVatType(vatType));
    }

    @ApiOperation(value = "Delete VatType by vatTypeId")
    @RequestMapping(value = COUNTRY_URL + "/vatType/{vatTypeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteVatType(@PathVariable long vatTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, vatTypeService.deleteVatType(vatTypeId));
    }


    // EmployeeLimit
    @ApiOperation(value = "Get EmployeeLimit by countryId")
    @RequestMapping(value = COUNTRY_URL + "/employeeLimit", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEmployeeLimit(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employeeLimitService.getEmployeeLimitByCountryId(countryId));
    }

    @ApiOperation(value = "Add EmployeeLimit by countryId")
    @RequestMapping(value = COUNTRY_URL + "/employeeLimit", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addEmployeeLimit(@PathVariable long countryId, @Validated @RequestBody EmployeeLimit employeeLimit) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employeeLimitService.createEmployeeLimit(countryId, employeeLimit));
    }

    @ApiOperation(value = "Update EmployeeLimit")
    @RequestMapping(value = COUNTRY_URL + "/employeeLimit", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateEmployeeLimit(@Validated @RequestBody EmployeeLimit employeeLimit) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employeeLimitService.updateEmployeeLimit(employeeLimit));
    }

    @ApiOperation(value = "Delete EmployeeLimit by employeeLimitId")
    @RequestMapping(value = COUNTRY_URL + "/employeeLimit/{employeeLimitId}", method = RequestMethod.DELETE)
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> deleteEmployeeLimit(@PathVariable long employeeLimitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employeeLimitService.deleteEmployeeLimit(employeeLimitId));
    }


    // PaymentType
    @RequestMapping(value = COUNTRY_URL + "/paymentType", method = RequestMethod.POST)
    @ApiOperation("create payment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addPaymentType(@PathVariable long countryId, @RequestBody PaymentType objectToSave) {
        HashMap<String, Object> paymentType = paymentTypeService.createPaymentType(countryId, objectToSave);
        if (paymentType == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, paymentType);
    }

    @RequestMapping(value = COUNTRY_URL + "/paymentType", method = RequestMethod.GET)
    @ApiOperation("create payment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPaymentTypes(@PathVariable long countryId) {
        List<Map<String, Object>> paymentTypes = paymentTypeService.getPaymentTypes(countryId);
        if (paymentTypes == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, paymentTypes);
    }

    @RequestMapping(value = COUNTRY_URL + "/paymentType", method = RequestMethod.PUT)
    @ApiOperation("create payment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePaymentType(@RequestBody PaymentType paymentType) {
        HashMap<String, Object> updatedPaymentType = paymentTypeService.updatePaymentType(paymentType);
        if (updatedPaymentType == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedPaymentType);
    }

    @RequestMapping(value = COUNTRY_URL + "/paymentType/{paymentTypeId}", method = RequestMethod.DELETE)
    @ApiOperation("create payment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deletePaymentType(@PathVariable long paymentTypeId) {
        boolean isDeleted = paymentTypeService.deletePaymentType(paymentTypeId);
        if (isDeleted) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, isDeleted);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, isDeleted);
    }


    // Currency
    @RequestMapping(value = COUNTRY_URL + "/currency", method = RequestMethod.POST)
    @ApiOperation("add new currency")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addCurrency(@PathVariable long countryId, @RequestBody @Validated Currency objectToSave) {
        HashMap<String, Object> currency = currencyService.saveCurrency(countryId, objectToSave);
        if (currency == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, currency);
    }

    @RequestMapping(value = COUNTRY_URL + "/currency", method = RequestMethod.GET)
    @ApiOperation("get currencies by country id")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCurrencies(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, currencyService.getCurrencies(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/currency", method = RequestMethod.PUT)
    @ApiOperation("update currency by country id")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCurrency(@RequestBody @Validated Currency currency) {
        HashMap<String, Object> updatedCurrency = currencyService.updateCurrency(currency);
        if (updatedCurrency == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedCurrency);
    }

    @RequestMapping(value = COUNTRY_URL + "/currency/{currencyId}", method = RequestMethod.DELETE)
    @ApiOperation("delete currency")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCurrency(@PathVariable long currencyId) {
        boolean isDeleted = currencyService.deleteCurrency(currencyId);
        if (isDeleted) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, isDeleted);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, isDeleted);
    }


    // KairosStatus
    @ApiOperation(value = "Get KairosStatus by countryId")
    @RequestMapping(value = COUNTRY_URL + "/kairosStatus", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getKairosStatus(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kairosStatusService.getKairosStatusByCountryId(countryId));
    }

    @ApiOperation(value = "Add KairosStatus by countryId")
    @RequestMapping(value = COUNTRY_URL + "/kairosStatus", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addKairosStatus(@PathVariable long countryId, @Validated @RequestBody KairosStatus kairosStatus) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kairosStatusService.createKairosStatus(countryId, kairosStatus));
    }

    @ApiOperation(value = "Update KairosStatus")
    @RequestMapping(value = COUNTRY_URL + "/kairosStatus", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateKairosStatus(@Validated @RequestBody KairosStatus kairosStatus) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kairosStatusService.updateEmployeeLimit(kairosStatus));
    }

    @ApiOperation(value = "Delete KairosStatus by kairosStatusId")
    @RequestMapping(value = COUNTRY_URL + "/kairosStatus/{kairosStatusId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteKairosStatus(@PathVariable long kairosStatusId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kairosStatusService.deleteKairosStatus(kairosStatusId));
    }


    // HousingType
    @ApiOperation(value = "Get HousingType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/housingType", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getHousingType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, housingTypeService.getHousingTypeByCountryId(countryId));
    }

    @ApiOperation(value = "Add HousingType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/housingType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addHousingType(@PathVariable long countryId, @Validated @RequestBody HousingType housingType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, housingTypeService.createHousingType(countryId, housingType));
    }

    @ApiOperation(value = "Update HousingType")
    @RequestMapping(value = COUNTRY_URL + "/housingType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateHousingType(@Validated @RequestBody HousingType housingType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, housingTypeService.updateHousingType(housingType));
    }

    @ApiOperation(value = "Delete HousingType by housingTypeId")
    @RequestMapping(value = COUNTRY_URL + "/housingType/{housingTypeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteHousingType(@PathVariable long housingTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, housingTypeService.deleteHousingType(housingTypeId));
    }


    // LocationType
    @ApiOperation(value = "Get LocationType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/locationType", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getLocationType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, locationTypeService.getLocationTypeByCountryId(countryId));
    }

    @ApiOperation(value = "Add LocationType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/locationType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addLocationType(@PathVariable long countryId, @Validated @RequestBody LocationType locationType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, locationTypeService.createLocationType(countryId, locationType));
    }

    @ApiOperation(value = "Update LocationType")
    @RequestMapping(value = COUNTRY_URL + "/locationType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLocationType(@Validated @RequestBody LocationType locationType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, locationTypeService.updateLocationType(locationType));
    }

    @ApiOperation(value = "Delete LocationType by locationTypeId")
    @RequestMapping(value = COUNTRY_URL + "/locationType/{locationTypeId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteLocationType(@PathVariable long locationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, locationTypeService.deleteLocationType(locationTypeId));
    }


    // EngineerType
    @ApiOperation(value = "Get EngineerType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/engineerType", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEngineerType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, engineerTypeService.getEngineerTypeByCountryId(countryId));
    }

    @ApiOperation(value = "Add EngineerType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/engineerType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addEngineerType(@PathVariable long countryId, @Validated @RequestBody EngineerType engineerType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, engineerTypeService.createEngineerType(countryId, engineerType));
    }

    @ApiOperation(value = "Update EngineerType")
    @RequestMapping(value = COUNTRY_URL + "/engineerType", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateEngineerTypee(@Validated @RequestBody EngineerType engineerType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, engineerTypeService.updateEngineerType(engineerType));
    }

    @ApiOperation(value = "Delete EngineerType by locationTypeId")
    @RequestMapping(value = COUNTRY_URL + "/engineerType/{engineerTypeId}", method = RequestMethod.DELETE)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteEngineerType(@PathVariable long engineerTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, engineerTypeService.deleteEngineerType(engineerTypeId));
    }


    // Language
    @ApiOperation(value = "Get Language by countryId")
    @RequestMapping(value = COUNTRY_URL + "/language", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getLanguage(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageService.getLanguageByCountryId(countryId));
    }

    @ApiOperation(value = "Add Language by countryId")
    @RequestMapping(value = COUNTRY_URL + "/language", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addLanguage(@PathVariable long countryId, @Validated @RequestBody Language language) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageService.createLanguage(countryId, language));
    }

    @ApiOperation(value = "Update Language")
    @RequestMapping(value = COUNTRY_URL + "/language", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLanguage(@Validated @RequestBody Language language, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageService.updateLanguage(language, countryId));
    }

    @ApiOperation(value = "Delete Language by languageId")
    @RequestMapping(value = COUNTRY_URL + "/language/{languageId}", method = RequestMethod.DELETE)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteLanguage(@PathVariable long languageId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageService.deleteLanguage(languageId));
    }


    // LanguageLevel
    @ApiOperation(value = "Get languageLevel by countryId")
    @RequestMapping(value = COUNTRY_URL + "/languageLevel", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getLanguageLevel(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageLevelService.getLanguageLevelByCountryId(countryId));
    }

    @ApiOperation(value = "Add languageLevel by countryId")
    @RequestMapping(value = COUNTRY_URL + "/languageLevel", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addLanguageLevel(@PathVariable long countryId, @Validated @RequestBody LanguageLevel language) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageLevelService.createLanguageLevel(countryId, language));
    }

    @ApiOperation(value = "Update languageLevel")
    @RequestMapping(value = COUNTRY_URL + "/languageLevel", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLanguageLevel(@Validated @RequestBody LanguageLevel language, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageLevelService.updateLanguageLevel(language, countryId));
    }

    @ApiOperation(value = "Delete languageLevel by languageId")
    @RequestMapping(value = COUNTRY_URL + "/languageLevel/{languageLevelId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteLanguageLevel(@PathVariable long languageLevelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageLevelService.deleteLanguageLevel(languageLevelId));
    }


    //CitizenStatus
    @ApiOperation(value = "Get CitizenStatus by countryId")
    @RequestMapping(value = COUNTRY_URL + "/citizenStatus", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCitizenStatus(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, citizenStatusService.getCitizenStatusByCountryId(countryId));
    }

    @ApiOperation(value = "Add CitizenStatus by countryId")
    @RequestMapping(value = COUNTRY_URL + "/citizenStatus", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addCitizenStatus(@PathVariable long countryId, @Validated @RequestBody CitizenStatus citizenStatus) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, citizenStatusService.createCitizenStatus(countryId, citizenStatus));
    }

    @ApiOperation(value = "Update CitizenStatus")
    @RequestMapping(value = COUNTRY_URL + "/citizenStatus", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCitizenStatus(@Validated @RequestBody CitizenStatus citizenStatus, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, citizenStatusService.updateCitizenStatus(citizenStatus));
    }

    @ApiOperation(value = "Delete CitizenStatus by citizenStatusId")
    @RequestMapping(value = COUNTRY_URL + "/citizenStatus/{citizenStatusId}", method = RequestMethod.DELETE)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCitizenStatus(@PathVariable long citizenStatusId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, citizenStatusService.deleteCitizenStatus(citizenStatusId));
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

    @RequestMapping(value = COUNTRY_URL + "/organization_type/{orgTypeId}/expertise", method = RequestMethod.POST)
    @ApiOperation("linking of expertise with an organization type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addExpertiseInOrgType(@PathVariable long orgTypeId, @RequestBody OrgTypeExpertiseDTO orgTypeExpertiseDTO) {
        organizationTypeService.addExpertiseInOrgType(orgTypeId, orgTypeExpertiseDTO.getExpertiseId(), orgTypeExpertiseDTO.isSelected());
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @RequestMapping(value = COUNTRY_URL + "/organization_type/{orgTypeId}/expertise", method = RequestMethod.GET)
    @ApiOperation("get expertise list for particular organization type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertise(@PathVariable long countryId, @PathVariable long orgTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getExpertise(countryId, orgTypeId));
    }

    @RequestMapping(value = "/country/organizaton_service/{organizationServiceId}", method = RequestMethod.GET)
    @ApiOperation("get country by organization service")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryByOrganizationService(@PathVariable long organizationServiceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryByOrganizationService(organizationServiceId));
    }


    /**
     * @return
     * @auther anil maurya
     * <p>
     * this url will be called by using rest template
     */
    @RequestMapping(value = "/country/{countryId}/task_type/skills", method = RequestMethod.GET)
    @ApiOperation("get skills by organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSkillsForTaskType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.getSkillsForTaskType(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/level", method = RequestMethod.POST)
    @ApiOperation("Add level in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addLevel(@PathVariable long countryId, @RequestBody Level level) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.addLevel(countryId, level));
    }

    @RequestMapping(value = COUNTRY_URL + "/level/{levelId}", method = RequestMethod.PUT)
    @ApiOperation("Update level in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLevel(@PathVariable long countryId, @PathVariable long levelId, @RequestBody Level level) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.updateLevel(countryId, levelId, level));
    }

    @RequestMapping(value = COUNTRY_URL + "/level/{levelId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete level in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteLevel(@PathVariable long countryId, @PathVariable long levelId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.deleteLevel(countryId, levelId));
    }

    @RequestMapping(value = COUNTRY_URL + "/level", method = RequestMethod.GET)
    @ApiOperation("get levels in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getLevels(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getLevels(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/relationType", method = RequestMethod.POST)
    @ApiOperation("Add relation types in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addRelationType(@PathVariable Long countryId, @RequestBody RelationType relationType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.addRelationType(countryId, relationType));
    }

    @RequestMapping(value = COUNTRY_URL + "/relationType/{relationTypeId}", method = RequestMethod.DELETE)
    @ApiOperation("Add relation types in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteRelationType(@PathVariable Long countryId, @PathVariable Long relationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.deleteRelationType(countryId, relationTypeId));
    }

    @RequestMapping(value = COUNTRY_URL + "/relationType", method = RequestMethod.GET)
    @ApiOperation("Add relation types in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getRelationTypes(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getRelationTypes(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicle", method = RequestMethod.POST)
    @ApiOperation("Add vehicle in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addVehicle(@PathVariable Long countryId, @RequestBody Vehicle vehicle) {
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
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getVehicleList(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicle/{vehicleId}", method = RequestMethod.PUT)
    @ApiOperation("Update vehicle in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateVehicle(@PathVariable Long countryId, @PathVariable Long vehicleId, @RequestBody Vehicle vehicle) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.updateVehicle(countryId, vehicleId, vehicle));
    }

    @ApiOperation(value = "Add timeType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/timeType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addTimeType(@PathVariable long countryId, @Validated @RequestBody TimeTypeDTO timeType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.addTimeType(timeType, countryId));
    }



    // timeType
    @ApiOperation(value = "Get timeType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/timeType", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllTimeTypes(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getAllTimeTypes(countryId));
    }
}

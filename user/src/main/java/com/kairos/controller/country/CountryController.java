package com.kairos.controller.country;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.persistence.model.country.default_data.IndustryTypeDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.skill.OrgTypeSkillDTO;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user.organization.OrganizationBasicDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.persistence.model.country.*;
import com.kairos.persistence.model.country.default_data.*;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseSkillDTO;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.language.LanguageLevel;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.country.*;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.language.LanguageLevelService;
import com.kairos.service.language.LanguageService;
import com.kairos.service.organization.*;
import com.kairos.service.payment_type.PaymentTypeService;
import com.kairos.service.skill.SkillCategoryService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.tpa_services.IntegrationConfigurationService;
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

import static com.kairos.constants.ApiConstants.*;


/**
 * CountryController
 * 1.Calls Country Service
 * 2. Call for CRUD operation on Country
 */
@RequestMapping(API_V1)
@Api(API_V1)
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
    private TimeTypeRestClient timeTypeRestClient;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private AccessPageService accessPageService;

    @Inject private CompanyCreationService companyCreationService;


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


    // Organization Type
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


    // Organization: Create Parent Level Organization
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


    // DayType
    @ApiOperation(value = "Get DayType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getAllDayTypeByCountryId(countryId));
    }

    @ApiOperation(value = "Get DayType for unit")
    @RequestMapping(value = UNIT_URL + "/day_type", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayTypeForUnit(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getAllDayTypeForUnit(unitId));
    }

    @ApiOperation(value = "Add DayType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addDayType(@PathVariable long countryId, @Validated @RequestBody DayTypeDTO dayTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.createDayType(dayTypeDTO, countryId));
    }

    @ApiOperation(value = "Update DayType")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateDayType(@Validated @RequestBody DayTypeDTO dayTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.updateDayType(dayTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addClinicType(@PathVariable long countryId, @Validated @RequestBody ClinicTypeDTO clinicTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clinicTypeService.createClinicType(countryId, clinicTypeDTO));
    }

    @ApiOperation(value = "Update DayType")
    @RequestMapping(value = COUNTRY_URL + "/clinicType", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateClinicType(@PathVariable long countryId, @Validated @RequestBody ClinicTypeDTO clinicTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clinicTypeService.updateClinicType(countryId, clinicTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addIndustryType(@PathVariable long countryId, @Validated @RequestBody IndustryTypeDTO industryTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, industryTypeService.createIndustryType(countryId, industryTypeDTO));
    }

    @ApiOperation(value = "Update IndustryType")
    @RequestMapping(value = COUNTRY_URL + "/industryType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateIndustryType(@PathVariable long countryId, @Validated @RequestBody IndustryTypeDTO industryTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, industryTypeService.updateIndustryType(countryId, industryTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addOwnershipType(@PathVariable long countryId, @Validated @RequestBody OwnershipTypeDTO ownershipTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ownershipTypeService.createOwnershipType(countryId, ownershipTypeDTO));
    }

    @ApiOperation(value = "Update OwnershipType")
    @RequestMapping(value = COUNTRY_URL + "/ownershipType", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOwnershipType(@PathVariable long countryId, @Validated @RequestBody OwnershipTypeDTO ownershipTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ownershipTypeService.updateOwnershipType(countryId, ownershipTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addBusinessType(@PathVariable long countryId, @Validated @RequestBody BusinessTypeDTO businessTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, businessTypeService.createBusinessType(countryId, businessTypeDTO));
    }

    @ApiOperation(value = "Update BusinessType")
    @RequestMapping(value = COUNTRY_URL + "/businessType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateBusinessType(@PathVariable long countryId, @Validated @RequestBody BusinessTypeDTO businessTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, businessTypeService.updateBusinessType(countryId, businessTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addContractType(@PathVariable long countryId, @Validated @RequestBody ContractTypeDTO contractTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, contractTypeService.createContractType(countryId, contractTypeDTO));
    }

    @ApiOperation(value = "Update ContractType")
    @RequestMapping(value = COUNTRY_URL + "/contractType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateContractType(@PathVariable long countryId, @Validated @RequestBody ContractTypeDTO contractTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, contractTypeService.updateContractType(countryId, contractTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addVatType(@PathVariable long countryId, @Validated @RequestBody VatTypeDTO vatTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, vatTypeService.createVatType(countryId, vatTypeDTO));
    }

    @ApiOperation(value = "Update VatType")
    @RequestMapping(value = COUNTRY_URL + "/vatType", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateVatType(@PathVariable long countryId, @Validated @RequestBody VatTypeDTO vatTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, vatTypeService.updateVatType(countryId, vatTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addEmployeeLimit(@PathVariable long countryId, @Validated @RequestBody EmployeeLimitDTO employeeLimitDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employeeLimitService.createEmployeeLimit(countryId, employeeLimitDTO));
    }

    @ApiOperation(value = "Update EmployeeLimit")
    @RequestMapping(value = COUNTRY_URL + "/employeeLimit", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateEmployeeLimit(@PathVariable long countryId, @Validated @RequestBody EmployeeLimitDTO employeeLimitDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employeeLimitService.updateEmployeeLimit(countryId, employeeLimitDTO));
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
    public ResponseEntity<Map<String, Object>> addPaymentType(@PathVariable long countryId, @Validated @RequestBody PaymentTypeDTO paymentTypeDTO) {
        PaymentTypeDTO paymentType = paymentTypeService.createPaymentType(countryId, paymentTypeDTO);
        if (paymentType == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, paymentType);
    }

    @RequestMapping(value = COUNTRY_URL + "/paymentType", method = RequestMethod.GET)
    @ApiOperation("create payment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPaymentTypes(@PathVariable long countryId) {
        List<PaymentTypeDTO> paymentTypes = paymentTypeService.getPaymentTypes(countryId);
        if (paymentTypes == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, paymentTypes);
    }

    @RequestMapping(value = COUNTRY_URL + "/paymentType", method = RequestMethod.PUT)
    @ApiOperation("create payment type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updatePaymentType(@PathVariable long countryId, @Validated @RequestBody PaymentTypeDTO paymentTypeDTO) {
        PaymentTypeDTO updatedPaymentType = paymentTypeService.updatePaymentType(countryId, paymentTypeDTO);
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
    public ResponseEntity<Map<String, Object>> addCurrency(@PathVariable long countryId, @RequestBody @Validated CurrencyDTO currencyDTO) {
        CurrencyDTO currency = currencyService.createCurrency(countryId, currencyDTO);
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
    public ResponseEntity<Map<String, Object>> updateCurrency(@PathVariable long countryId, @RequestBody @Validated CurrencyDTO currencyDTO) {
        CurrencyDTO updatedCurrency = currencyService.updateCurrency(countryId, currencyDTO);
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
    public ResponseEntity<Map<String, Object>> addKairosStatus(@PathVariable long countryId, @Validated @RequestBody KairosStatusDTO kairosStatusDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kairosStatusService.createKairosStatus(countryId, kairosStatusDTO));
    }

    @ApiOperation(value = "Update KairosStatus")
    @RequestMapping(value = COUNTRY_URL + "/kairosStatus", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateKairosStatus(@PathVariable long countryId, @Validated @RequestBody KairosStatusDTO kairosStatusDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, kairosStatusService.updateKairosStatus(countryId, kairosStatusDTO));
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
    public ResponseEntity<Map<String, Object>> addHousingType(@PathVariable long countryId, @Validated @RequestBody HousingTypeDTO housingTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, housingTypeService.createHousingType(countryId, housingTypeDTO));
    }

    @ApiOperation(value = "Update HousingType")
    @RequestMapping(value = COUNTRY_URL + "/housingType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateHousingType(@PathVariable long countryId, @Validated @RequestBody HousingTypeDTO housingTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, housingTypeService.updateHousingType(countryId, housingTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addLocationType(@PathVariable long countryId, @Validated @RequestBody LocationTypeDTO locationTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, locationTypeService.createLocationType(countryId, locationTypeDTO));
    }

    @ApiOperation(value = "Update LocationType")
    @RequestMapping(value = COUNTRY_URL + "/locationType", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLocationType(@PathVariable long countryId, @Validated @RequestBody LocationTypeDTO locationTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, locationTypeService.updateLocationType(countryId, locationTypeDTO));
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
    public ResponseEntity<Map<String, Object>> addEngineerType(@PathVariable long countryId, @Validated @RequestBody EngineerTypeDTO engineerTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, engineerTypeService.createEngineerType(countryId, engineerTypeDTO));
    }

    @ApiOperation(value = "Update EngineerType")
    @RequestMapping(value = COUNTRY_URL + "/engineerType", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateEngineerTypee(@PathVariable long countryId, @Validated @RequestBody EngineerTypeDTO engineerTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, engineerTypeService.updateEngineerType(countryId, engineerTypeDTO));
    }

    @ApiOperation(value = "Delete EngineerType by engineerTypeId")
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
    public ResponseEntity<Map<String, Object>> addCitizenStatus(@PathVariable long countryId, @Validated @RequestBody CitizenStatusDTO citizenStatusDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, citizenStatusService.createCitizenStatus(countryId, citizenStatusDTO));
    }

    @ApiOperation(value = "Update CitizenStatus")
    @RequestMapping(value = COUNTRY_URL + "/citizenStatus", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateCitizenStatus(@Validated @RequestBody CitizenStatusDTO citizenStatusDTO, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, citizenStatusService.updateCitizenStatus(citizenStatusDTO, countryId));
    }

    @ApiOperation(value = "Delete CitizenStatus by citizenStatusId")
    @RequestMapping(value = COUNTRY_URL + "/citizenStatus/{citizenStatusId}", method = RequestMethod.DELETE)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteCitizenStatus(@PathVariable long citizenStatusId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, citizenStatusService.deleteCitizenStatus(citizenStatusId));
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
    @RequestMapping(value = COUNTRY_URL + "/expertise", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertise(@PathVariable long countryId, @RequestBody @Validated ExpertiseDTO expertise) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.updateExpertise(countryId, expertise));
    }

    @ApiOperation(value = "Delete expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteExpertise(@PathVariable long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.deleteExpertise(expertiseId));
    }

//    @ApiOperation(value = "get a single expertise based on Id")
//    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}", method = RequestMethod.GET)
//    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
//    public ResponseEntity<Map<String, Object>> getExpertiseById(@PathVariable Long expertiseId) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.getExpertiseById(expertiseId));
//    }

    @ApiOperation(value = "Publish expertise")
    @RequestMapping(value = COUNTRY_URL + "/expertise/{expertiseId}/publish", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> publishExpertise(@PathVariable long expertiseId, @RequestParam Long publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseService.publishExpertise(expertiseId, publishedDate));
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
    /**
     * @Id KP-3683
     * @Changed By vipul
     * making it for skill instead of expertise
    **/

    @RequestMapping(value = COUNTRY_URL + "/organization_type/{orgTypeId}/skill_category", method = RequestMethod.POST)
    @ApiOperation("linking of skill with an organization type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addExpertiseInOrgType(@PathVariable long orgTypeId, @RequestBody OrgTypeSkillDTO orgTypeSkillDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.addExpertiseInOrgType(orgTypeId, orgTypeSkillDTO.getSkillId(), orgTypeSkillDTO.isSelected()));
    }

    @RequestMapping(value = COUNTRY_URL + "/organization_type/{orgTypeId}/skill_category", method = RequestMethod.GET)
    @ApiOperation("get Skill list for particular organization type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertise(@PathVariable long countryId, @PathVariable long orgTypeId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationTypeService.getSkillsByOrganizationTypeId(countryId, orgTypeId));
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
    public ResponseEntity<Map<String, Object>> addRelationType(@PathVariable Long countryId, @RequestBody RelationTypeDTO relationTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.addRelationType(countryId, relationTypeDTO));
    }

    @RequestMapping(value = COUNTRY_URL + "/relationType/{relationTypeId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete relation types in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteRelationType(@PathVariable Long countryId, @PathVariable Long relationTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.deleteRelationType(countryId, relationTypeId));
    }

    @RequestMapping(value = COUNTRY_URL + "/relationType", method = RequestMethod.GET)
    @ApiOperation("Get relation types in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getRelationTypes(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getRelationTypes(countryId));
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

    @ApiOperation(value = "Get day types by id")
    @RequestMapping(value = "/day_types", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getDayTypesById(@RequestBody List<Long> dayTypeIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getDayTypes(dayTypeIds));
    }

    @RequestMapping(value = COUNTRY_URL + "/cta/default-data", method = RequestMethod.GET)
    @ApiOperation("get default data for cta rule template")
    public ResponseEntity<Map<String, Object>> getDefaultDataForCTARuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getDefaultDataForCTATemplate(countryId, null));
    }

    @ApiOperation(value = "Get day types by id")
    @RequestMapping(value = COUNTRY_URL + "/time_slots", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTimeSlotOfCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotsOfCountry(countryId));
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
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.mappingPayRollListToCountry(countryId,payRollTypeIds));

    }

    @ApiOperation(value = "get cuntry id by unit id ")
    @GetMapping(value = UNIT_URL + "/country_id")
    public ResponseEntity<Map<String, Object>> getCountryIdByUnitId(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryIdByUnitId(unitId));

    }
}


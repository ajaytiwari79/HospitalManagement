package com.kairos.controller.country;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.persistence.model.country.default_data.*;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.language.LanguageLevel;
import com.kairos.service.country.*;
import com.kairos.service.language.LanguageLevelService;
import com.kairos.service.language.LanguageService;
import com.kairos.service.organization.TimeSlotService;
import com.kairos.service.payment_type.PaymentTypeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@RequestMapping(API_V1)
@Api(API_V1)
@RestController
public class CountryDefaultDataController {

    @Inject
    private CountryService countryService;
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
    private TimeSlotService timeSlotService;

    @ApiOperation(value = "Get day types by id")
    @RequestMapping(value = COUNTRY_URL + "/time_slots", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTimeSlotOfCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotsOfCountry(countryId));
    }

    @ApiOperation(value = "Get day types by id")
    @RequestMapping(value = "/day_types", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getDayTypesById(@RequestBody List<Long> dayTypeIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getDayTypes(dayTypeIds));
    }

    @RequestMapping(value = COUNTRY_URL + "/level", method = RequestMethod.POST)
    @ApiOperation("Add level in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addLevel(@PathVariable long countryId, @RequestBody @Valid Level level) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.addLevel(countryId, level));
    }

    @RequestMapping(value = COUNTRY_URL + "/level/{levelId}", method = RequestMethod.PUT)
    @ApiOperation("Update level in country")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateLevel(@PathVariable long countryId, @PathVariable long levelId, @RequestBody @Valid Level level) {
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

    @ApiOperation(value = "Get DayType by countryId")
    @RequestMapping(value = COUNTRY_URL + "/dayType", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayType(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dayTypeService.getAllDayTypeByCountryId(countryId));
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
}

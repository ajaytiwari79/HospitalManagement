package com.kairos.service.organization;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.OrganizationMappingActivityTypeDTO;
import com.kairos.dto.activity.cta.CTABasicDetailsDTO;
import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
import com.kairos.dto.activity.wta.basic_details.WTABasicDetailsDTO;
import com.kairos.dto.activity.wta.basic_details.WTADefaultDataInfoDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotsDeductionDTO;
import com.kairos.dto.user.organization.*;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.country.default_data.*;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.services.OrganizationServicesAndLevelQueryResult;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.model.staff.personal_details.OrganizationStaffWrapper;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailQueryResult;
import com.kairos.persistence.model.user.counter.OrgTypeQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.response.OrderAndActivityDTO;
import com.kairos.persistence.model.user.expertise.response.OrderDefaultDataWrapper;
import com.kairos.persistence.model.user.open_shift.OrganizationTypeAndSubType;
import com.kairos.persistence.model.user.open_shift.RuleTemplateDefaultData;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.resources.VehicleQueryResult;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.*;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.*;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.PlannedTimeTypeRestClient;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.client.ClientService;
import com.kairos.service.country.CitizenStatusService;
import com.kairos.service.country.DayTypeService;
import com.kairos.service.country.EmploymentTypeService;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.region.RegionService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffRetrievalService;
import com.kairos.utils.FormatUtil;
import com.kairos.utils.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResult;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.DateUtils.parseDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.UserMessagesConstants.*;

@Transactional
@Service
public class OrganizationService {
    public static final String ESTIMOTE_APP_ID = "estimoteAppId";
    public static final String ESTIMOTE_APP_TOKEN = "estimoteAppToken";
    public static final String CLIENT_SINCE = "clientSince";
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OpeningSettingsGraphRepository openingSettingsGraphRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private BusinessTypeGraphRepository businessTypeGraphRepository;
    @Inject
    private IndustryTypeGraphRepository industryTypeGraphRepository;
    @Inject
    private OwnershipTypeGraphRepository ownershipTypeGraphRepository;
    @Inject
    private ContractTypeGraphRepository contractTypeGraphRepository;
    @Inject
    private VatTypeGraphRepository vatTypeGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private RegionService regionService;
    @Inject
    private KairosStatusGraphRepository kairosStatusGraphRepository;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private TeamGraphRepository teamGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private TeamService teamService;
    @Inject
    private ClientService clientService;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private CitizenStatusService citizenStatusService;
    @Inject
    private AbsenceTypesRepository absenceTypesRepository;
    @Inject
    private SkillService skillService;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SkillGraphRepository skillGraphRepository;
    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private PlannedTimeTypeRestClient plannedTimeTypeRestClient;
    @Inject
    private EmploymentTypeService employmentTypeService;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private SchedulerServiceRestClient schedulerServiceRestClient;
    @Inject
    private OrganizationMetadataRepository organizationMetadataRepository;
    @Inject private UnitService unitService;
    @Inject
    private TagService tagService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);

    public OrganizationDTO getOrganizationWithCountryId(long id) {
        OrganizationBaseEntity unit = organizationBaseRepository.findOne(id);
        OrganizationDTO organizationDTO = ObjectMapperUtils.copyPropertiesByMapper(unit, OrganizationDTO.class);
        organizationDTO.setCountryId(countryGraphRepository.getCountryIdByUnitId(id));
        organizationDTO.setParentOrganization(unit instanceof Organization);
        organizationDTO.setTagDTOS(tagService.getTagsByOrganizationIdAndMasterDataType(id, MasterDataTypeEnum.STAFF));
        return organizationDTO;
    }

    public boolean showCountryTagForOrganization(long orgId) {
        return organizationBaseRepository.showCountryTags(orgId);
    }


    public Organization createOrganization(Organization organization, boolean baseOrganization) {
        organizationGraphRepository.save(organization);
        timeSlotService.createDefaultTimeSlots(organization, TimeSlotType.SHIFT_PLANNING);
        timeSlotService.createDefaultTimeSlots(organization, TimeSlotType.TASK_PLANNING);
        if (!baseOrganization) {
            accessGroupService.createDefaultAccessGroups(organization);
            organizationGraphRepository.assignDefaultSkillsToOrg(organization.getId(), DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
        }
        return organization;
    }

    public boolean deleteOrganization(long organizationId) {
        OrganizationBaseEntity organization = organizationBaseRepository.findOne(organizationId);
        if(organization.isBoardingCompleted()){
            exceptionService.actionNotPermittedException(MESSAGE_PUBLISH_ORGANIZATION_CONNOT_DELETE);
        }
        boolean success;
        if (organization.isBoardingCompleted()) {
            organization.setEnable(false);
            organization.setDeleted(true);
            organizationBaseRepository.save(organization);
            success = true;
        } else {
            List<Long> organizationIdsToDelete = getAllOrganizationIdsToDelete(organization);
            unitGraphRepository.removeOrganizationCompletely(organizationIdsToDelete);
            success = true;
        }
        return success;
    }

    public OrganizationBaseEntity getOrganizationById(long id) {
        return organizationBaseRepository.findOne(id);
    }


    public Map<String, Object> getGeneralDetails(long id) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> metaData = null;
        Long countryId = countryGraphRepository.getCountryIdByUnitId(id);
        List<Map<String, Object>> data = unitGraphRepository.getGeneralTabMetaData(countryId);
        for (Map<String, Object> map : data) {
            metaData = (Map<String, Object>) map.get("data");
        }
        Map<String, Object> cloneMap = new HashMap<>(metaData);
        OrganizationContactAddress organizationContactAddress = unitGraphRepository.getContactAddressOfOrg(id);
        ZipCode zipCode = organizationContactAddress.getZipCode();
        List<Municipality> municipalities = (zipCode == null) ? Collections.emptyList() : municipalityGraphRepository.getMunicipalitiesByZipCode(zipCode.getId());
        Map<String, Object> generalTabQueryResult = unitGraphRepository.getGeneralTabInfo(id);
        HashMap<String, Object> generalTabInfo = new HashMap<>(generalTabQueryResult);
        generalTabInfo.put(CLIENT_SINCE, (generalTabInfo.get(CLIENT_SINCE) == null ? null : getDate((long) generalTabInfo.get(CLIENT_SINCE))));
        cloneMap.put("municipalities", municipalities);
        response.put("generalTabInfo", generalTabInfo);
        response.put("otherData", cloneMap);

        return response;
    }

    public boolean updateOrganizationGeneralDetails(OrganizationGeneral organizationGeneral, long unitId) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findById(unitId).orElseThrow(()-> new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_UNIT_ID_NOTFOUND, unitId)));
        OwnershipType ownershipType = null;
        ContractType contractType = null;
        IndustryType industryType = null;
        KairosStatus kairosStatus = null;
        VatType vatType = null;
        if (organizationGeneral.getOwnershipTypeId() != null)
            ownershipType = ownershipTypeGraphRepository.findOne(organizationGeneral.getOwnershipTypeId());
        List<BusinessType> businessTypes = businessTypeGraphRepository.findByIdIn(organizationGeneral.getBusinessTypeId());
        if (organizationGeneral.getContractTypeId() != null)
            contractType = contractTypeGraphRepository.findOne(organizationGeneral.getContractTypeId());
        if (organizationGeneral.getIndustryTypeId() != null)
            industryType = industryTypeGraphRepository.findOne(organizationGeneral.getIndustryTypeId());
        if (organizationGeneral.getVatTypeId() != null)
            vatType = vatTypeGraphRepository.findOne(organizationGeneral.getVatTypeId());
        if (organizationGeneral.getKairosStatusId() != null)
            kairosStatus = kairosStatusGraphRepository.findOne(organizationGeneral.getKairosStatusId());
        ContactAddress contactAddress = organizationBaseEntity.getContactAddress();
        if (contactAddress != null) {
            Municipality municipality = municipalityGraphRepository.findOne(organizationGeneral.getMunicipalityId());
            if (municipality == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_MUNICIPALITY_NOTFOUND);
            }
            contactAddress.setMunicipality(municipality);
        }
        Optional<OrganizationType> organizationTypes = organizationTypeGraphRepository.findById(organizationGeneral.getOrganizationTypeId());
        List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationGeneral.getOrganizationSubTypeId());
        organizationBaseEntity.setContactAddress(contactAddress);
        organizationBaseEntity.setName(organizationGeneral.getName());
        organizationBaseEntity.setShortName(organizationGeneral.getShortName());
        organizationBaseEntity.setCvrNumber(organizationGeneral.getCvrNumber());
        organizationBaseEntity.setPNumber(organizationGeneral.getpNumber());
        organizationBaseEntity.setWebSiteUrl(organizationGeneral.getWebsiteUrl());
        organizationBaseEntity.setDescription(organizationGeneral.getDescription());
        organizationBaseEntity.setOrganizationType(organizationTypes.get());
        organizationBaseEntity.setOrganizationSubTypes(organizationSubTypes);
        organizationBaseEntity.setVatType(vatType);
        organizationBaseEntity.setEanNumber(organizationGeneral.getEanNumber());
        organizationBaseEntity.setCostCenterCode(organizationGeneral.getCostCenterCode());
        organizationBaseEntity.setCostCenterName(organizationGeneral.getCostCenterName());
        organizationBaseEntity.setOwnershipType(ownershipType);
        organizationBaseEntity.setBusinessTypes(businessTypes);
        organizationBaseEntity.setIndustryType(industryType);
        organizationBaseEntity.setContractType(contractType);
        organizationBaseEntity.setClientSince(parseDate(organizationGeneral.getClientSince()).getTime());
        organizationBaseEntity.setKairosStatus(kairosStatus);
        organizationBaseEntity.setExternalId(organizationGeneral.getExternalId());
        organizationBaseEntity.setEndTimeDeduction(organizationGeneral.getPercentageWorkDeduction());
        organizationBaseEntity.setKmdExternalId(organizationGeneral.getKmdExternalId());
        organizationBaseEntity.setDayShiftTimeDeduction(organizationGeneral.getDayShiftTimeDeduction());
        organizationBaseEntity.setNightShiftTimeDeduction(organizationGeneral.getNightShiftTimeDeduction());
        if (organizationBaseEntity instanceof Organization) {
            ((Organization) organizationBaseEntity).setKairosHub(organizationGeneral.isKairosHub());
            organizationGraphRepository.save(((Organization) organizationBaseEntity));
        } else {
            unitGraphRepository.save(((Unit) organizationBaseEntity));
        }

        return true;

    }

    public Map<String, Object> getParentOrganization(Long countryIdOrOrgId,boolean viaCountry) {
        Map<String, Object> data = new HashMap<>(2);

        Long countryId=viaCountry?countryIdOrOrgId:countryGraphRepository.getCountryIdByUnitId(countryIdOrOrgId);
        List<OrganizationBasicResponse> organizationQueryResult =viaCountry? unitGraphRepository.getAllParentOrganizationOfCountry(countryId):
                unitGraphRepository.getAllOrganizationOfOrganization(countryIdOrOrgId);
        OrganizationCreationData organizationCreationData = unitGraphRepository.getOrganizationCreationData(countryId);
        List<Map<String, Object>> zipCodes = FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId));
        if (Optional.ofNullable(organizationCreationData).isPresent()) {
            organizationCreationData.setZipCodes(zipCodes);
        }
        organizationCreationData.setCompanyTypes(CompanyType.getListOfCompanyType());
        organizationCreationData.setCompanyUnitTypes(CompanyUnitType.getListOfCompanyUnitType());
        organizationCreationData.setAccessGroups(accessGroupService.getCountryAccessGroupsForOrganizationCreation(countryId));
        organizationCreationData.setHubList(unitGraphRepository.getAllHubByCountryId(countryId));
        organizationCreationData.setCountryId(countryId);
        data.put("globalData", organizationCreationData);
        data.put("organization", organizationQueryResult);
        return data;
    }

    public List<OrganizationBasicResponse> getOrganizationGdprAndWorkcenter(Long organizationId) {
        List<OrganizationBasicResponse> organizationQueryResult = unitGraphRepository.getOrganizationGdprAndWorkCenter(organizationId);
        List<Long> unitIds = organizationQueryResult.stream().map(organizationBasicResponse -> organizationBasicResponse.getId()).collect(Collectors.toList());
        List<OrganizationContactAddress> organizationContactAddresses = unitGraphRepository.getContactAddressOfOrganizations(unitIds);
        Map<Long,OrganizationContactAddress> organizationContactAddressMap=organizationContactAddresses.stream().collect(Collectors.toMap(k->k.getOrganizationId(), Function.identity()));
        List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults = userGraphRepository.getUnitManagerOfOrganization(unitIds, organizationId);
        for (OrganizationBasicResponse organizationData : organizationQueryResult) {
                if (organizationContactAddressMap.containsKey(organizationData.getId())) {
                    AddressDTO addressDTO=ObjectMapperUtils.copyPropertiesByMapper(organizationContactAddressMap.get(organizationData.getId()).getContactAddress(),AddressDTO.class);
                    organizationData.setContactAddress(addressDTO);
                    addressDTO.setMunicipality(ObjectMapperUtils.copyPropertiesByMapper(organizationContactAddressMap.get(organizationData.getId()).getMunicipality(),MunicipalityDTO.class));
                    addressDTO.setZipCode(ObjectMapperUtils.copyPropertiesByMapper(organizationContactAddressMap.get(organizationData.getId()).getZipCode(),ZipCodeDTO.class));
            }
            Optional<StaffPersonalDetailQueryResult> currentStaff = staffPersonalDetailQueryResults.stream().filter(staffPersonalDetailDTO -> staffPersonalDetailDTO.getOrganizationId().equals(organizationData.getId())).findFirst();
            organizationData.setUnitManager(currentStaff.isPresent() ? currentStaff.get() : null);
        }
        return organizationQueryResult;
    }

    public Unit getByPublicPhoneNumber(String phoneNumber) {
        return unitGraphRepository.findOrganizationByPublicPhoneNumber(phoneNumber);
    }

    public Unit getOrganizationByExternalId(String externalId) {
        return unitGraphRepository.findByExternalId(externalId);
    }

    public OrganizationStaffWrapper getOrganizationAndStaffByExternalId(String externalId, Long staffExternalId, Long staffTimeCareEmploymentId) {
        OrganizationStaffWrapper organizationStaffWrapper = staffGraphRepository.getStaff(staffExternalId, staffTimeCareEmploymentId);
        if (Optional.ofNullable(organizationStaffWrapper).isPresent()) {
            organizationStaffWrapper.setUnit(unitGraphRepository.findByExternalId(externalId));
        }
        return organizationStaffWrapper;
    }

    public boolean deleteOrganizationById(long parentOrganizationId, long childOrganizationId) {
        unitGraphRepository.deleteChildRelationOrganizationById(parentOrganizationId, childOrganizationId);
        unitGraphRepository.deleteOrganizationById(childOrganizationId);
        return unitGraphRepository.findOne(childOrganizationId) == null;
    }

    public Unit updateExternalId(long unitId, long externalId) {
        Unit unit = unitGraphRepository.findOne(unitId);
        if (unit == null) {
            return null;
        }
        unit.setExternalId(String.valueOf(externalId));
        unitGraphRepository.save(unit);
        return unit;
    }

    public Map setEstimoteCredentials(long organization, Map<String, String> payload) {
        Unit unitObj = unitGraphRepository.findOne(organization);
        if (unitObj != null && payload.containsKey(ESTIMOTE_APP_ID) && payload.containsKey(ESTIMOTE_APP_TOKEN) && payload.get(ESTIMOTE_APP_ID) != null && payload.get(ESTIMOTE_APP_TOKEN) != null) {
            unitObj.setEstimoteAppId(payload.get(ESTIMOTE_APP_ID));
            unitObj.setEstimoteAppToken(payload.get(ESTIMOTE_APP_TOKEN));
            return payload;
        }
        return null;
    }

    public Map getEstimoteCredentials(long organization) {
        Map returnData = new HashMap();
        Unit unitObj = unitGraphRepository.findOne(organization);
        if (unitObj != null) {
            returnData.put(ESTIMOTE_APP_ID, unitObj.getEstimoteAppId());
            returnData.put(ESTIMOTE_APP_TOKEN, unitObj.getEstimoteAppToken());
        }
        return returnData;
    }


    public Integer checkDuplicationOrganizationRelation(Long organizationId, Long unitId) {
        return unitGraphRepository.checkParentChildRelation(organizationId, unitId);
    }

    public Map<String, Object> getCommonDataOfOrganization(Long unitId) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> organizationSkills = unitGraphRepository.getSkillsOfParentOrganization(unitId);
        List<Map<String, Object>> orgSkillRel = new ArrayList<>(organizationSkills.size());
        for (Map<String, Object> map : organizationSkills) {
            orgSkillRel.add((Map<String, Object>) map.get("data"));
        }
        data.put("skillsOfOrganization", orgSkillRel);
        data.put("teamsOfOrganization", teamService.getTeamsInUnit(unitId));
        data.put("zipCodes", regionService.getAllZipCodes());
        return data;
    }

    public Map<String, Object> getUnitVisitationInfo(long unitId) {
        Map<String, Object> organizationResult = new HashMap();
        Map<String, Object> unitData = new HashMap();
        Map<String, Object> organizationTimeSlotList = timeSlotService.getTimeSlots(unitId);
        unitData.put("organizationTimeSlotList", organizationTimeSlotList.get("timeSlots"));
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        List<CitizenStatusDTO> clientStatusList = citizenStatusService.getCitizenStatusByCountryId(countryId);
        unitData.put("clientStatusList", clientStatusList);
        List<Object> localAreaTagsList = new ArrayList<>();
        List<Map<String, Object>> tagList = organizationMetadataRepository.findAllByIsDeletedAndUnitId(unitId);
        for (Map<String, Object> map : tagList) {
            localAreaTagsList.add(map.get("tags"));
        }
        unitData.put("localAreaTags", localAreaTagsList);
        unitData.put("serviceTypes", organizationServiceRepository.getOrganizationServiceByOrgId(unitId));
        Map<String, Object> timeSlotData = timeSlotService.getTimeSlots(unitId);
        if (timeSlotData != null) {
            unitData.put("timeSlotList", timeSlotData);
        }
        organizationResult.put("unitData", unitData);
        List<Map<String, Object>> citizenList = clientService.getOrganizationClientsExcludeDead(unitId);
        organizationResult.put("citizenList", citizenList);
        return organizationResult;
    }

    public Map<String, Object> getTaskDemandSupplierInfo(Long unitId) {
        Map<String, Object> supplierInfo = new HashMap();
        Unit weekdaySupplier = unitGraphRepository.findOne(unitId, 0);
        supplierInfo.put("weekdaySupplier", weekdaySupplier.getName());
        supplierInfo.put("weekdaySupplierId", weekdaySupplier.getId());
        return supplierInfo;
    }

    public Unit getParentOrganizationOfCityLevel(Long unitId) {
        return unitGraphRepository.getParentOrganizationOfCityLevel(unitId);
    }

    public Organization getParentOfOrganization(Long unitId) {
        return fetchParentOrganization(unitId);
    }

    public Unit getOrganizationByTeamId(Long teamId) {
        return unitGraphRepository.getOrganizationByTeamId(teamId);
    }

    public Map<String, Object> getPrerequisitesForTimeCareTask(GetWorkShiftsFromWorkPlaceByIdResult workShift) {
        Unit unit = unitGraphRepository.findByExternalId(workShift.getWorkPlace().getId().toString());
        if (unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_EXTERNALID_NOTFOUND);
        }
        Map<String, Object> requiredDataForTimeCareTask = new HashMap<>();
        OrganizationContactAddress organizationContactData = unitGraphRepository.getContactAddressOfOrg(unit.getId());
        Staff staff = staffGraphRepository.findByExternalId(workShift.getPerson().getId());
        AbsenceTypes absenceTypes = absenceTypesRepository.findByName(workShift.getActivity().getName());
        requiredDataForTimeCareTask.put("organizationContactAddress", organizationContactData);
        requiredDataForTimeCareTask.put("staff", staff);
        requiredDataForTimeCareTask.put("absenceTypes", absenceTypes);
        requiredDataForTimeCareTask.put("organization", unit);
        return requiredDataForTimeCareTask;
    }

    public Boolean verifyOrganizationExpertise(OrganizationMappingActivityTypeDTO organizationMappingActivityTypeDTO) {
        List<Long> organizationTypeAndSubTypeIds = new ArrayList<Long>();
        organizationTypeAndSubTypeIds.addAll(organizationMappingActivityTypeDTO.getOrganizationTypes());
        organizationTypeAndSubTypeIds.addAll(organizationMappingActivityTypeDTO.getOrganizationSubTypes());
        Long matchedOrganizationTypeAndSubTypeIdsCount = unitGraphRepository.findAllOrgCountMatchedByIds(organizationTypeAndSubTypeIds);
        if (matchedOrganizationTypeAndSubTypeIdsCount != organizationTypeAndSubTypeIds.size()) {
            exceptionService.dataNotMatchedException(MESSAGE_ORGANIZATION_UPDATE_MISMATCHED);
        }
        return true;
    }

    public List<Long> getAllOrganizationIds() {
        return unitGraphRepository.findAllOrganizationIds();
    }

    public OrganizationTypeAndSubTypeDTO getOrganizationTypeAndSubTypes(Long id) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(id);
        OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO = new OrganizationTypeAndSubTypeDTO();
        if (organizationBaseEntity instanceof Unit) {
            Organization organization = fetchParentOrganization(id);
            organizationTypeAndSubTypeDTO.setParentOrganizationId(organization.getId());
            organizationTypeAndSubTypeDTO.setParent(false);
        } else {
            organizationTypeAndSubTypeDTO.setParent(true);
        }
        List<Long> orgTypeIds = organizationTypeGraphRepository.getOrganizationTypeIdsByUnitId(organizationBaseEntity.getId());
        List<Long> orgSubTypeIds = organizationTypeGraphRepository.getOrganizationSubTypeIdsByUnitId(organizationBaseEntity.getId());
        organizationTypeAndSubTypeDTO.setOrganizationTypes(Optional.ofNullable(orgTypeIds).orElse(Collections.EMPTY_LIST));
        organizationTypeAndSubTypeDTO.setOrganizationSubTypes(Optional.ofNullable(orgSubTypeIds).orElse(Collections.EMPTY_LIST));
        organizationTypeAndSubTypeDTO.setUnitId(organizationBaseEntity.getId());
        return organizationTypeAndSubTypeDTO;
    }

    public OrganizationExternalIdsDTO saveKMDExternalId(Long unitId, OrganizationExternalIdsDTO organizationExternalIdsDTO) {
        Unit unit = unitGraphRepository.findOne(unitId);
        unit.setKmdExternalId(organizationExternalIdsDTO.getKmdExternalId());
        unit.setExternalId(organizationExternalIdsDTO.getTimeCareExternalId());
        unitGraphRepository.save(unit);
        return organizationExternalIdsDTO;

    }

    public TimeSlotsDeductionDTO saveTimeSlotPercentageDeduction(Long unitId, TimeSlotsDeductionDTO timeSlotsDeductionDTO) {
        Unit unit = unitGraphRepository.findOne(unitId);
        unit.setDayShiftTimeDeduction(timeSlotsDeductionDTO.getDayShiftTimeDeduction());
        unit.setNightShiftTimeDeduction(timeSlotsDeductionDTO.getNightShiftTimeDeduction());
        unitGraphRepository.save(unit);
        return timeSlotsDeductionDTO;

    }

    public OrganizationExternalIdsDTO getKMDExternalId(Long unitId) {
        Unit unit = unitGraphRepository.findOne(unitId);
        OrganizationExternalIdsDTO organizationExternalIdsDTO = new OrganizationExternalIdsDTO();
        organizationExternalIdsDTO.setKmdExternalId(unit.getKmdExternalId());
        organizationExternalIdsDTO.setTimeCareExternalId(unit.getExternalId());
        return organizationExternalIdsDTO;

    }

    public TimeSlotsDeductionDTO getTimeSlotPercentageDeduction(Long unitId) {
        OrganizationBaseEntity unit = organizationBaseRepository.findOne(unitId);
        TimeSlotsDeductionDTO timeSlotsDeductionDTO = new TimeSlotsDeductionDTO();
        timeSlotsDeductionDTO.setNightShiftTimeDeduction(unit.getNightShiftTimeDeduction());
        timeSlotsDeductionDTO.setDayShiftTimeDeduction(unit.getDayShiftTimeDeduction());
        return timeSlotsDeductionDTO;

    }

    public List<VehicleQueryResult> getVehicleList(long unitId) {
        Unit unit = unitGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            LOGGER.debug("Searching organization by id " + unitId);
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);

        }
        Long countryId = UserContext.getUserDetails().getCountryId();
        return countryGraphRepository.getResourcesWithFeaturesByCountry(countryId);
    }

    public OrganizationSkillAndOrganizationTypesDTO getOrganizationAvailableSkillsAndOrganizationTypesSubTypes(Long unitId) {
        OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO = this.getOrganizationTypeAndSubTypes(unitId);
        return new OrganizationSkillAndOrganizationTypesDTO(organizationTypeAndSubTypeDTO, skillService.getSkillsOfOrganization(unitId));
    }

    public List<DayType> getDayType(Long unitId, Date date) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        return dayTypeService.getDayTypeByDate(countryId, date);
    }

    public List<DayType> getAllDayTypeofOrganization(Long organizationId) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        return dayTypeService.getAllDayTypeByCountryId(countryId);
    }

    public List<Map<String, Object>> getUnitsByOrganizationIs(Long orgID) {
        return unitGraphRepository.getOrganizationChildList(orgID);
    }

    public Map<String, Object> getAvailableZoneIds(Long unitId) {
        Set<String> allZones = ZoneId.getAvailableZoneIds();
        List<String> zoneList = new ArrayList<>(allZones);
        Collections.sort(zoneList);
        Map<String, Object> timeZonesData = new HashMap<>();
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(unitId);
        if (!Optional.ofNullable(organizationBaseEntity).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);

        }
        timeZonesData.put("selectedTimeZone", organizationBaseEntity.getTimeZone() != null ? organizationBaseEntity.getTimeZone().getId() : null);
        timeZonesData.put("allTimeZones", zoneList);
        return timeZonesData;
    }

    public boolean assignUnitTimeZone(Long unitId, String zoneIdString) {
        ZoneId zoneId = ZoneId.of(zoneIdString);
        if (!Optional.ofNullable(zoneId).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ZONEID_NOTFOUND, zoneIdString);

        }
        Unit unit = unitGraphRepository.findOne(unitId);
        schedulerServiceRestClient.publishRequest(zoneId, unitId, true, IntegrationOperation.CREATE, "/scheduler_panel/time_zone", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
        unit.setTimeZone(zoneId);
        unitGraphRepository.save(unit);
        return true;
    }

    public Organization fetchParentOrganization(Long unitId) {
        Long parentOrgId = organizationBaseRepository.findParentOrgId(unitId);
        Organization parent=  organizationGraphRepository.findOne(parentOrgId);
        if (!Optional.ofNullable(parent).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, unitId);
        }
        return parent;
    }

    public OrganizationMappingDTO getEmploymentTypeWithExpertise(Long unitId) {
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        OrganizationMappingDTO organizationMappingDTO = new OrganizationMappingDTO();
        organizationMappingDTO.setEmploymentTypes(employmentTypeGraphRepository.getAllEmploymentTypeByOrganization(unitId, false));
        organizationMappingDTO.setExpertise(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffRetrievalService.getExpertisesOfUnitByCountryId(countryId, unitId), Expertise.class));
        return organizationMappingDTO;
    }

    public WTABasicDetailsDTO getWTARelatedInfo(Long countryId, Long organizationId, Long organizationSubTypeId, Long organizationTypeId, Long expertiseId, List<Long> unitIds) {
        WTABasicDetailsDTO wtaBasicDetailsDTO = new WTABasicDetailsDTO();
        if (Optional.ofNullable(expertiseId).isPresent()) {
            Expertise expertise = expertiseGraphRepository.findOne(expertiseId, 0);
            if (expertise != null) {
                ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();
                BeanUtils.copyProperties(expertise, expertiseResponseDTO);
                wtaBasicDetailsDTO.setExpertiseResponse(expertiseResponseDTO);
            }
        }
        if (Optional.ofNullable(organizationId).isPresent()) {
            Unit unit = unitGraphRepository.findOne(organizationId, 0);
            if (unit != null) {
                OrganizationBasicDTO organizationBasicDTO = new OrganizationBasicDTO();
                BeanUtils.copyProperties(unit, organizationBasicDTO);
                wtaBasicDetailsDTO.setOrganization(organizationBasicDTO);
            }
        }
        if (Optional.ofNullable(countryId).isPresent()) {
            Country country = countryGraphRepository.findOne(countryId, 0);
            if (country != null) {
                CountryDTO countryDTO = new CountryDTO();
                BeanUtils.copyProperties(country, countryDTO);
                wtaBasicDetailsDTO.setCountryDTO(countryDTO);
            }
        }
        Long orgTypeId = organizationTypeGraphRepository.findOrganizationTypeIdBySubTypeId(organizationSubTypeId);
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(orgTypeId, 0);
        if (Optional.ofNullable(organizationType).isPresent()) {
            OrganizationTypeDTO organizationTypeDTO = new OrganizationTypeDTO();
            BeanUtils.copyProperties(organizationType, organizationTypeDTO);
            wtaBasicDetailsDTO.setOrganizationType(organizationTypeDTO);
        }
        List<Unit> units;
        units = CollectionUtils.isNotEmpty(unitIds) ? unitGraphRepository.findOrganizationsByIdsIn(unitIds) : organizationTypeGraphRepository.getOrganizationsByOrganizationTypeId(organizationSubTypeId);
        units = units.stream().filter(unit -> unit.isWorkcentre()).collect(Collectors.toList());
        if (Optional.ofNullable(organizationSubTypeId).isPresent()) {
            OrganizationType organizationSubType = organizationTypeGraphRepository.findOne(organizationSubTypeId, 0);
            if (Optional.ofNullable(organizationSubType).isPresent()) {
                OrganizationTypeDTO organizationSubTypeDTO = new OrganizationTypeDTO();
                BeanUtils.copyProperties(organizationSubType, organizationSubTypeDTO);
                wtaBasicDetailsDTO.setOrganizationSubType(organizationSubTypeDTO);
            }
        }
        if (CollectionUtils.isNotEmpty(units)) {
            List<OrganizationBasicDTO> organizationBasicDTOS = new ArrayList<>();
            units.forEach(organization -> {
                OrganizationBasicDTO organizationBasicDTO = new OrganizationBasicDTO();
                ObjectMapperUtils.copyProperties(organization, organizationBasicDTO);
                organizationBasicDTOS.add(organizationBasicDTO);
            });
            wtaBasicDetailsDTO.setOrganizations(organizationBasicDTOS);
        }
        return wtaBasicDetailsDTO;
    }

    public ZoneId getTimeZoneStringOfUnit(Long unitId) {
        Unit unit = unitGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        return unit.getTimeZone();
    }

    public OrganizationCategory getOrganizationCategory(CompanyType companyType) {
        OrganizationCategory organizationCategory;
        switch (companyType) {
            case HUB: {
                organizationCategory = OrganizationCategory.HUB;
                break;
            }
            case UNION: {
                organizationCategory = OrganizationCategory.UNION;
                break;
            }
            default: {
                organizationCategory = OrganizationCategory.ORGANIZATION;
            }
        }
        return organizationCategory;
    }

    public OrderDefaultDataWrapper getDefaultDataForOrder(long unitId) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        OrderAndActivityDTO orderAndActivityDTO = activityIntegrationService.getAllOrderAndActivitiesByUnit(unitId);
        List<Skill> skills = skillGraphRepository.findAllSkillsByCountryId(countryId);
        List<Long> allUnitIds = organizationBaseRepository.fetchAllUnitIds(unitId);
        OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(allUnitIds);
        List<Expertise> expertise = new ArrayList<>();
        if (isNotNull(servicesAndLevel)) {
            expertise = expertiseGraphRepository.getExpertiseByCountryAndOrganizationServices(countryId, servicesAndLevel.getServicesId());

        } else {
            LOGGER.info("Organization Services or Level is not present for Unit id {}", unitId);
        }
        List<StaffPersonalDetailQueryResult> staffList = staffGraphRepository.getAllStaffWithMobileNumber(unitId);
        List<PresenceTypeDTO> plannedTypes = plannedTimeTypeRestClient.getAllPlannedTimeTypes(countryId);
        List<FunctionDTO> functions = functionGraphRepository.findFunctionsIdAndNameByCountry(countryId);
        List<ReasonCodeResponseDTO> reasonCodes = reasonCodeGraphRepository.findReasonCodesByUnitIdAndReasonCodeType(unitId, ReasonCodeType.ORDER);
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        return new OrderDefaultDataWrapper(orderAndActivityDTO.getOrders(), orderAndActivityDTO.getActivities(), skills, expertise, staffList, plannedTypes, functions, reasonCodes, dayTypes, orderAndActivityDTO.getMinOpenShiftHours(), orderAndActivityDTO.getCounters());
    }

    public RuleTemplateDefaultData getDefaultDataForRuleTemplate(long countryId) {
        List<OrganizationTypeAndSubType> organizationTypeAndSubTypes = organizationTypeGraphRepository.getAllOrganizationTypeAndSubType(countryId);
        PriorityGroupDefaultData priorityGroupDefaultData1 = employmentTypeService.getExpertiseAndEmployment(countryId, false);
        List<Skill> skills = skillGraphRepository.findAllSkillsByCountryId(countryId);
        ActivityWithTimeTypeDTO activityWithTimeTypeDTOS = activityIntegrationService.getAllActivitiesAndTimeTypes(countryId);
        return new RuleTemplateDefaultData(organizationTypeAndSubTypes, skills, activityWithTimeTypeDTOS.getTimeTypeDTOS(), activityWithTimeTypeDTOS.getActivityDTOS(), activityWithTimeTypeDTOS.getIntervals(), priorityGroupDefaultData1.getEmploymentTypes(), priorityGroupDefaultData1.getExpertises(), activityWithTimeTypeDTOS.getCounters());
    }

    public WTADefaultDataInfoDTO getWtaTemplateDefaultDataInfoByUnitId(Long unitId) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeRestClient.getAllPlannedTimeTypes(countryId);
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(unitId);
        List<TimeSlotDTO> timeSlotDTOS = timeSlotService.getShiftPlanningTimeSlotByUnit(organizationBaseEntity);
        List<DayTypeDTO> dayTypeDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(dayTypes,DayTypeDTO.class);
        return new WTADefaultDataInfoDTO(dayTypeDTOS, presenceTypeDTOS, timeSlotDTOS, countryId);
    }

    public RuleTemplateDefaultData getDefaultDataForRuleTemplateByUnit(Long unitId) {
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        List<Skill> skills = skillGraphRepository.findAllSkillsByCountryId(countryId);
        ActivityWithTimeTypeDTO activityWithTimeTypeDTOS = activityIntegrationService.getAllActivitiesAndTimeTypesByUnit(unitId, countryId);
        PriorityGroupDefaultData priorityGroupDefaultData1 = employmentTypeService.getExpertiseAndEmployment(countryId, false);
        return new RuleTemplateDefaultData(skills, activityWithTimeTypeDTOS.getTimeTypeDTOS(), activityWithTimeTypeDTOS.getActivityDTOS(), activityWithTimeTypeDTOS.getIntervals(), priorityGroupDefaultData1.getEmploymentTypes(), priorityGroupDefaultData1.getExpertises(), activityWithTimeTypeDTOS.getMinOpenShiftHours(), activityWithTimeTypeDTOS.getCounters());
    }

    public OrganizationSettingDTO updateOrganizationSettings(OrganizationSettingDTO organizationSettingDTO, Long unitId) {
        OrganizationSetting organizationSetting = unitGraphRepository.getOrganisationSettingByOrgId(unitId);
        organizationSetting.setWalkingMeter(organizationSettingDTO.getWalkingMeter());
        organizationSetting.setWalkingMinutes(organizationSettingDTO.getWalkingMinutes());
        openingSettingsGraphRepository.save(organizationSetting);
        return organizationSettingDTO;
    }

    public OrganizationSettingDTO getOrganizationSettings(Long unitId) {
        OrganizationSetting organizationSetting = unitGraphRepository.getOrganisationSettingByOrgId(unitId);
        return new OrganizationSettingDTO(organizationSetting.getWalkingMeter(), organizationSetting.getWalkingMinutes());
    }

    public List<UnitAndParentOrganizationAndCountryDTO> getParentOrganizationAndCountryIdsOfUnit() {
        List<Map<String, Object>> parentOrganizationAndCountryData = unitGraphRepository.getUnitAndParentOrganizationAndCountryIds();
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(parentOrganizationAndCountryData, UnitAndParentOrganizationAndCountryDTO.class);
    }

    public CTABasicDetailsDTO getCTABasicDetailInfo(Long expertiseId, Long organizationSubTypeId, Long countryId, List<Long> unitIds) {
        CTABasicDetailsDTO ctaBasicDetailsDTO = new CTABasicDetailsDTO();
        if (Optional.ofNullable(expertiseId).isPresent()) {
            Expertise expertise = expertiseGraphRepository.findOne(expertiseId, 0);
            if (expertise != null) {
                ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();
                BeanUtils.copyProperties(expertise, expertiseResponseDTO);
                ctaBasicDetailsDTO.setExpertise(expertiseResponseDTO);
            }
        }
        if (Optional.ofNullable(countryId).isPresent()) {
            Country country = countryGraphRepository.findOne(countryId, 0);
            if (country != null) {
                CountryDTO countryDTO = new CountryDTO();
                BeanUtils.copyProperties(country, countryDTO);
                ctaBasicDetailsDTO.setCountryDTO(countryDTO);
            }
        }
        if (organizationSubTypeId != null) {
            OrganizationType organizationType = organizationTypeGraphRepository.findOrganizationTypeBySubTypeId(organizationSubTypeId);
            if (Optional.ofNullable(organizationType).isPresent()) {
                OrganizationTypeDTO organizationTypeDTO = new OrganizationTypeDTO();
                BeanUtils.copyProperties(organizationType, organizationTypeDTO);
                ctaBasicDetailsDTO.setOrganizationType(organizationTypeDTO);
            }
        }
        OrganizationType organizationSubType = organizationTypeGraphRepository.findOne(organizationSubTypeId, 0);
        List<Unit> units;
        if (CollectionUtils.isNotEmpty(unitIds)) {
            units = unitGraphRepository.findOrganizationsByIdsIn(unitIds);
        } else {
            units = organizationTypeGraphRepository.getOrganizationsByOrganizationTypeId(organizationSubTypeId);
        }
        units = units.stream().filter(unit -> unit.isWorkcentre()).collect(Collectors.toList());
        if (Optional.ofNullable(organizationSubType).isPresent()) {
            OrganizationTypeDTO organizationSubTypeDTO = new OrganizationTypeDTO();
            BeanUtils.copyProperties(organizationSubType, organizationSubTypeDTO);
            ctaBasicDetailsDTO.setOrganizationSubType(organizationSubTypeDTO);
        }
        if (CollectionUtils.isNotEmpty(units)) {
            List<OrganizationBasicDTO> organizationBasicDTOS = new ArrayList<>();
            units.forEach(organization -> {
                OrganizationBasicDTO organizationBasicDTO = new OrganizationBasicDTO();
                ObjectMapperUtils.copyProperties(organization, organizationBasicDTO);
                organizationBasicDTOS.add(organizationBasicDTO);
            });
            ctaBasicDetailsDTO.setOrganizations(organizationBasicDTOS);
        }
        return ctaBasicDetailsDTO;
    }

    public List<OrgTypeQueryResult> getOrganizationIdsBySubOrgTypeId(List<Long> orgTypeId) {
        return unitGraphRepository.getOrganizationIdsBySubOrgTypeId(orgTypeId);
    }

    public Map<Long, String> getTimeZoneStringsOfAllUnits() {
        List<OrganizationBasicResponse> organizationBasicResponses = unitGraphRepository.findTimezoneforAllorganizations();
        return organizationBasicResponses.stream().collect(HashMap::new, (m, v) -> m.put(v.getId(), v.getTimezone()), HashMap::putAll);
    }

    public Map<Long, String> getTimeZoneStringsByUnitIds(Set<Long> unitIds) {
        List<OrganizationBasicResponse> organizationBasicResponses = unitGraphRepository.findTimezoneByUnitIds(unitIds);
        return organizationBasicResponses.stream().collect(Collectors.toMap(OrganizationBasicResponse::getId, OrganizationBasicResponse::getTimezone));
    }

        public List<Long> getOrganizationIds(Long unitId) {
        List<Long> organizationIds = null;
        if (isNull(unitId)) {
            organizationIds = unitGraphRepository.findAllOrganizationIds();
        } else {
            Optional<Organization> optionalOrganization = organizationGraphRepository.findById(unitId);
            if (optionalOrganization.isPresent()) {
                organizationIds = optionalOrganization.get().getUnits().stream().map(unit -> unit.getId()).collect(Collectors.toList());
            }
        }
        return organizationIds;
    }

    public SelfRosteringMetaData getPublicHolidaysReasonCodeAndDayTypeUnitId(long unitId) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unitId);
        List<ReasonCodeDTO> reasonCodes = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(reasonCodeGraphRepository.findReasonCodeByUnitId(unitId), ReasonCodeDTO.class);
        return new SelfRosteringMetaData(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(dayTypeService.getAllDayTypeByCountryId(countryId), com.kairos.dto.user.country.day_type.DayType.class), new ReasonCodeWrapper(reasonCodes, userAccessRoleDTO), FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId)));
    }

    public boolean isUnit(Long unitId) {
        return unitGraphRepository.findOne(unitId) != null;
    }

    public boolean takeOverOrganization(Long organizationId, Long newOrganizationId) {
        boolean organizationValid = organizationGraphRepository.verifyOrganization(Arrays.asList(organizationId, newOrganizationId));
        if (!organizationValid) {
            exceptionService.actionNotPermittedException("Please provide valid organizations");
        }
        organizationGraphRepository.mergeTwoOrganizations(organizationId, newOrganizationId);
        return true;
    }




    public ShiftFilterDefaultData getFilterDataBySelfRosteringFilter(SelfRosteringFilterDTO selfRosteringFilterDTO) {
        List<TimeSlotDTO> timeSlotDTOS = timeSlotService.getUnitTimeSlot(selfRosteringFilterDTO.getUnitId());
        List<BigInteger> teamActivityIds = teamGraphRepository.getTeamActivityIdsByTeamIds(selfRosteringFilterDTO.getTeamIds().stream().map(value -> new Long(value)).collect(Collectors.toList()));
        return new ShiftFilterDefaultData(timeSlotDTOS,teamActivityIds);
    }

    public List<Long> getAllUnitIdsByCountryId(Long countryId) {
        return organizationGraphRepository.getAllUnitsByCountryId(countryId);
    }

    public OrganizationCategory getOrganisationCategory(Long organizationId){
        OrganizationCategory organizationCategory = null;
        if(unitService.isUnit(organizationId)){
            organizationCategory = OrganizationCategory.ORGANIZATION;
        }else {
            Optional<Organization> organizationOptional = organizationGraphRepository.findById(organizationId);
            if(organizationOptional.isPresent()){
                organizationCategory = organizationOptional.get().getOrganizationCategory();
            }
        }
        return organizationCategory;
    }

    public List<Organization> getAllUnionsByOrganizationOrCountryId(Long organizationId,Long countryId){
        return organizationGraphRepository.getAllUnionsByOrganizationOrCountryId(organizationId,countryId);
    }

    private List<Long> getAllOrganizationIdsToDelete(OrganizationBaseEntity organizationBaseEntity){
        if(organizationBaseEntity instanceof Unit){
            return Collections.singletonList(organizationBaseEntity.getId());
        }else {
            List<Long> organizationIdsToDelete=new ArrayList<>();
            Organization organization=(Organization) organizationBaseEntity;
            organizationIdsToDelete.add(organization.getId());
            organizationIdsToDelete.addAll(organization.getChildren().stream().map(UserBaseEntity::getId).collect(Collectors.toList()));
            organizationIdsToDelete.addAll(organization.getUnits().stream().map(UserBaseEntity::getId).collect(Collectors.toList()));
            return organizationIdsToDelete;
        }
    }

}
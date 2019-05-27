package com.kairos.service.organization;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.OrganizationMappingActivityTypeDTO;
import com.kairos.dto.activity.cta.CTABasicDetailsDTO;
import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
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
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.OrganizationCategory;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.country.default_data.*;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.organization.AbsenceTypes;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.services.OrganizationServicesAndLevelQueryResult;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.model.staff.personal_details.OrganizationStaffWrapper;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.counter.OrgTypeQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.OrderAndActivityDTO;
import com.kairos.persistence.model.user.expertise.Response.OrderDefaultDataWrapper;
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
import com.kairos.rest_client.PhaseRestClient;
import com.kairos.rest_client.PlannedTimeTypeRestClient;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.client.ClientService;
import com.kairos.service.country.CitizenStatusService;
import com.kairos.service.country.DayTypeService;
import com.kairos.service.country.EmploymentTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.region.RegionService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffRetrievalService;
import com.kairos.utils.FormatUtil;
import com.kairos.utils.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResult;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.DateUtils.parseDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.TEAM;
import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION;

@Transactional
@Service
public class OrganizationService {
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
    private EmployeeLimitGraphRepository employeeLimitGraphRepository;
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
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private TeamService teamService;
    @Inject
    private PhaseRestClient phaseRestClient;
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

    private final static Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);

    public Unit getOrganizationById(long id) {
        return organizationGraphRepository.findOne(id);
    }

    public OrganizationDTO getOrganizationWithCountryId(long id) {
        Unit unit = organizationGraphRepository.findOne(id);
        Country country = unit.isParentOrganization() ? organizationGraphRepository.getCountry(unit.getId()) : organizationGraphRepository.getCountryByParentOrganization(unit.getId());
        OrganizationDTO organizationDTO = ObjectMapperUtils.copyPropertiesByMapper(unit, OrganizationDTO.class);
        organizationDTO.setCountryId(country.getId());
        organizationDTO.setParentOrganization(unit.isParentOrganization());
        return organizationDTO;
    }

    public boolean showCountryTagForOrganization(long id) {
        Unit unit = organizationGraphRepository.findOne(id);
        return unit.isShowCountryTags();
    }

    public Long getCountryIdOfOrganization(long orgId) {
        Unit unit = fetchParentOrganization(orgId);
        Country country = organizationGraphRepository.getCountry(unit.getId());
        return country != null ? country.getId() : null;
    }

    public Unit createOrganization(Unit unit, Long id, boolean baseOrganization) {
        Unit parent = (id == null) ? null : getOrganizationById(id);
        if(parent != null) {
            organizationGraphRepository.save(unit);
            Unit o = organizationGraphRepository.createChildOrganization(parent.getId(), unit.getId());
        } else {
            unit = organizationGraphRepository.save(unit);
            if(!baseOrganization && !unit.getOrganizationLevel().equals(OrganizationLevel.COUNTRY)) {
                organizationGraphRepository.linkWithRegionLevelOrganization(unit.getId());
            }
        }
        timeSlotService.createDefaultTimeSlots(unit, TimeSlotType.SHIFT_PLANNING);
        timeSlotService.createDefaultTimeSlots(unit, TimeSlotType.TASK_PLANNING);
        if(!baseOrganization) {
            accessGroupService.createDefaultAccessGroups(unit);
            organizationGraphRepository.assignDefaultSkillsToOrg(unit.getId(), DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
        }
        return unit;
    }

    public List<Map<String, Object>> getAllOrganization() {
        return organizationGraphRepository.findAllOrganizations();
    }

    public boolean deleteOrganization(long organizationId) {
        Unit unit = organizationGraphRepository.findOne(organizationId);
        boolean success;
        if(unit != null && unit.isBoardingCompleted()) {
            unit.setEnable(false);
            unit.setDeleted(true);
            organizationGraphRepository.save(unit);
            success = true;
        } else {
            List<Long> organizationIdsToDelete = new ArrayList<>();
            organizationIdsToDelete.add(unit.getId());
            organizationIdsToDelete.addAll(unit.getChildren().stream().map(child -> child.getId()).collect(Collectors.toList()));
            organizationGraphRepository.removeOrganizationCompletely(organizationIdsToDelete);
            success = true;
        }
        return success;
    }

    public List<Map<String, Object>> getUnits(long organizationId) {
        return organizationGraphRepository.getUnits(organizationId);
    }

    public Map<String, Object> getGeneralDetails(long id, String type) {
        Map<String, Object> response = new HashMap<>();
        if(ORGANIZATION.equalsIgnoreCase(type)) {
            Unit unit = organizationGraphRepository.findOne(id, 0);
            if(unit == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, id);
            }
            Map<String, Object> metaData = null;
            Long countryId = countryGraphRepository.getCountryIdByUnitId(id);
            List<Map<String, Object>> data = organizationGraphRepository.getGeneralTabMetaData(countryId);
            for (Map<String, Object> map : data) {
                metaData = (Map<String, Object>) map.get("data");
            }
            Map<String, Object> cloneMap = new HashMap<>(metaData);
            OrganizationContactAddress organizationContactAddress = organizationGraphRepository.getContactAddressOfOrg(id);
            ZipCode zipCode = organizationContactAddress.getZipCode();
            List<Municipality> municipalities = (zipCode == null) ? Collections.emptyList() : municipalityGraphRepository.getMunicipalitiesByZipCode(zipCode.getId());
            Map<String, Object> generalTabQueryResult = organizationGraphRepository.getGeneralTabInfo(unit.getId());
            HashMap<String, Object> generalTabInfo = new HashMap<>(generalTabQueryResult);
            generalTabInfo.put("clientSince", (generalTabInfo.get("clientSince") == null ? null : getDate((long) generalTabInfo.get("clientSince"))));
            cloneMap.put("municipalities", municipalities);
            response.put("generalTabInfo", generalTabInfo);
            response.put("otherData", cloneMap);
        } else if(TEAM.equalsIgnoreCase(type)) {
            Team team = teamGraphRepository.findOne(id);
            if(team == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_TEAM_ID_NOTFOUND);
            }
            Map<String, Object> teamInfo = new HashMap<>();
            teamInfo.put("name", team.getName());
            teamInfo.put("id", team.getId());
            response.put("generalTabInfo", teamInfo);
            response.put("otherData", Collections.emptyMap());
        }
        return response;
    }

    public boolean updateOrganizationGeneralDetails(OrganizationGeneral organizationGeneral, long unitId) throws ParseException {
        Unit unit = organizationGraphRepository.findOne(unitId);
        if(unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, unitId);
        }
        OwnershipType ownershipType = null;
        ContractType contractType = null;
        IndustryType industryType = null;
        EmployeeLimit employeeLimit = null;
        KairosStatus kairosStatus = null;
        VatType vatType = null;
        if(organizationGeneral.getOwnershipTypeId() != null)
            ownershipType = ownershipTypeGraphRepository.findOne(organizationGeneral.getOwnershipTypeId());
        List<BusinessType> businessTypes = businessTypeGraphRepository.findByIdIn(organizationGeneral.getBusinessTypeId());
        if(organizationGeneral.getContractTypeId() != null)
            contractType = contractTypeGraphRepository.findOne(organizationGeneral.getContractTypeId());
        if(organizationGeneral.getIndustryTypeId() != null)
            industryType = industryTypeGraphRepository.findOne(organizationGeneral.getIndustryTypeId());
        if(organizationGeneral.getEmployeeLimitId() != null)
            employeeLimit = employeeLimitGraphRepository.findOne(organizationGeneral.getEmployeeLimitId());
        if(organizationGeneral.getVatTypeId() != null)
            vatType = vatTypeGraphRepository.findOne(organizationGeneral.getVatTypeId());
        if(organizationGeneral.getKairosStatusId() != null)
            kairosStatus = kairosStatusGraphRepository.findOne(organizationGeneral.getKairosStatusId());
        ContactAddress contactAddress = unit.getContactAddress();
        if(contactAddress != null) {
            Municipality municipality = municipalityGraphRepository.findOne(organizationGeneral.getMunicipalityId());
            if(municipality == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_MUNICIPALITY_NOTFOUND);
            }
            contactAddress.setMunicipality(municipality);
        }
        Optional<OrganizationType> organizationTypes = organizationTypeGraphRepository.findById(organizationGeneral.getOrganizationTypeId());
        List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationGeneral.getOrganizationSubTypeId());
        unit.setContactAddress(contactAddress);
        unit.setName(organizationGeneral.getName());
        unit.setShortName(organizationGeneral.getShortName());
        unit.setCvrNumber(organizationGeneral.getCvrNumber());
        unit.setpNumber(organizationGeneral.getpNumber());
        unit.setWebSiteUrl(organizationGeneral.getWebsiteUrl());
        unit.setEmployeeLimit(employeeLimit);
        unit.setDescription(organizationGeneral.getDescription());
        unit.setOrganizationType(organizationTypes.get());
        unit.setOrganizationSubTypes(organizationSubTypes);
        unit.setVatType(vatType);
        unit.setEanNumber(organizationGeneral.getEanNumber());
        unit.setCostCenterCode(organizationGeneral.getCostCenterCode());
        unit.setCostCenterName(organizationGeneral.getCostCenterName());
        unit.setOwnershipType(ownershipType);
        unit.setBusinessTypes(businessTypes);
        unit.setIndustryType(industryType);
        unit.setContractType(contractType);
        unit.setClientSince(parseDate(organizationGeneral.getClientSince()).getTime());
        unit.setKairosHub(organizationGeneral.isKairosHub());
        unit.setKairosStatus(kairosStatus);
        unit.setExternalId(organizationGeneral.getExternalId());
        unit.setEndTimeDeduction(organizationGeneral.getPercentageWorkDeduction());
        unit.setKmdExternalId(organizationGeneral.getKmdExternalId());
        unit.setDayShiftTimeDeduction(organizationGeneral.getDayShiftTimeDeduction());
        unit.setNightShiftTimeDeduction(organizationGeneral.getNightShiftTimeDeduction());
        organizationGraphRepository.save(unit);
        return true;

    }

    public Map<String, Object> getParentOrganization(Long countryId) {
        Map<String, Object> data = new HashMap<>(2);
        List<OrganizationBasicResponse> organizationQueryResult = organizationGraphRepository.getAllParentOrganizationOfCountry(countryId);
        OrganizationCreationData organizationCreationData = organizationGraphRepository.getOrganizationCreationData(countryId);
        List<Map<String, Object>> zipCodes = FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId));
        if(Optional.ofNullable(organizationCreationData).isPresent()) {
            organizationCreationData.setZipCodes(zipCodes);
        }
        organizationCreationData.setCompanyTypes(CompanyType.getListOfCompanyType());
        organizationCreationData.setCompanyUnitTypes(CompanyUnitType.getListOfCompanyUnitType());
        organizationCreationData.setAccessGroups(accessGroupService.getCountryAccessGroupsForOrganizationCreation(countryId));
        organizationCreationData.setHubList(organizationGraphRepository.getAllHubByCountryId(countryId));
        data.put("globalData", organizationCreationData);
        data.put("organization", organizationQueryResult);
        return data;
    }

    public List<OrganizationBasicResponse> getOrganizationGdprAndWorkcenter(Long organizationId) {
        List<OrganizationBasicResponse> organizationQueryResult = organizationGraphRepository.getOrganizationGdprAndWorkCenter(organizationId);
        List<Long> unitIds = organizationQueryResult.stream().map(organizationBasicResponse -> organizationBasicResponse.getId()).collect(Collectors.toList());
        List<Map<String, Object>> organizationContactAddress = organizationGraphRepository.getContactAddressOfParentOrganization(unitIds);
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS = userGraphRepository.getUnitManagerOfOrganization(unitIds, organizationId);
        for (OrganizationBasicResponse organizationData : organizationQueryResult) {
            for (Map<String, Object> address : organizationContactAddress) {
                if(address.get("organizationId").equals(organizationData.getId())) {
                    organizationData.setContactAddress(address);
                    break;
                }
            }
            Optional<StaffPersonalDetailDTO> currentStaff = staffPersonalDetailDTOS.stream().filter(staffPersonalDetailDTO -> staffPersonalDetailDTO.getOrganizationId().equals(organizationData.getId())).findFirst();
            organizationData.setUnitManager(currentStaff.isPresent() ? currentStaff.get() : null);
        }
        return organizationQueryResult;
    }

    public Unit getByPublicPhoneNumber(String phoneNumber) {
        return organizationGraphRepository.findOrganizationByPublicPhoneNumber(phoneNumber);
    }

    public Unit getOrganizationByExternalId(String externalId) {
        return organizationGraphRepository.findByExternalId(externalId);
    }

    public OrganizationStaffWrapper getOrganizationAndStaffByExternalId(String externalId, Long staffExternalId, Long staffTimeCareEmploymentId) {
        OrganizationStaffWrapper organizationStaffWrapper = staffGraphRepository.getStaff(staffExternalId, staffTimeCareEmploymentId);
        if(Optional.ofNullable(organizationStaffWrapper).isPresent()) {
            organizationStaffWrapper.setUnit(organizationGraphRepository.findByExternalId(externalId));
        }
        return organizationStaffWrapper;
    }

    public boolean deleteOrganizationById(long parentOrganizationId, long childOrganizationId) {
        organizationGraphRepository.deleteChildRelationOrganizationById(parentOrganizationId, childOrganizationId);
        organizationGraphRepository.deleteOrganizationById(childOrganizationId);
        return organizationGraphRepository.findOne(childOrganizationId) == null;
    }

    public Unit updateExternalId(long unitId, long externalId) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        if(unit == null) {
            return null;
        }
        unit.setExternalId(String.valueOf(externalId));
        organizationGraphRepository.save(unit);
        return unit;
    }

    public Map setEstimoteCredentials(long organization, Map<String, String> payload) {
        Unit unitObj = organizationGraphRepository.findOne(organization);
        if(unitObj != null && payload.containsKey("estimoteAppId") && payload.containsKey("estimoteAppToken") && payload.get("estimoteAppId") != null && payload.get("estimoteAppToken") != null) {
            unitObj.setEstimoteAppId(payload.get("estimoteAppId"));
            unitObj.setEstimoteAppToken(payload.get("estimoteAppToken"));
            return payload;
        }
        return null;
    }

    public Map getEstimoteCredentials(long organization) {
        Map returnData = new HashMap();
        Unit unitObj = organizationGraphRepository.findOne(organization);
        if(unitObj != null) {
            returnData.put("estimoteAppId", unitObj.getEstimoteAppId());
            returnData.put("estimoteAppToken", unitObj.getEstimoteAppToken());
        }
        return returnData;
    }

    public Boolean createLinkParentWithChildOrganization(Long parentId, Long childId) {
        Unit parent = organizationGraphRepository.findOne(parentId);
        Unit child = organizationGraphRepository.findOne(childId);
        organizationGraphRepository.createChildOrganization(parent.getId(), child.getId());
        accessGroupService.createDefaultAccessGroups(child);
        timeSlotService.createDefaultTimeSlots(child, TimeSlotType.SHIFT_PLANNING);
        timeSlotService.createDefaultTimeSlots(child, TimeSlotType.TASK_PLANNING);
        return true;
    }

    public Long getOrganizationIdByTeamIdOrGroupIdOrOrganizationId(String type, Long id) {
        if(ORGANIZATION.equalsIgnoreCase(type)) {
            Unit unit = organizationGraphRepository.findOne(id);
            if(unit == null) {
                return null;
            }
            return unit.getId();
        } else if(TEAM.equalsIgnoreCase(type)) {
            return organizationGraphRepository.getOrganizationByTeamId(id).getId();
        }
        return null;
    }

    public Integer checkDuplicationOrganizationRelation(Long organizationId, Long unitId) {
        return organizationGraphRepository.checkParentChildRelation(organizationId, unitId);
    }

    public Map<String, Object> getCommonDataOfOrganization(Long unitId) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> organizationSkills = organizationGraphRepository.getSkillsOfParentOrganization(unitId);
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
        if(timeSlotData != null) {
            unitData.put("timeSlotList", timeSlotData);
        }
        organizationResult.put("unitData", unitData);
        List<Map<String, Object>> citizenList = clientService.getOrganizationClientsExcludeDead(unitId);
        organizationResult.put("citizenList", citizenList);
        return organizationResult;
    }

    public boolean updateOneTimeSyncsettings(long unitId) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        if(unit == null) {
            LOGGER.debug("Searching organization by id " + unitId);
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        unit.setOneTimeSyncPerformed(true);
        organizationGraphRepository.save(unit);
        return true;
    }

    public boolean updateAutoGenerateTaskSettings(long unitId) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        if(unit == null) {
            LOGGER.debug("Searching organization by id " + unitId);
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        unit.setAutoGeneratedPerformed(true);
        organizationGraphRepository.save(unit);
        return true;
    }

    public Map<String, Object> getTaskDemandSupplierInfo(Long unitId) {
        Map<String, Object> supplierInfo = new HashMap();
        Unit weekdaySupplier = organizationGraphRepository.findOne(unitId, 0);
        supplierInfo.put("weekdaySupplier", weekdaySupplier.getName());
        supplierInfo.put("weekdaySupplierId", weekdaySupplier.getId());
        return supplierInfo;
    }

    public Unit getParentOrganizationOfCityLevel(Long unitId) {
        return organizationGraphRepository.getParentOrganizationOfCityLevel(unitId);
    }

    public Unit getParentOfOrganization(Long unitId) {
        return organizationGraphRepository.getParentOfOrganization(unitId);
    }

    public Unit getOrganizationByTeamId(Long teamId) {
        return organizationGraphRepository.getOrganizationByTeamId(teamId);
    }

    public Map<String, Object> getPrerequisitesForTimeCareTask(GetWorkShiftsFromWorkPlaceByIdResult workShift) {
        Unit unit = organizationGraphRepository.findByExternalId(workShift.getWorkPlace().getId().toString());
        if(unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_EXTERNALID_NOTFOUND);
        }
        Map<String, Object> requiredDataForTimeCareTask = new HashMap<>();
        OrganizationContactAddress organizationContactData = organizationGraphRepository.getContactAddressOfOrg(unit.getId());
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
        Long matchedOrganizationTypeAndSubTypeIdsCount = organizationGraphRepository.findAllOrgCountMatchedByIds(organizationTypeAndSubTypeIds);
        if(matchedOrganizationTypeAndSubTypeIdsCount != organizationTypeAndSubTypeIds.size()) {
            exceptionService.dataNotMatchedException(MESSAGE_ORGANIZATION_UPDATE_MISMATCHED);
        }
        return true;
    }

    public List<Long> getAllOrganizationIds() {
        return organizationGraphRepository.findAllOrganizationIds();
    }

    public OrganizationTypeAndSubTypeDTO getOrganizationTypeAndSubTypes(Long id, String type) {
        Unit unit = getOrganizationDetail(id, type);
        OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO = new OrganizationTypeAndSubTypeDTO();
        if(!unit.isParentOrganization()) {
            Unit parentUnit = organizationGraphRepository.getParentOfOrganization(unit.getId());
            organizationTypeAndSubTypeDTO.setParentOrganizationId(parentUnit.getId());
            organizationTypeAndSubTypeDTO.setParent(false);
        } else {
            organizationTypeAndSubTypeDTO.setParent(true);
        }
        List<Long> orgTypeIds = organizationTypeGraphRepository.getOrganizationTypeIdsByUnitId(unit.getId());
        List<Long> orgSubTypeIds = organizationTypeGraphRepository.getOrganizationSubTypeIdsByUnitId(unit.getId());
        organizationTypeAndSubTypeDTO.setOrganizationTypes(Optional.ofNullable(orgTypeIds).orElse(Collections.EMPTY_LIST));
        organizationTypeAndSubTypeDTO.setOrganizationSubTypes(Optional.ofNullable(orgSubTypeIds).orElse(Collections.EMPTY_LIST));
        organizationTypeAndSubTypeDTO.setUnitId(unit.getId());
        return organizationTypeAndSubTypeDTO;
    }

    public OrganizationExternalIdsDTO saveKMDExternalId(Long unitId, OrganizationExternalIdsDTO organizationExternalIdsDTO) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        unit.setKmdExternalId(organizationExternalIdsDTO.getKmdExternalId());
        unit.setExternalId(organizationExternalIdsDTO.getTimeCareExternalId());
        organizationGraphRepository.save(unit);
        return organizationExternalIdsDTO;

    }

    public TimeSlotsDeductionDTO saveTimeSlotPercentageDeduction(Long unitId, TimeSlotsDeductionDTO timeSlotsDeductionDTO) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        unit.setDayShiftTimeDeduction(timeSlotsDeductionDTO.getDayShiftTimeDeduction());
        unit.setNightShiftTimeDeduction(timeSlotsDeductionDTO.getNightShiftTimeDeduction());
        organizationGraphRepository.save(unit);
        return timeSlotsDeductionDTO;

    }

    public OrganizationExternalIdsDTO getKMDExternalId(Long unitId) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        OrganizationExternalIdsDTO organizationExternalIdsDTO = new OrganizationExternalIdsDTO();
        organizationExternalIdsDTO.setKmdExternalId(unit.getKmdExternalId());
        organizationExternalIdsDTO.setTimeCareExternalId(unit.getExternalId());
        return organizationExternalIdsDTO;

    }

    public TimeSlotsDeductionDTO getTimeSlotPercentageDeduction(Long unitId) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        TimeSlotsDeductionDTO timeSlotsDeductionDTO = new TimeSlotsDeductionDTO();
        timeSlotsDeductionDTO.setNightShiftTimeDeduction(unit.getNightShiftTimeDeduction());
        timeSlotsDeductionDTO.setDayShiftTimeDeduction(unit.getDayShiftTimeDeduction());
        return timeSlotsDeductionDTO;

    }

    public List<VehicleQueryResult> getVehicleList(long unitId) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        if(!Optional.ofNullable(unit).isPresent()) {
            LOGGER.debug("Searching organization by id " + unitId);
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);

        }
        Long countryId = UserContext.getUserDetails().getCountryId();
        return countryGraphRepository.getResourcesWithFeaturesByCountry(countryId);
    }

    public Long getOrganization(Long id, String type) {
        Unit unit = getOrganizationDetail(id, type);
        return unit.getId();
    }

    public Unit getOrganizationDetail(Long id, String type) {
        Unit unit = null;
        switch (type.toUpperCase()) {
            case ORGANIZATION:
                unit = organizationGraphRepository.findOne(id, 1);
                break;
            case TEAM:
                unit = teamService.getOrganizationByTeamId(id);
                break;
            default:
                exceptionService.unsupportedOperationException(MESSAGE_ORGANIZATION_TYPE_NOTVALID);
        }
        if(!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, id);

        }
        return unit;
    }

    public OrganizationSkillAndOrganizationTypesDTO getOrganizationAvailableSkillsAndOrganizationTypesSubTypes(Long unitId) {
        OrganizationTypeAndSubTypeDTO organizationTypeAndSubTypeDTO = this.getOrganizationTypeAndSubTypes(unitId, "organization");
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
        return organizationGraphRepository.getOrganizationChildList(orgID);
    }

    public Map<String, Object> getAvailableZoneIds(Long unitId) {
        Set<String> allZones = ZoneId.getAvailableZoneIds();
        List<String> zoneList = new ArrayList<>(allZones);
        Collections.sort(zoneList);
        Map<String, Object> timeZonesData = new HashMap<>();
        Unit unit = organizationGraphRepository.findOne(unitId);
        if(!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);

        }
        timeZonesData.put("selectedTimeZone", unit.getTimeZone() != null ? unit.getTimeZone().getId() : null);
        timeZonesData.put("allTimeZones", zoneList);
        return timeZonesData;
    }

    public boolean assignUnitTimeZone(Long unitId, String zoneIdString) {
        ZoneId zoneId = ZoneId.of(zoneIdString);
        if(!Optional.ofNullable(zoneId).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ZONEID_NOTFOUND, zoneIdString);

        }
        Unit unit = organizationGraphRepository.findOne(unitId);
        schedulerServiceRestClient.publishRequest(zoneId, unitId, true, IntegrationOperation.CREATE, "/scheduler_panel/time_zone", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
        unit.setTimeZone(zoneId);
        organizationGraphRepository.save(unit);
        return true;
    }

    public Unit fetchParentOrganization(Long unitId) {
        Unit parent = null;
        Unit unit = organizationGraphRepository.findOne(unitId, 0);
        if(!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else if(!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        } else {
            parent = unit;
        }
        return parent;
    }

    public OrganizationMappingDTO getEmploymentTypeWithExpertise(Long unitId) {
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        OrganizationMappingDTO organizationMappingDTO = new OrganizationMappingDTO();
        organizationMappingDTO.setEmploymentTypes(employmentTypeGraphRepository.getAllEmploymentTypeByOrganization(unitId, false));
        organizationMappingDTO.setExpertise(ObjectMapperUtils.copyPropertiesOfListByMapper(staffRetrievalService.getExpertisesOfUnitByCountryId(countryId, unitId), Expertise.class));
        return organizationMappingDTO;
    }

    public WTABasicDetailsDTO getWTARelatedInfo(Long countryId, Long organizationId, Long organizationSubTypeId, Long organizationTypeId, Long expertiseId, List<Long> unitIds) {
        WTABasicDetailsDTO wtaBasicDetailsDTO = new WTABasicDetailsDTO();
        if(Optional.ofNullable(expertiseId).isPresent()) {
            Expertise expertise = expertiseGraphRepository.findOne(expertiseId, 0);
            if(expertise != null) {
                ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();
                BeanUtils.copyProperties(expertise, expertiseResponseDTO);
                wtaBasicDetailsDTO.setExpertiseResponse(expertiseResponseDTO);
            }
        }
        if(Optional.ofNullable(organizationId).isPresent()) {
            Unit unit = organizationGraphRepository.findOne(organizationId, 0);
            if(unit != null) {
                OrganizationBasicDTO organizationBasicDTO = new OrganizationBasicDTO();
                BeanUtils.copyProperties(unit, organizationBasicDTO);
                wtaBasicDetailsDTO.setOrganization(organizationBasicDTO);
            }
        }
        if(Optional.ofNullable(countryId).isPresent()) {
            Country country = countryGraphRepository.findOne(countryId, 0);
            if(country != null) {
                CountryDTO countryDTO = new CountryDTO();
                BeanUtils.copyProperties(country, countryDTO);
                wtaBasicDetailsDTO.setCountryDTO(countryDTO);
            }
        }
        Long orgTypeId = organizationTypeGraphRepository.findOrganizationTypeIdBySubTypeId(organizationSubTypeId);
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(orgTypeId, 0);
        if(Optional.ofNullable(organizationType).isPresent()) {
            OrganizationTypeDTO organizationTypeDTO = new OrganizationTypeDTO();
            BeanUtils.copyProperties(organizationType, organizationTypeDTO);
            wtaBasicDetailsDTO.setOrganizationType(organizationTypeDTO);
        }
        List<Unit> units;
        units = CollectionUtils.isNotEmpty(unitIds) ? organizationGraphRepository.findOrganizationsByIdsIn(unitIds) : organizationTypeGraphRepository.getOrganizationsByOrganizationType(organizationSubTypeId);
        if(Optional.ofNullable(organizationSubTypeId).isPresent()) {
            OrganizationType organizationSubType = organizationTypeGraphRepository.findOne(organizationSubTypeId, 0);
            if(Optional.ofNullable(organizationSubType).isPresent()) {
                OrganizationTypeDTO organizationSubTypeDTO = new OrganizationTypeDTO();
                BeanUtils.copyProperties(organizationSubType, organizationSubTypeDTO);
                wtaBasicDetailsDTO.setOrganizationSubType(organizationSubTypeDTO);
            }
        }
        if(CollectionUtils.isNotEmpty(units)) {
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
        Unit unit = organizationGraphRepository.findOne(unitId, 0);
        if(!Optional.ofNullable(unit).isPresent()) {
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
        OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(unitId);
        List<Expertise> expertise = new ArrayList<>();
        if(isNotNull(servicesAndLevel)) {
            if(Optional.ofNullable(servicesAndLevel.getLevelId()).isPresent()) {
                expertise = expertiseGraphRepository.getExpertiseByCountryAndOrganizationServices(countryId, servicesAndLevel.getServicesId(), servicesAndLevel.getLevelId());
            } else {
                expertise = expertiseGraphRepository.getExpertiseByCountryAndOrganizationServices(countryId, servicesAndLevel.getServicesId());
            }
        } else {
            LOGGER.info("Organization Services or Level is not present for Unit id " + unitId);
        }
        List<StaffPersonalDetailDTO> staffList = staffGraphRepository.getAllStaffWithMobileNumber(unitId);
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
        Unit unit = organizationGraphRepository.findOne(unitId);
        Long countryId = unit.isParentOrganization() ? unit.getCountry().getId() : organizationGraphRepository.getCountryByParentOrganization(unit.getId()).getId();
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeRestClient.getAllPlannedTimeTypes(countryId);
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        List<DayTypeDTO> dayTypeDTOS = new ArrayList<>();
        List<TimeSlotDTO> timeSlotDTOS = timeSlotService.getShiftPlanningTimeSlotByUnit(unit);
        List<PresenceTypeDTO> presenceTypeDTOS1 = presenceTypeDTOS.stream().map(p -> new PresenceTypeDTO(p.getName(), p.getId())).collect(Collectors.toList());
        dayTypes.forEach(dayType -> {
            DayTypeDTO dayTypeDTO = new DayTypeDTO();
            try {
                PropertyUtils.copyProperties(dayTypeDTO, dayType);
                dayTypeDTOS.add(dayTypeDTO);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
        return new WTADefaultDataInfoDTO(dayTypeDTOS, presenceTypeDTOS1, timeSlotDTOS, countryId);
    }

    public RuleTemplateDefaultData getDefaultDataForRuleTemplateByUnit(Long unitId) {
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        List<Skill> skills = skillGraphRepository.findAllSkillsByCountryId(countryId);
        ActivityWithTimeTypeDTO activityWithTimeTypeDTOS = activityIntegrationService.getAllActivitiesAndTimeTypesByUnit(unitId, countryId);
        PriorityGroupDefaultData priorityGroupDefaultData1 = employmentTypeService.getExpertiseAndEmployment(countryId, false);
        RuleTemplateDefaultData ruleTemplateDefaultData = new RuleTemplateDefaultData(skills, activityWithTimeTypeDTOS.getTimeTypeDTOS(), activityWithTimeTypeDTOS.getActivityDTOS(), activityWithTimeTypeDTOS.getIntervals(), priorityGroupDefaultData1.getEmploymentTypes(), priorityGroupDefaultData1.getExpertises(), activityWithTimeTypeDTOS.getMinOpenShiftHours(), activityWithTimeTypeDTOS.getCounters());
        return ruleTemplateDefaultData;
    }

    public OrganizationSettingDTO updateOrganizationSettings(OrganizationSettingDTO organizationSettingDTO, Long unitId) {
        OrganizationSetting organizationSetting = organizationGraphRepository.getOrganisationSettingByOrgId(unitId);
        organizationSetting.setWalkingMeter(organizationSettingDTO.getWalkingMeter());
        organizationSetting.setWalkingMinutes(organizationSettingDTO.getWalkingMinutes());
        openingSettingsGraphRepository.save(organizationSetting);
        return organizationSettingDTO;
    }

    public OrganizationSettingDTO getOrganizationSettings(Long unitId) {
        OrganizationSetting organizationSetting = organizationGraphRepository.getOrganisationSettingByOrgId(unitId);
        return new OrganizationSettingDTO(organizationSetting.getWalkingMeter(), organizationSetting.getWalkingMinutes());
    }

    public List<UnitAndParentOrganizationAndCountryDTO> getParentOrganizationAndCountryIdsOfUnit() {
        List<Map<String, Object>> parentOrganizationAndCountryData = organizationGraphRepository.getUnitAndParentOrganizationAndCountryIds();
        return ObjectMapperUtils.copyPropertiesOfListByMapper(parentOrganizationAndCountryData, UnitAndParentOrganizationAndCountryDTO.class);
    }

    public CTABasicDetailsDTO getCTABasicDetailInfo(Long expertiseId, Long organizationSubTypeId, Long countryId, List<Long> unitIds) {
        CTABasicDetailsDTO ctaBasicDetailsDTO = new CTABasicDetailsDTO();
        if(Optional.ofNullable(expertiseId).isPresent()) {
            Expertise expertise = expertiseGraphRepository.findOne(expertiseId, 0);
            if(expertise != null) {
                ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();
                BeanUtils.copyProperties(expertise, expertiseResponseDTO);
                ctaBasicDetailsDTO.setExpertise(expertiseResponseDTO);
            }
        }
        if(Optional.ofNullable(countryId).isPresent()) {
            Country country = countryGraphRepository.findOne(countryId, 0);
            if(country != null) {
                CountryDTO countryDTO = new CountryDTO();
                BeanUtils.copyProperties(country, countryDTO);
                ctaBasicDetailsDTO.setCountryDTO(countryDTO);
            }
        }
        if(organizationSubTypeId != null) {
            OrganizationType organizationType = organizationTypeGraphRepository.findOrganizationTypeBySubTypeId(organizationSubTypeId);
            if(Optional.ofNullable(organizationType).isPresent()) {
                OrganizationTypeDTO organizationTypeDTO = new OrganizationTypeDTO();
                BeanUtils.copyProperties(organizationType, organizationTypeDTO);
                ctaBasicDetailsDTO.setOrganizationType(organizationTypeDTO);
            }
        }
        OrganizationType organizationSubType = organizationTypeGraphRepository.findOne(organizationSubTypeId, 0);
        List<Unit> units;
        if(CollectionUtils.isNotEmpty(unitIds)) {
            units = organizationGraphRepository.findOrganizationsByIdsIn(unitIds);
        } else {
            units = organizationTypeGraphRepository.getOrganizationsByOrganizationType(organizationSubTypeId);
        }
        if(Optional.ofNullable(organizationSubType).isPresent()) {
            OrganizationTypeDTO organizationSubTypeDTO = new OrganizationTypeDTO();
            BeanUtils.copyProperties(organizationSubType, organizationSubTypeDTO);
            ctaBasicDetailsDTO.setOrganizationSubType(organizationSubTypeDTO);
        }
        if(CollectionUtils.isNotEmpty(units)) {
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
        return organizationGraphRepository.getOrganizationIdsBySubOrgTypeId(orgTypeId);
    }

    public Map<Long, String> getTimeZoneStringsOfAllUnits() {
        List<OrganizationBasicResponse> organizationBasicResponses = organizationGraphRepository.findTimezoneforAllorganizations();
        return organizationBasicResponses.stream().collect(HashMap::new, (m, v) -> m.put(v.getId(), v.getTimezone()), HashMap::putAll);
    }

    public Map<Long, String> getTimeZoneStringsByUnitIds(Set<Long> unitIds) {
        List<OrganizationBasicResponse> organizationBasicResponses = organizationGraphRepository.findTimezoneByUnitIds(unitIds);
        return organizationBasicResponses.stream().collect(Collectors.toMap(OrganizationBasicResponse::getId, OrganizationBasicResponse::getTimezone));
    }

    public boolean mappingPayRollToUnit(long unitId, BigInteger payRollTypeId) {
        Unit unit = organizationGraphRepository.findOne(unitId);
        if(unit != null && !unit.isDeleted()) {
            unit.setPayRollTypeId(payRollTypeId);
            organizationGraphRepository.save(unit);
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "Organization", unitId);
        }
        return true;
    }

    public List<Long> getOrganizationIds(Long unitId) {
        List<Long> organizationIds = null;
        if(isNull(unitId)) {
            organizationIds = organizationGraphRepository.findAllOrganizationIds();
        } else {
            Optional<Unit> optionalOrganization = organizationGraphRepository.findById(unitId);
            if(optionalOrganization.isPresent()) {
                organizationIds = optionalOrganization.get().getChildren().stream().map(unit -> unit.getId()).collect(Collectors.toList());
            }
        }
        return organizationIds;
    }

    public SelfRosteringMetaData getPublicHolidaysReasonCodeAndDayTypeUnitId(long unitId) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        if(countryId == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        }
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unitId);
        List<ReasonCodeDTO> reasonCodes = ObjectMapperUtils.copyPropertiesOfListByMapper(reasonCodeGraphRepository.findReasonCodesByUnitIdAndReasonCodeType(unitId, ReasonCodeType.TIME_TYPE), ReasonCodeDTO.class);
        return new SelfRosteringMetaData(ObjectMapperUtils.copyPropertiesOfListByMapper(dayTypeService.getAllDayTypeByCountryId(countryId), com.kairos.dto.user.country.day_type.DayType.class), new ReasonCodeWrapper(reasonCodes, userAccessRoleDTO), FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId)));
    }
}
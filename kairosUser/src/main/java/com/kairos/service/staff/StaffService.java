package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.response.dto.shift.StaffUnitPositionDetails;
import com.kairos.activity.util.ObjectMapperUtils;

import com.kairos.activity.util.DateUtils;
import com.kairos.client.TaskServiceRestClient;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.enums.StaffStatusEnum;
import com.kairos.persistence.model.enums.TimeSlotType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.UnitManagerDTO;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.model.organization.organizationServicesAndLevelQueryResult;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.access_permission.AccessGroupRole;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.client.Client;
import com.kairos.persistence.model.user.client.ContactAddress;
import com.kairos.persistence.model.user.client.ContactDetail;
import com.kairos.persistence.model.user.country.DayType;
import com.kairos.persistence.model.user.country.EngineerType;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.filter.FavoriteFilterQueryResult;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.staff.*;
import com.kairos.persistence.model.user.unit_position.UnitPositionQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
import com.kairos.response.dto.web.access_group.UserAccessRoleDTO;
import com.kairos.response.dto.web.client.ClientStaffInfoDTO;
import com.kairos.response.dto.web.PasswordUpdateDTO;
import com.kairos.response.dto.web.StaffAssignedTasksWrapper;
import com.kairos.response.dto.web.StaffTaskDTO;
import com.kairos.response.dto.web.cta.DayTypeDTO;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;


import com.kairos.response.dto.web.skill.SkillDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.integration.IntegrationService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.mail.MailService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TeamService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.util.CPRUtil;
import com.kairos.util.DateConverter;
import com.kairos.util.DateUtil;
import com.kairos.util.FileUtil;
import com.kairos.util.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.time.DayOfWeek;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.util.FileUtil.createDirectory;

/**
 * Created by prabjot on 24/10/16.
 */
@Transactional
@Service
public class StaffService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private LanguageGraphRepository languageGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private Scheduler scheduler;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    private TeamService teamService;
    @Inject
    private PartialLeaveGraphRepository partialLeaveGraphRepository;
    @Inject
    IntegrationService integrationService;
    @Inject
    private MailService mailService;
    @Inject
    private EmploymentService employmentService;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private SkillService skillService;
    @Inject
    private StaffAddressService staffAddressService;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    StaffFilterService staffFilterService;
    @Autowired
    UnitEmpAccessGraphRepository unitEmpAccessGraphRepository;
    @Autowired
    ClientGraphRepository clientGraphRepository;
    @Autowired
    TaskServiceRestClient taskServiceRestClient;
    @Inject
    private OrganizationService organizationService;
    @Autowired
    private UnitPositionGraphRepository unitPositionGraphRepository;
    @Inject
    private UnitPositionService unitPositionService;
    @Inject
    StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private PlannerSyncService plannerSyncService;
    @Inject private TimeSlotGraphRepository timeSlotGraphRepository;
    @Inject
    private ExceptionService exceptionService;

  public String uploadPhoto(Long staffId, MultipartFile multipartFile) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        createDirectory(IMAGES_PATH);
        String fileName = DateUtil.getCurrentDate().getTime() + multipartFile.getOriginalFilename();
        final String path = IMAGES_PATH + File.separator + fileName;
        FileUtil.writeFile(path, multipartFile);
        staff.setProfilePic(fileName);
        save(staff);
        return envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + fileName;

    }

    public boolean removePhoto(Long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return false;
        }
        staff.setProfilePic(null);
        save(staff);
        return true;
    }


    public boolean updatePassword(long staffId, PasswordUpdateDTO passwordUpdateDTO) {

        User user = userGraphRepository.getUserByStaffId(staffId);
        if (!Optional.ofNullable(user).isPresent()) {
            logger.error("User not found belongs to this staff id " + staffId);
            exceptionService.dataNotFoundByIdException("message.staff.user.id.notfound",staffId);

        }
        CharSequence oldPassword = CharBuffer.wrap(passwordUpdateDTO.getOldPassword());
        if (new BCryptPasswordEncoder().matches(oldPassword, user.getPassword())) {
            CharSequence newPassword = CharBuffer.wrap(passwordUpdateDTO.getNewPassword());
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            save(user);
        } else {
            logger.error("Password not matched ");
            exceptionService.dataNotMatchedException("message.staff.user.password.notmatch");

        }
        return true;

    }

    public StaffPersonalDetail savePersonalDetail(long staffId, StaffPersonalDetail staffPersonalDetail, long unitId) throws ParseException {
        Staff staffToUpdate = staffGraphRepository.findOne(staffId);

        if (staffToUpdate == null) {
            exceptionService.dataNotFoundByIdException("message.staff.notfound");

        }
        if(StaffStatusEnum.ACTIVE.equals(staffToUpdate.getCurrentStatus())&&StaffStatusEnum.FICTIVE.equals(staffPersonalDetail.getCurrentStatus()))
        {
            exceptionService.actionNotPermittedException("message.employ.notconvert.Fictive");

        }
        staffExpertiseRelationShipGraphRepository.unlinkExpertiseFromStaffExcludingCurrent(staffId, staffPersonalDetail.getExpertiseWithExperience().stream().map(StaffExperienceInExpertiseDTO::getExpertiseId).collect(Collectors.toList()));
        for (int i = 0; i < staffPersonalDetail.getExpertiseWithExperience().size(); i++) {
            Expertise expertise = expertiseGraphRepository.findOne(staffPersonalDetail.getExpertiseWithExperience().get(i).getExpertiseId());
            StaffExperienceInExpertiseDTO staffExperienceInExpertiseDTO = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndExpertiseId(staffId, staffPersonalDetail.getExpertiseWithExperience().get(i).getExpertiseId());
            Long id = null;
            if (Optional.ofNullable(staffExperienceInExpertiseDTO).isPresent())
                 id = staffExperienceInExpertiseDTO.getId();

            Date expertiseStartDate = staffPersonalDetail.getExpertiseWithExperience().get(i).getExpertiseStartDate();
            StaffExpertiseRelationShip staffExpertiseRelationShip = new StaffExpertiseRelationShip(id, staffToUpdate, expertise, staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths(), expertiseStartDate);
            staffExpertiseRelationShipGraphRepository.save(staffExpertiseRelationShip);
            boolean isSeniorityLevelMatched=false;
            for(SeniorityLevel seniorityLevel:expertise.getSeniorityLevel()){
                if(staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths()>=seniorityLevel.getFrom()*12 &&
                        (seniorityLevel.getTo()==null || staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths()<=seniorityLevel.getTo()*12)){
                    isSeniorityLevelMatched=true;
                    break;
                }
            }
            if(!isSeniorityLevelMatched){
                exceptionService.actionNotPermittedException("error.noSeniorityLevelFound","seniorityLevel "+staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths());
            }

            staffPersonalDetail.getExpertiseWithExperience().get(i).setId(staffExpertiseRelationShip.getId());
            staffPersonalDetail.getExpertiseWithExperience().get(i).setNextSeniorityLevelInMonths(nextSeniorityLevelInMonths(expertise.getSeniorityLevel(),
                    staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths()));
        }
        Language language = languageGraphRepository.findOne(staffPersonalDetail.getLanguageId());
        List<Expertise> expertise = expertiseGraphRepository.getExpertiseByIdsIn(staffPersonalDetail.getExpertiseIds());
        List<Expertise> oldExpertise = staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staffToUpdate.getId());
        staffToUpdate.setLanguage(language);
        staffToUpdate.setFirstName(staffPersonalDetail.getFirstName());
        staffToUpdate.setLastName(staffPersonalDetail.getLastName());
        staffToUpdate.setFamilyName(staffPersonalDetail.getFamilyName());
        staffToUpdate.setCurrentStatus(staffPersonalDetail.getCurrentStatus());
//        staffToUpdate.setCprNumber(staffPersonalDetail.getCprNumber());
        staffToUpdate.setSpeedPercent(staffPersonalDetail.getSpeedPercent());
        staffToUpdate.setWorkPercent(staffPersonalDetail.getWorkPercent());
        staffToUpdate.setOvertime(staffPersonalDetail.getOvertime());
        staffToUpdate.setCostDay(staffPersonalDetail.getCostDay());
        staffToUpdate.setCostCall(staffPersonalDetail.getCostCall());
        staffToUpdate.setCostKm(staffPersonalDetail.getCostKm());
        staffToUpdate.setCostHour(staffPersonalDetail.getCostHour());
        staffToUpdate.setCostHourOvertime(staffPersonalDetail.getCostHourOvertime());
        staffToUpdate.setCapacity(staffPersonalDetail.getCapacity());
        staffToUpdate.setCareOfName(staffPersonalDetail.getCareOfName());
        staffPersonalDetail.setExpertiseIds(staffPersonalDetail.getExpertiseWithExperience().stream().map(StaffExperienceInExpertiseDTO::getExpertiseId).collect(Collectors.toList()));

        if (staffPersonalDetail.getCurrentStatus() == StaffStatusEnum.INACTIVE) {
            staffToUpdate.setInactiveFrom(DateConverter.parseDate(staffPersonalDetail.getInactiveFrom()).getTime());
        }
        staffToUpdate.setSignature(staffPersonalDetail.getSignature());
        staffToUpdate.setContactDetail(staffPersonalDetail.getContactDetail());
        save(staffToUpdate);

        if (oldExpertise != null) {
            List<Long> expertiseIds = oldExpertise.stream().map(Expertise::getId).collect(Collectors.toList());
            staffGraphRepository.removeSkillsByExpertise(staffToUpdate.getId(), expertiseIds);
        }
        List<Long> expertiseIds = expertise.stream().map(Expertise::getId).collect(Collectors.toList());
        staffGraphRepository.updateSkillsByExpertise(staffToUpdate.getId(), expertiseIds, DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime(), Skill.SkillLevel.ADVANCE);


        // Set if user is female and pregnant
        User user = userGraphRepository.getUserByStaffId(staffId);
        if( !user.getCprNumber().equals(staffPersonalDetail.getCprNumber()) ){
            user.setCprNumber(staffPersonalDetail.getCprNumber());
            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(staffPersonalDetail.getCprNumber()));
        }
        staffToUpdate.setCprNumber(staffPersonalDetail.getCprNumber());
        user.setGender(staffPersonalDetail.getGender());
        user.setPregnant( user.getGender().equals(Gender.FEMALE) ? staffPersonalDetail.isPregnant() : false);
        save(user);
        staffPersonalDetail.setPregnant(user.isPregnant());
        return staffPersonalDetail;
    }

    public Map<String, Object> getPersonalInfo(long staffId, long unitId, String type) {

        StaffEmploymentDTO staffEmploymentDTO = null;
        Staff staff = null;

        if (TEAM.equalsIgnoreCase(type)) {
            staff = staffGraphRepository.getTeamStaff(unitId, staffId);
            staffEmploymentDTO = new StaffEmploymentDTO(staff, null);
        } else if (ORGANIZATION.equalsIgnoreCase(type)) {
            Organization unit = organizationGraphRepository.findOne(unitId);
            Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
            // unit is parent so fetching all staff from itself
            //staffPersonalDetailDTOS = staffGraphRepository.getAllStaffByUnitId(parentOrganization.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
            staffEmploymentDTO = staffGraphRepository.getStaffAndEmploymentByUnitId(parentOrganization.getId(), staffId);
            staff = Optional.ofNullable(staffEmploymentDTO).isPresent() ? staffEmploymentDTO.getStaff() : null;
        }

        if (staff == null) {
            exceptionService.dataNotFoundByIdException("message.staff.idandunitid.notfound",staffId,type,unitId);

        }

        Map<String, Object> personalInfo = new HashMap<>(2);
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);
        List<Expertise> expertise = new ArrayList<>();
        List<Language> languages;
        List<EngineerType> engineerTypes;
        if (countryId != null) {
            engineerTypes = engineerTypeGraphRepository.findEngineerTypeByCountry(countryId);
            languages = languageGraphRepository.getLanguageByCountryId(countryId);
        } else {
            languages = Collections.emptyList();
            engineerTypes = Collections.emptyList();
        }
        organizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(unitId);
        expertise = expertiseGraphRepository.getExpertiseByCountryAndOrganizationServices(countryId, servicesAndLevel.getServicesId(), servicesAndLevel.getLevelId(), DateUtil.getCurrentDateMillis());
        personalInfo.put("employmentInfo", employmentService.retrieveEmploymentDetails(staffEmploymentDTO));
        personalInfo.put("personalInfo", retrievePersonalInfo(staff));
        personalInfo.put("expertise", expertise);
        personalInfo.put("languages", languages);
        personalInfo.put("engineerTypes", engineerTypes);
        return personalInfo;

    }


    public Map<String, Object> retrievePersonalInfo(Staff staff) {
        User user = userGraphRepository.getUserByStaffId(staff.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", staff.getFirstName());
        map.put("lastName", staff.getLastName());
        map.put("profilePic", envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + staff.getProfilePic());
        map.put("familyName", staff.getFamilyName());
        map.put("currentStatus", staff.getCurrentStatus());
        map.put("signature", staff.getSignature());
        Date inactiveFrom = Optional.ofNullable(staff.getInactiveFrom()).isPresent() ? DateConverter.getDate(staff.getInactiveFrom()) : null;
        map.put("inactiveFrom", inactiveFrom);
        map.put("languageId", staffGraphRepository.getLanguageId(staff.getId()));
        map.put("contactDetail", staffGraphRepository.getContactDetail(staff.getId()));
        map.put("cprNumber", user.getCprNumber());
        map.put("careOfName", staff.getCareOfName());
        map.put("gender", user.getGender());
        map.put("pregnant", user.isPregnant());


        List<StaffExpertiseQueryResult> staffExpertiseQueryResults=staffExpertiseRelationShipGraphRepository.getExpertiseWithExperience(staff.getId());
        staffExpertiseQueryResults.forEach(expertiseQueryResult ->{
            expertiseQueryResult.setRelevantExperienceInMonths((int) ChronoUnit.MONTHS.between(DateUtil.asLocalDate(expertiseQueryResult.getExpertiseStartDate()), LocalDate.now()));
            expertiseQueryResult.setNextSeniorityLevelInMonths(nextSeniorityLevelInMonths(expertiseQueryResult.getSeniorityLevels(),expertiseQueryResult.getRelevantExperienceInMonths()));
        });

        map.put("expertiseIds", staffExpertiseQueryResults.stream().map(StaffExpertiseQueryResult::getExpertiseId).collect(Collectors.toList()));
        map.put("expertiseWithExperience", staffExpertiseQueryResults);

        // Visitour Speed Profile
        map.put("speedPercent", staff.getSpeedPercent());
        map.put("workPercent", staff.getWorkPercent());
        map.put("overtime", staff.getOvertime());
        map.put("costDay", staff.getCostDay());
        map.put("costCall", staff.getCostCall());
        map.put("costKm", staff.getCostKm());
        map.put("costHour", staff.getCostHour());
        map.put("costHourOvertime", staff.getCostHourOvertime());
        map.put("capacity", staff.getCapacity());
        return map;
    }

    public Map<String, Object> saveNotes(long staffId, String generalNote, String requestFromPerson) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff != null) {
            logger.info("General note: " + generalNote + "\nPerson: " + requestFromPerson);
            staff.saveNotes(generalNote, requestFromPerson);
            save(staff);
            return staff.retrieveNotes();
        }
        return null;
    }

    public Map<String, Object> getNotes(long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        return staff.retrieveNotes();
    }


    public Map<String, Object> getStaffWithFilter(Long unitId, String type, long id, Boolean allStaffRequired, StaffFilterDTO staffFilterDTO) {

        List<StaffPersonalDetailDTO> staff = null;
        Long countryId = null;
        List<AccessGroup> roles = null;
        List<EngineerType> engineerTypes = null;
        Map<String, Object> map = new HashMap();
        if (ORGANIZATION.equalsIgnoreCase(type)) {
//            staff = getStaffWithBasicInfo(id, allStaffRequired);
            map.put("staffList", staffFilterService.getAllStaffByUnitId(unitId, allStaffRequired, staffFilterDTO).getStaffList());
            roles = accessGroupService.getAccessGroups(id);
            countryId = countryGraphRepository.getCountryIdByUnitId(id);
            engineerTypes = engineerTypeGraphRepository.findEngineerTypeByCountry(countryId);
        } else if (GROUP.equalsIgnoreCase(type)) {
            staff = staffGraphRepository.getStaffByGroupId(id, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
            Organization organization = organizationGraphRepository.getOrganizationByGroupId(id).getOrganization();
            countryId = countryGraphRepository.getCountryIdByUnitId(organization.getId());
            roles = accessGroupService.getAccessGroups(organization.getId());
        } else if (TEAM.equalsIgnoreCase(type)) {
            staff = staffGraphRepository.getStaffByTeamId(id, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
            Organization organization = organizationGraphRepository.getOrganizationByTeamId(id);
            roles = accessGroupService.getAccessGroups(organization.getId());
            countryId = countryGraphRepository.getCountryIdByUnitId(organization.getId());
        }


        if (Optional.ofNullable(staff).isPresent()) {
            map.put("staffList", staff);
        }

        map.put("engineerTypes", engineerTypes);
        map.put("engineerList", engineerTypeGraphRepository.findEngineerTypeByCountry(countryId));
        map.put("roles", roles);
        return map;
    }


    public Map<String, Object> getStaff(String type, long id, Boolean allStaffRequired) {

        List<StaffPersonalDetailDTO> staff = null;
        Long countryId = null;
        List<AccessGroup> roles = null;
        List<EngineerType> engineerTypes = null;
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            staff = getStaffWithBasicInfo(id, allStaffRequired);
            roles = accessGroupService.getAccessGroups(id);
            countryId = countryGraphRepository.getCountryIdByUnitId(id);
            engineerTypes = engineerTypeGraphRepository.findEngineerTypeByCountry(countryId);
        } else if (GROUP.equalsIgnoreCase(type)) {
            staff = staffGraphRepository.getStaffByGroupId(id, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
            Organization organization = organizationGraphRepository.getOrganizationByGroupId(id).getOrganization();
            countryId = countryGraphRepository.getCountryIdByUnitId(organization.getId());
            roles = accessGroupService.getAccessGroups(organization.getId());
        } else if (TEAM.equalsIgnoreCase(type)) {
            staff = staffGraphRepository.getStaffByTeamId(id, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
            Organization organization = organizationGraphRepository.getOrganizationByTeamId(id);
            roles = accessGroupService.getAccessGroups(organization.getId());
            countryId = countryGraphRepository.getCountryIdByUnitId(organization.getId());
        }

        Map<String, Object> map = new HashMap();
        map.put("staffList", staff);
        map.put("engineerTypes", engineerTypes);
        map.put("engineerList", engineerTypeGraphRepository.findEngineerTypeByCountry(countryId));
        map.put("roles", roles);
        return map;
    }

    /* @Modified by VIPUL
    * */

    // TODO NEED TO FIX map
    public List<Map<String, Object>> getStaffWithBasicInfo(long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",unitId);

        }
        List<Map<String, Object>> staffPersonalDetailDTOS = new ArrayList<>();
        staffPersonalDetailDTOS = staffGraphRepository.getAllStaffHavingUnitPositionByUnitIdMap(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        return staffPersonalDetailDTOS;
    }

    public List<StaffPersonalDetailDTO> getStaffWithBasicInfo(long unitId, Boolean allStaffRequired) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",unitId);

        }
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS = new ArrayList<>();
        if (allStaffRequired) {
            Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffByUnitId(parentOrganization.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        } else {
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffHavingUnitPositionByUnitId(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        }
        return staffPersonalDetailDTOS;
//        Organization parent = null;
//        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
//            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
//
//        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
//            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
//        }
//
//        if (parent == null) {
//            return staffGraphRepository.getStaffWithBasicInfo(unit.getId(), unit.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
//        } else {
//            return staffGraphRepository.getStaffInfoForFilters(parent.getId(), unit.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
//        }
    }

    public List<StaffAdditionalInfoQueryResult> getStaffWithAdditionalInfo(long unitId) {

        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException("message.unit.notfound",unitId);

        }

        return staffGraphRepository.getStaffAndCitizenDetailsOfUnit(unitId);
        //TODO unnecessary queries should be removed
        /*Organization parent = null;
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        if (parent == null) {
            return staffGraphRepository.getStaffWithAdditionalInfo(unit.getId(), unit.getId());
        } else {
            return staffGraphRepository.getStaffWithAdditionalInfo(parent.getId(), unit.getId());
        }*/
    }

    public Staff assignExpertiseToStaff(long staffId, List<Long> expertiseIds) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        List<StaffExpertiseRelationShip> staffExpertiseRelationShips = new ArrayList<>();
        List<Expertise> expertise = expertiseGraphRepository.getExpertiseByIdsIn(expertiseIds);
        for (Expertise currentExpertise : expertise) {
            StaffExpertiseRelationShip staffExpertiseRelationShip = new StaffExpertiseRelationShip(staff, currentExpertise, 0, DateUtil.getCurrentDate());
            staffExpertiseRelationShips.add(staffExpertiseRelationShip);
        }
//        if (expertise != null) {
//            staff.setParentExpertise(expertise);
//            staffGraphRepository.save(staff);
//        }
        staffExpertiseRelationShipGraphRepository.saveAll(staffExpertiseRelationShips);
        return staff;
    }

    public Map<String, Object> getExpertiseOfStaff(long countryId, long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("allExpertise", expertiseGraphRepository.getAllExpertiseByCountry(countryId,DateUtil.getCurrentDateMillis()));
        map.put("myExpertise", staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staffId).stream().map(Expertise::getId).collect(Collectors.toList()));
        return map;
    }

    public List<Staff> getPlannerInOrganization(Long organizationId) {
        return staffGraphRepository.findAllPlanners(organizationId);

    }

    public List<Staff> getManagersInOrganization(Long organizationId) {
        return staffGraphRepository.findAllManager(organizationId);

    }

    public List<Staff> getVisitatorsInOrganization(Long organizationId) {
        return staffGraphRepository.findAllVisitator(organizationId);

    }

    public List<Staff> getTeamLeadersInOrganization(Long organizationId) {
        return staffGraphRepository.findAllTeamLeader(organizationId);

    }


    public StaffUploadBySheetQueryResult batchAddStaffToDatabase(long unitId, MultipartFile multipartFile, Long accessGroupId) {

        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGroup).isPresent()) {
            logger.error("Access group not found");
            exceptionService.invalidRequestException("error.staff.accessgroup.notfound",accessGroupId);

        }
        List<Staff> staffList = new ArrayList<>();
        List<Integer> staffErrorList = new ArrayList<>();
        StaffUploadBySheetQueryResult staffUploadBySheetQueryResult = new StaffUploadBySheetQueryResult();
        staffUploadBySheetQueryResult.setStaffErrorList(staffErrorList);
        staffUploadBySheetQueryResult.setStaffList(staffList);

        Organization unit = organizationGraphRepository.findOne(unitId);
        if (unit == null) {
            logger.info("Organization is null");
            return null;
        }
        Organization parent = null;
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        try (InputStream stream = multipartFile.getInputStream()) {
            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                exceptionService.internalServerError("error.xssfsheet.noMoreRow",0);

            }
            Row header = sheet.getRow(0);

            Set<Long> externalIdsOfStaffToBeSaved = new HashSet<>();
            boolean headerSkipped = false;
            for (Row row : sheet) { // For each Row.
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                Cell cell = row.getCell(2); // Get the Cell at the Index / Column you want.
                if (cell != null) {
//                    externalIdsOfStaffToBeSaved.add(new Double(cell.getNumericCellValue()).longValue());
                    externalIdsOfStaffToBeSaved.add(new Double(cell.toString()).longValue());
                }
            }
            List<Long> alreadyAddedStaffIds = staffGraphRepository.findStaffByExternalIdIn(externalIdsOfStaffToBeSaved);
            logger.info(externalIdsOfStaffToBeSaved.toString());

            int NumberOfColumnsInSheet = header.getLastCellNum();
            int cprHeader = -1;

            for (int i = 0; i < NumberOfColumnsInSheet; i++) {
                String columnHeader = header.getCell(i).getStringCellValue();
                if (columnHeader.equalsIgnoreCase(CPR_NUMBER)) {
                    cprHeader = i;
                    break;
                }
            }
            if (cprHeader == -1) {
                logger.info("Sheet has no header containing cprNumber. Please add a cpr number header as cprnumber");
                exceptionService.internalServerError("error.sheet.add.crpnumber");

            }

            logger.info("Sheet has rows");
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (String.valueOf(row.getCell(0)) == null || String.valueOf(row.getCell(0)).isEmpty()) {
                    break;
                }
                if (row.getCell(0) == null) {
                    logger.info("No more rows");
                    if (staffList.size() != 0) {
                        break;
                    }
                }
                // Skip headers
                if (row.getRowNum() == 0) {
                    continue;
                }
                Cell cprHeaderValue = row.getCell(cprHeader);
                if (cprHeaderValue == null || cprHeaderValue.getCellType() == Cell.CELL_TYPE_BLANK) {
                    logger.info(" This row has no CRP Number so skipping this %s", row.getRowNum());
                    staffErrorList.add(row.getRowNum());

                } else {

                    Cell cell = row.getCell(8);
                    cell.setCellType(Cell.CELL_TYPE_STRING);

                    cell = row.getCell(2);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    Long externalId = (StringUtils.isBlank(cell.getStringCellValue())) ? 0 : Long.parseLong(cell.getStringCellValue());
                    if (alreadyAddedStaffIds.contains(externalId)) {
                        logger.info(" staff with kmd external id  already found  so we are skipping this " + externalId);
                        staffErrorList.add(row.getRowNum());
                        continue;
                    }
//                    StaffQueryResult staffQueryResult = (Optional.ofNullable(parent).isPresent()) ? staffGraphRepository.getStaffByExternalIdInOrganization(parent.getId(), externalId)
//                            : staffGraphRepository.getStaffByExternalIdInOrganization(unitId, externalId);

//                    if (Optional.ofNullable(staffQueryResult).isPresent()) {
//                        staff = staffQueryResult.getStaff();
//                    } else {
//                        logger.info("Creating new staff with kmd external id " + externalId + " in unit " + unit.getId());

//                    }
                    Staff staff = new Staff();
                    boolean isEmploymentExist = (staff.getId()) != null;
                    staff.setExternalId(externalId);
                    if (row.getCell(17) != null) {
                        staff.setBadgeNumber(row.getCell(17).toString());
                    }
                    staff.setFirstName(row.getCell(20).toString());
                    staff.setLastName(row.getCell(21).toString());
                    staff.setFamilyName(row.getCell(21).toString());
                    staff.setUserName(row.getCell(19).toString());
                    ContactAddress contactAddress = extractContactAddressFromRow(row);
                    if (!Optional.ofNullable(contactAddress).isPresent()) {
                        contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(unit);
                    }
                    ContactDetail contactDetail = extractContactDetailFromRow(row);
//                    if (Optional.ofNullable(staffQueryResult).isPresent()) {
//
//                        if (Optional.ofNullable(contactDetail).isPresent()) {
//                            contactDetail.setId(staffQueryResult.getContactDetailId());
//                        }
//
//                        if (Optional.ofNullable(contactAddress).isPresent()) {
//                            contactAddress.setId(staffQueryResult.getContactAddressId());
//                        }
//                    }
                    staff.setContactDetail(contactDetail);
                    staff.setContactAddress(contactAddress);

                    cell = row.getCell(2);
                    if (Optional.ofNullable(cell).isPresent()) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        User user = null;
                        user = userGraphRepository.findByTimeCareExternalId(cell.getStringCellValue());
                        if (!Optional.ofNullable(user).isPresent()) {
                            user = new User();
                            user.setFirstName(row.getCell(20).toString());
                            user.setLastName(row.getCell(21).toString());
                            Long cprNumberLong = new Double(row.getCell(41).toString()).longValue();
                            user.setCprNumber(cprNumberLong.toString());
                            user.setGender(CPRUtil.getGenderFromCPRNumber(user.getCprNumber()));
                            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(user.getCprNumber()));
                            user.setTimeCareExternalId(cell.getStringCellValue());
                            if (Optional.ofNullable(contactDetail).isPresent() && Optional.ofNullable(contactDetail.getPrivateEmail()).isPresent()) {
                                user.setUserName(contactDetail.getPrivateEmail().toLowerCase());
                                user.setEmail(contactDetail.getPrivateEmail().toLowerCase());
                            }
                            String defaultPassword = user.getFirstName().trim() + "@kairos";
                            user.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
                        }
                        Client client = createClientObject(staff, user.getCprNumber());  // here client is referred as citizen

                        staff.setClient(client);
                        staff.setUser(user);
                    }
                    staffGraphRepository.save(staff);
                    staffList.add(staff);
                    if (!staffGraphRepository.staffAlreadyInUnit(externalId, unit.getId())) {
                        createEmployment(parent, unit, staff, accessGroupId, null, isEmploymentExist);
                    }

                }
            }
            return staffUploadBySheetQueryResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staffUploadBySheetQueryResult;
    }


    private ContactAddress extractContactAddressFromRow(Row row) {

        Cell cell = row.getCell(24);
        if (Optional.ofNullable(cell).isPresent()) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            ZipCode zipCode = zipCodeGraphRepository.findByZipCode((StringUtils.isBlank(cell.getStringCellValue())) ? 0 : Integer.parseInt(cell.getStringCellValue()));
            if (zipCode != null) {
                ContactAddress contactAddress = new ContactAddress();
                contactAddress.setZipCode(zipCode);
                String address = row.getCell(23).getStringCellValue();
                String arr[] = address.split(",");
                String houseNumber = "";
                String fullStreetName = "";
                if (arr.length != 0) {
                    String street = arr[0];
                    String newArray[] = street.split(" ");
                    houseNumber = newArray[newArray.length - 1];
                    for (int i = 0; i < newArray.length - 1; i++) {
                        if (i == 0) {
                            fullStreetName = fullStreetName + newArray[i];
                        } else {
                            fullStreetName = fullStreetName + " " + newArray[i];
                        }
                    }
                    contactAddress.setHouseNumber(houseNumber);
                    contactAddress.setStreet1(fullStreetName);
                    contactAddress.setCity(row.getCell(25).toString());
                }
                return contactAddress;
            }
        }
        return null;
    }

    private ContactDetail extractContactDetailFromRow(Row row) {
        Cell cell = row.getCell(26);
        ContactDetail contactDetail = null;
        if (cell != null) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            String telephoneNumber = cell.getStringCellValue();
            if (!StringUtils.isBlank(telephoneNumber)) {
                contactDetail = new ContactDetail();
                contactDetail.setPrivatePhone(telephoneNumber.trim());
            }
        }
        cell = row.getCell(27);
        if (cell != null) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            String cellPhoneNumber = cell.getStringCellValue();
            if (!StringUtils.isBlank(cellPhoneNumber)) {
                if (!Optional.ofNullable(contactDetail).isPresent()) {
                    contactDetail = new ContactDetail();
                }
                contactDetail.setMobilePhone(cellPhoneNumber.trim());
            }
        }
        cell = row.getCell(28);
        if (cell != null) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            String email = cell.getStringCellValue();
            if (!StringUtils.isBlank(email)) {
                if (!Optional.ofNullable(contactDetail).isPresent()) {
                    contactDetail = new ContactDetail();
                }
                contactDetail.setPrivateEmail(email.toLowerCase());
            }
        }
        return contactDetail;
    }

    public Staff createStaff(Staff staff) {

        if (checkStaffEmailConstraint(staff)) {

            logger.info("Creating Staff.......... " + staff.getFirstName() + " " + staff.getLastName());
            logger.info("Creating User for Staff");
            User user = new User();
            user.setEmail(staff.getEmail());
            staff.setUser(userGraphRepository.save(user));
            save(staff);
            return staff;
        }
        logger.info("Not Creating Staff.......... " + staff.getFirstName() + " " + staff.getLastName());
        return null;
    }

    private boolean checkStaffEmailConstraint(Staff staff) {
        logger.info("Checking Email constraint");
        if (staff.getEmail() != null && userGraphRepository.findByEmail(staff.getEmail()) != null) {

            logger.info("Email matched !");
            return false;
        }
        return true;
    }

    public Map<String, Object> deleteNote(long staffId) {
        Staff currentStaff = staffGraphRepository.findOne(staffId);
        currentStaff.saveNotes("", "");
        staffGraphRepository.save(currentStaff);
        return currentStaff.retrieveNotes();

    }

    public List<Staff> getAllStaff() {
        return staffGraphRepository.findAll();
    }

    public Staff getByExternalId(Long externalId) {
        return staffGraphRepository.findByExternalId(externalId);
    }

    public boolean deleteStaffById(Long staffId, Long employmentId) {
        staffGraphRepository.deleteStaffEmployment(staffId, employmentId);
        staffGraphRepository.deleteStaffById(staffId);
        return staffGraphRepository.findOne(staffId) == null;

    }


    public User createCountryAdmin(User admin) {
        User user = userGraphRepository.findByEmail(admin.getEmail());
        if (user != null) {
            return null;
        }
        admin.setPassword(new BCryptPasswordEncoder().encode(admin.getPassword()));
        userGraphRepository.save(admin);
        Staff adminAsStaff = new Staff();
        adminAsStaff.setGeneralNote("Will manage the platform");
        adminAsStaff.setUser(admin);
        adminAsStaff.setFirstName(admin.getFirstName());
        adminAsStaff.setLastName(admin.getLastName());
        adminAsStaff.setCurrentStatus(StaffStatusEnum.ACTIVE);
        adminAsStaff.setEmail(admin.getEmail());
        adminAsStaff.setUserName(admin.getEmail());
        staffGraphRepository.save(adminAsStaff);

        List<Organization> organizations = organizationGraphRepository.findByOrganizationLevel(OrganizationLevel.COUNTRY);
        Organization organization = null;
        if (!organizations.isEmpty()) {
            organization = organizations.get(0);
        }
        if (organization != null) {
            Employment employment = new Employment("working as country admin", adminAsStaff);
            organization.getEmployments().add(employment);
            organizationGraphRepository.save(organization);

            AccessGroup accessGroup = accessGroupRepository.findAccessGroupByName(organization.getId(), AppConstants.COUNTRY_ADMIN);
            UnitPermission unitPermission = new UnitPermission();
            unitPermission.setOrganization(organization);
            AccessPermission accessPermission = new AccessPermission(accessGroup);
            UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitPermission, accessPermission);
            unitEmpAccessRelationship.setEnabled(true);
            unitEmpAccessGraphRepository.save(unitEmpAccessRelationship);
            accessPageService.setPagePermissionToAdmin(accessPermission);
            employment.getUnitPermissions().add(unitPermission);
            organization.getEmployments().add(employment);
            organizationGraphRepository.save(organization);
        } else {
            return null;
        }
        return admin;
    }


    public Staff createStaffFromPlanningWorkflow(StaffDTO data, long unitId) {
        if (data == null) {
            return null;
        }
        Staff staff = new Staff();
        staff.setFirstName(data.getFirstName());
        staff.setLastName(data.getLastName());
        staff.setCprNumber(String.valueOf(data.getCprNumber()));
        staff.setFamilyName(data.getFamilyName());
        //staff.setEmployedSince(data.getEmployedSince().getTime());
        staff.setCurrentStatus(data.getCurrentStatus());
        staff = createStaff(staff);
        if (staff != null) {
            if (data.getTeamId() != null) {
                //TODO hardcoded unit id to removes
                boolean result = teamService.addStaffInTeam(staff.getId(), data.getTeamId(), false, unitId);
                logger.info("Assigning team to staff: " + result);
            }
            if (data.getSkills() != null) {
                List<Map<String, Object>> result = skillService.assignSkillToStaff(staff.getId(), data.getSkills(), false, unitId);
                logger.info("Assigned Number of Skills to staff: " + result.size());
            }
            taskServiceRestClient.updateTaskForStaff(staff.getId(), data.getAnonymousStaffId());
            return staff;
        }
        return null;
    }

    /*
    By Yasir
    Commented below method as we are no longer using FLS Visitour
     */
    public Map<String, String> createStaffSchedule(long organizationId, Long unitId) throws ParseException {

        /*Map<String, String> workScheduleStatus = new HashMap<>();
        Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        List<Map<String, Object>> fieldStaffs = staffGraphRepository.getFieldStaff(organizationId, unitId);
        logger.debug("field staff found is" + fieldStaffs);
        Map<String, Object> staffData;

        for (Map fieldStaff : fieldStaffs) {
            staffData = (Map<String, Object>) fieldStaff.get("data");
            Map<String, Object> workScheduleMetaData = new HashMap<>();
            workScheduleMetaData.put("fmvtid", staffData.get("fmVTID"));
            workScheduleMetaData.put("fmextID", staffData.get("fmVTID"));
            workScheduleMetaData.put("type", -1); // Zero : Engineer is available for scheduling
            workScheduleMetaData.put("info", "Create Workschedule from 0500 to 1700");
            workScheduleMetaData.put("startLocation", -1);
            workScheduleMetaData.put("endLocation", -1);
            Map<String, Object> dateTimeInfo = new HashMap<>();
            dateTimeInfo.put("startDate", DateConverter.convertToDate("16/03/2017")); //Assigning Available starting from tomorrow
            dateTimeInfo.put("endDate", DateConverter.convertToDate("01/10/2019")); //till day after tomorrow
            int flsResponse = scheduler.createEngineerWorkSchedule(workScheduleMetaData, dateTimeInfo, flsCredentials);
            logger.info("Fls response after syncing work schedule:: " + flsResponse);

        }
        workScheduleStatus.put("message", "success");
        return workScheduleStatus;*/
        return null;
    }


    public Staff createStaffFromWeb(Long unitId, StaffCreationDTO payload) {

        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound",unitId);

        }
        User user = Optional.ofNullable(userGraphRepository.findByEmail(payload.getPrivateEmail().trim())).orElse(new User());
        Staff staff = staffGraphRepository.findByExternalId(payload.getExternalId());
        if (Optional.ofNullable(staff).isPresent()) {
            exceptionService.duplicateDataException("message.staff.externalid.alreadyexist");

        }
        setBasicDetailsOfUser(user, payload);
        Organization parent = null;
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        staff = createStaffObject(parent, unit, payload);
        Client client = createClientObject(staff, payload.getCprNumber());
        boolean isEmploymentExist = (staff.getId()) != null;
        staff.setUser(user);
        staff.setClient(client);
        staffGraphRepository.save(staff);
        createEmployment(parent, unit, staff, payload.getAccessGroupId(), DateUtil.getIsoDateInLong(payload.getEmployedSince()), isEmploymentExist);
        staff.setUser(null); // removing user to send in FE
//        staff.setGender(user.getGender());
      //  plannerSyncService.publishStaff(unitId, staff, IntegrationOperation.CREATE);
        return staff;
    }

    public User createUnitManagerForNewOrganization(Long organizationId, StaffCreationDTO staffCreationPOJOData) {
        User user = userGraphRepository.findByEmail(staffCreationPOJOData.getPrivateEmail().trim());
        if (!Optional.ofNullable(user).isPresent()) {
            user = new User();
            setBasicDetailsOfUser(user, staffCreationPOJOData);
            userGraphRepository.save(user);
        }
        createUnitManagerAndEmployment(organizationId, user);
        return user;
    }

    private void setBasicDetailsOfUser(User user, StaffCreationDTO staffCreationDTO) {
        user.setEmail(staffCreationDTO.getPrivateEmail());
        user.setUserName(staffCreationDTO.getPrivateEmail());
        user.setFirstName(staffCreationDTO.getFirstName());
        user.setGender(CPRUtil.getGenderFromCPRNumber(staffCreationDTO.getCprNumber()));
        user.setLastName(staffCreationDTO.getLastName());
        String defaultPassword = user.getFirstName().trim() + "@kairos";
        user.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
        user.setCprNumber(staffCreationDTO.getCprNumber());
        if (!StringUtils.isBlank(staffCreationDTO.getCprNumber())) {
            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(staffCreationDTO.getCprNumber()));
            user.setAge(Integer.valueOf(staffCreationDTO.getCprNumber().substring(staffCreationDTO.getCprNumber().length() - 1)));
        }
    }

    private Staff createStaffObject(Organization parent, Organization unit, StaffCreationDTO payload) {

        StaffQueryResult staffQueryResult;
        if (Optional.ofNullable(parent).isPresent()) {
            staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(parent.getId(), payload.getExternalId());
        } else {
            staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(unit.getId(), payload.getExternalId());
        }
        Staff staff;
        if (Optional.ofNullable(staffQueryResult).isPresent()) {
            staff = staffQueryResult.getStaff();
        } else {
            logger.info("Creating new staff with kmd external id " + payload.getExternalId() + " in unit " + unit.getId());
            staff = new Staff();
        }
        staff.setEmail(payload.getPrivateEmail());
        staff.setInactiveFrom(payload.getInactiveFrom());
        //staff.setEmployedSince(payload.getEmployedSince());
        staff.setExternalId(payload.getExternalId());
        staff.setFirstName(payload.getFirstName());
        staff.setLastName(payload.getLastName());
        staff.setFamilyName(payload.getFamilyName());
        staff.setCprNumber(payload.getCprNumber());
        ContactAddress contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(unit);
        staff.setContactAddress(contactAddress);

        ObjectMapper objectMapper = new ObjectMapper();
        ContactDetail contactDetail = objectMapper.convertValue(payload, ContactDetail.class);
        staff.setContactDetail(contactDetail);

        staff.setCurrentStatus(payload.getCurrentStatus());
        if (Optional.ofNullable(staffQueryResult).isPresent()) {
            contactAddress.setId(staffQueryResult.getContactAddressId());
            contactDetail.setId(staffQueryResult.getContactDetailId());
        }
        if (Optional.ofNullable(payload.getEngineerTypeId()).isPresent()) {
            EngineerType engineerType = engineerTypeGraphRepository.findOne(payload.getEngineerTypeId());
            staff.setEngineerType(engineerType);
        }
        return staff;
    }

    private Client createClientObject(Staff staff, String cprNumber) {
        Client client = clientGraphRepository.findByCprNumber(cprNumber);
        logger.debug("Staff cpr number", cprNumber);
        if (!Optional.ofNullable(client).isPresent()) {
            logger.debug("NO CITIZEN found by cpr : cpr number", cprNumber);
            client = new Client();
            client.setFirstName(staff.getFirstName());
            client.setLastName(staff.getLastName());
            client.setEmail(staff.getEmail());
            client.setCprNumber(cprNumber);
            client.setGender(CPRUtil.getGenderFromCPRNumber(client.getCprNumber()));
            client.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(cprNumber));
        }
        return client;
    }

    private void createEmployment(Organization organization,
                                  Organization unit, Staff staff, Long accessGroupId, Long employedSince, boolean employmentAlreadyExist) {
        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.staff.accessgroup.notfound",accessGroupId);

        }
        Employment employment;
        if (employmentAlreadyExist) {
            employment = (Optional.ofNullable(organization).isPresent()) ?
                    employmentGraphRepository.findEmployment(organization.getId(), staff.getId()) :
                    employmentGraphRepository.findEmployment(unit.getId(), staff.getId());
        } else {
            employment = new Employment();
        }
        employment.setName("Working as staff");
        employment.setStaff(staff);
        employment.setStartDateMillis(employedSince);

        UnitPermission unitPermission = new UnitPermission();
        unitPermission.setOrganization(unit);
        unitPermission.setAccessGroup(accessGroup);
        //set permission in unit employment
//        AccessPermission accessPermission = new AccessPermission(accessGroup);
//        UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitPermission, accessPermission);
//        unitEmpAccessGraphRepository.save(unitEmpAccessRelationship);
//        accessPageService.setPagePermissionToStaff(accessPermission, accessGroup.getId());
        employment.getUnitPermissions().add(unitPermission);
        save(employment);

        if (Optional.ofNullable(organization).isPresent()) {
            if (Optional.ofNullable(organization.getEmployments()).isPresent()) {
                organization.getEmployments().add(employment);
                organizationGraphRepository.save(organization);
            } else {
                List<Employment> employments = new ArrayList<>();
                employments.add(employment);
                organization.setEmployments(employments);
                organizationGraphRepository.save(organization);
            }
        } else {
            if (Optional.ofNullable(unit.getEmployments()).isPresent()) {
                unit.getEmployments().add(employment);
                organizationGraphRepository.save(unit);
            } else {
                List<Employment> employments = new ArrayList<>();
                employments.add(employment);
                unit.setEmployments(employments);
                organizationGraphRepository.save(unit);
            }

        }
//        if (accessGroup.isTypeOfTaskGiver()) {
//            Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unit.getId());
//            if (StringUtils.isBlank(flsCredentials.get("flsDefaultUrl"))) {
//                throw new FlsCredentialException("Fls credentials not found");
//            }
//            employmentService.syncStaffInVisitour(staff, unit.getId(), flsCredentials);
//        }
    }


    private void createUnitManagerAndEmployment(Long organizationId, User user) {

        Organization organization = organizationGraphRepository.findOne(organizationId);
        Organization parent;
        if (organization.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organizationId);

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(organizationId);
        }
        if (!Optional.ofNullable(parent).isPresent()) {
            parent = organization;
        }

        Staff staff = new Staff(user.getEmail(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getFirstName(), StaffStatusEnum.ACTIVE, null, user.getCprNumber());

        Employment employment = new Employment();
        employment.setStaff(staff);
        staff.setUser(user);

        employment.setName(UNIT_MANAGER_EMPLOYMENT_DESCRIPTION);
        employment.setStaff(staff);
        employment.setStartDateMillis(DateUtil.getCurrentDateMillis());

        parent.getEmployments().add(employment);

        UnitPermission unitPermission = new UnitPermission();
        unitPermission.setOrganization(parent);
        AccessGroup accessGroup = accessGroupRepository.getAccessGroupOfOrganizationByRole(parent.getId(), AccessGroupRole.MANAGEMENT.toString()); // findOne(accessGroupId);
        if (Optional.ofNullable(accessGroup).isPresent()) {
            unitPermission.setAccessGroup(accessGroup);
        }

        employment.getUnitPermissions().add(unitPermission);
        save(employment);
    }

    public Staff createStaffObject(User user, Staff staff, Long engineerTypeId, Organization unit) {
        ContactAddress contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(unit);

        if (contactAddress != null)
            staff.setContactAddress(contactAddress);

        if (engineerTypeId != null)
            staff.setEngineerType(engineerTypeGraphRepository.findOne(engineerTypeId));
        staff.setUser(user);
        staff.setOrganizationId(unit.getId());
        staff = staffGraphRepository.save(staff);

        if (unit == null) {
            exceptionService.internalServerError("error.unit.notNull");

        }

        Organization parent = null;
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        if (parent == null) {
            if (employmentGraphRepository.findEmployment(unit.getId(), staff.getId()) == null) {
                employmentGraphRepository.createEmployments(unit.getId(), Arrays.asList(staff.getId()), unit.getId());
            }
        } else {
            if (employmentGraphRepository.findEmployment(parent.getId(), staff.getId()) == null) {
                employmentGraphRepository.createEmployments(parent.getId(), Arrays.asList(staff.getId()), unit.getId());
            }
        }
        return staff;
    }

    private void updateStaffPersonalInfoInFLS(Staff staff, long unitId) {
        logger.info(":::::::::::::: Start updating personal info to FLS :::::::::::::");
        Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map<String, Object> engineerMetaData = new HashMap<>();
        engineerMetaData.put("fmvtid", staff.getVisitourId());
        engineerMetaData.put("fmextID", staff.getVisitourId());
        engineerMetaData.put("speedPercent", staff.getSpeedPercent());
        engineerMetaData.put("workPercent", staff.getWorkPercent());
        engineerMetaData.put("overtime", staff.getOvertime());
        engineerMetaData.put("costDay", staff.getCostDay());
        engineerMetaData.put("costCall", staff.getCostCall());
        engineerMetaData.put("costKm", staff.getCostKm());
        engineerMetaData.put("costHour", staff.getCostHour());
        engineerMetaData.put("costHourOvertime", staff.getCostHourOvertime());
        engineerMetaData.put("capacity", staff.getCapacity());
        int code = scheduler.createEngineer(engineerMetaData, flsCredentials);
        logger.info(" Status code :: " + code);
    }


    public void updateStaffFromExcel(MultipartFile multipartFile) {

        int staffUpdated = 0;

        List<Staff> staffList = new ArrayList<>();

        try (InputStream stream = multipartFile.getInputStream()) {
            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                exceptionService.internalServerError("error.xssfsheet.noMoreRow",2);

            }

            Staff staff;
            Cell cell;
            Row row;
            long staffId;
            String firstName;
            String lastName;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                if (row.getRowNum() > 0) {
                    cell = row.getCell(0);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    staffId = Long.valueOf(cell.getStringCellValue());

                    staff = staffGraphRepository.findOne(staffId);
                    if (staff != null) {
                        cell = row.getCell(1);
                        firstName = cell.getStringCellValue();
                        cell = row.getCell(2);
                        lastName = cell.getStringCellValue();

                        staff.setFirstName(firstName);
                        staff.setLastName(lastName);
                        staffList.add(staff);
                        staffUpdated++;
                    }
                }
            }
            staffGraphRepository.saveAll(staffList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("total staff updated  " + staffUpdated);
    }


    public Map createUnitManager(long unitId, UnitManagerDTO unitManagerDTO) {

        User user = userGraphRepository.findByEmail(unitManagerDTO.getEmail());
        Organization unit = organizationGraphRepository.findOne(unitId);
        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        final String password = unitManagerDTO.getFirstName().trim().toLowerCase() + "@kairos";
        ObjectMapper mapper = new ObjectMapper();
        Map unitManagerDTOMap = mapper.convertValue(unitManagerDTO, Map.class);
        if (user == null) {
            logger.info("Unit manager is null..creating new user first");
            user = new User();
            user.setUserName(unitManagerDTO.getEmail());
            user.setEmail(unitManagerDTO.getEmail());
            user.setFirstName(unitManagerDTO.getFirstName().trim());
            user.setLastName(unitManagerDTO.getLastName().trim());
            user.setContactDetail(unitManagerDTO.getContactDetail());
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            userGraphRepository.save(user);
            Staff staff = createStaff(user);
            unitManagerDTOMap.put("id", staff.getId());
            employmentService.createEmploymentForUnitManager(staff, parent, unit, unitManagerDTO.getAccessGroupId());
            sendEmailToUnitManager(unitManagerDTO, password);
            return unitManagerDTOMap;
        } else {
            long organizationId = (parent == null) ? unitId : parent.getId();
            if (staffGraphRepository.countOfUnitEmployment(organizationId, unitId, user.getEmail()) == 0) {
                Staff staff = createStaff(user);
                unitManagerDTOMap.put("id", staff.getId());
                employmentService.createEmploymentForUnitManager(staff, parent, unit, unitManagerDTO.getAccessGroupId());
                userGraphRepository.save(user);
                sendEmailToUnitManager(unitManagerDTO, password);
                return unitManagerDTOMap;
            } else {
                return null;
            }
        }
    }

    private Staff createStaff(User user) {
        Staff staff = new Staff();
        staff.setEmail(user.getEmail());
        staff.setFirstName(user.getFirstName());
        staff.setLastName(user.getLastName());
        staff.setUser(user);
        staff.setContactDetail(user.getContactDetail());
        staffGraphRepository.save(staff);
        return staff;
    }

    public Map<String, Object> getUnitManager(long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId);

        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }


        List<Map<String, Object>> unitManagers;
        if (parent == null)
            unitManagers = staffGraphRepository.getUnitManagers(unitId, unitId);
        else
            unitManagers = staffGraphRepository.getUnitManagers(parent.getId(), unitId);

        List<Map<String, Object>> unitManagerList = new ArrayList<>();
        for (Map<String, Object> unitManager : unitManagers) {
            unitManagerList.add((Map<String, Object>) unitManager.get("data"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("unitManager", unitManagerList);
        map.put("accessGroups", accessGroupRepository.getAccessGroups(unitId));
        return map;
    }


    private void sendEmailToUnitManager(UnitManagerDTO unitManagerDTO, String password) {

        String body = "Hi,\n\n" + "You are assigned as an unit manager and to get access in KairosPlanning.\n" + "Your username " + unitManagerDTO.getEmail() + " and password is " + password + "\n\n Thanks";
        String subject = "You are a unit manager at KairosPlanning";
        mailService.sendPlainMail(unitManagerDTO.getEmail(), body, subject);
    }

    public List<Staff> getUploadedStaffByOrganizationId(Long organizationId) {
        return staffGraphRepository.getUploadedStaffByOrganizationId(organizationId);
    }


    public UnitManagerDTO updateUnitManager(Long staffId, UnitManagerDTO unitManagerDTO) {

        Staff staff = staffGraphRepository.findOne(staffId);
        User user = userGraphRepository.findByEmail(unitManagerDTO.getEmail());
        staff.setFirstName(unitManagerDTO.getFirstName());
        staff.setLastName(unitManagerDTO.getLastName());
        staff.setContactDetail(unitManagerDTO.getContactDetail());
        user.setFirstName(unitManagerDTO.getFirstName().trim());
        user.setLastName(unitManagerDTO.getLastName().trim());
        user.setContactDetail(unitManagerDTO.getContactDetail());
        userGraphRepository.save(user);
        staffGraphRepository.save(staff);
        unitManagerDTO.setStaffId(staffId);
        return unitManagerDTO;

    }

    /**
     * @param unitId
     * @param staffId
     * @param date
     * @return
     * @auther anil maurya
     */
    public List<StaffTaskDTO> getAssignedTasksOfStaff(long unitId, long staffId, String date) {

        Organization unit = organizationGraphRepository.findOne(unitId);
        Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
        Staff staff = staffGraphRepository.getStaffByUnitId(parentOrganization.getId(), staffId);
        if (staff == null) {
            exceptionService.dataNotFoundByIdException("message.staff.id.notFound");

        }
        List<StaffAssignedTasksWrapper> tasks = taskServiceRestClient.getAssignedTasksOfStaff(staffId, date);
        List<Long> citizenIds = tasks.stream().map(task -> task.getId()).collect(Collectors.toList());
        List<Client> clients = clientGraphRepository.findByIdIn(citizenIds);
        ObjectMapper objectMapper = new ObjectMapper();
        StaffTaskDTO staffTaskDTO;
        List<StaffTaskDTO> staffTaskDTOS = new ArrayList<>(clients.size());
        int taskIndex = 0;
        for (Client client : clients) {
            staffTaskDTO = objectMapper.convertValue(client, StaffTaskDTO.class);
            staffTaskDTO.setTasks(tasks.get(taskIndex).getTasks());
            staffTaskDTOS.add(staffTaskDTO);
            taskIndex++;
        }
        return staffTaskDTOS;
    }


    public Map<String, Object> getTeamStaffAndStaffSkill(Long organizationId, List<Long> staffIds) {
        Map<String, Object> responseMap = new HashMap();
        List<Object> teamStaffList = new ArrayList<>();
        List<Object> staffList = new ArrayList<>();
        List<Map<String, Object>> teamStaffs = staffGraphRepository.getTeamStaffList(organizationId, staffIds);
        List<Map<String, Object>> staffs = staffGraphRepository.getSkillsOfStaffs(staffIds);
        for (Map<String, Object> map : teamStaffs) {
            Object o = map.get("data");
            teamStaffList.add(o);
        }
        for (Map<String, Object> map : staffs) {
            Object o = map.get("data");
            staffList.add(o);
        }

        responseMap.put("teamStaffList", teamStaffList);
        responseMap.put("staffs", staffList);
        return responseMap;
    }


    /**
     * @return
     * @auther anil maurya
     * this method is called from task micro service
     */
    public ClientStaffInfoDTO getStaffInfo(String loggedInUserName) {
        Staff staff = staffGraphRepository.getByUser(userGraphRepository.findByUserNameIgnoreCase(loggedInUserName).getId());
        if (staff == null) {
            exceptionService.dataNotFoundByIdException("message.staff.id.notFound");

        }
        return new ClientStaffInfoDTO(staff.getId());
    }

    public Staff getStaffById(long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId, 0);
        if (staff == null) {
            logger.debug("Searching staff by id " + staffId);
            exceptionService.dataNotFoundByIdException("message.staff.id.incorrect",staffId);

        }
        return staff;
    }

    public List<Long> getCountryAdminIds(long organizationId) {
        return staffGraphRepository.getCountryAdminIds(organizationId);

    }

    public List<Long> getUnitManagerIds(long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId);

        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }


        List<Long> unitManagers;
        if (parent == null)
            unitManagers = staffGraphRepository.getUnitManagersIds(unitId, unitId);
        else
            unitManagers = staffGraphRepository.getUnitManagersIds(parent.getId(), unitId);


        return unitManagers;
    }

    /*public List<StaffPersonalDetailDTO> getAllStaffByUnitId(Long unitId, Boolean allStaffRequired) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            throw new DataNotFoundByIdException("unit  not found  Unit ID: " + unitId);
        }
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS = new ArrayList<>();
        if (allStaffRequired) {
            Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
            // unit is parent so fetching all staff from itself
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffByUnitId(parentOrganization.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        } else {
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffHavingUnitPositionByUnitId(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        }
        return staffPersonalDetailDTOS;
    }*/

    public List<StaffPersonalDetailDTO> getAllStaffByUnitId(Long unitId, Boolean allStaffRequired) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",unitId);

        }
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS = new ArrayList<>();
        if (allStaffRequired) {
            Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
            // unit is parent so fetching all staff from itself
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffByUnitId(parentOrganization.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        } else {
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffHavingUnitPositionByUnitId(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        }
        return staffPersonalDetailDTOS;
    }

    public List<StaffPersonalDetailDTO> getStaffInfoById(long staffId, long unitId) {
        List<StaffPersonalDetailDTO> staffPersonalDetailList = staffGraphRepository.getStaffInfoById(unitId, staffId);
        if (!Optional.ofNullable(staffPersonalDetailList).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staffandunit.id.notfound",staffId,unitId);

        }
        return staffPersonalDetailList;

    }

    public StaffAdditionalInfoDTO getStaffEmploymentData(long staffId, Long unitPositionId, long id, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        Long unitId = organization.getId();
        List<TimeSlotSet> timeSlotSets = timeSlotGraphRepository.findTimeSlotSetsByOrganizationId(unitId, organization.getTimeSlotMode(), TimeSlotType.SHIFT_PLANNING);
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.findTimeSlotsByTimeSlotSet(timeSlotSets.get(0).getId());
        //List<TimeSlotSetDTO> timeSlotSetDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(timeSlotSets,TimeSlotSetDTO.class);
        StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffId(unitId, staffId);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAdditionalInfoQueryResult,StaffAdditionalInfoDTO.class);
       // StaffUnitPositionDetails unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        StaffUnitPositionDetails unitPosition = unitPositionService.getUnitPositionCTA(unitPositionId,unitId);
        staffAdditionalInfoDTO.setUnitId(organization.getId());
        staffAdditionalInfoDTO.setOrganizationNightEndTimeTo(organization.getNightEndTimeTo());
        staffAdditionalInfoDTO.setTimeSlotSets(ObjectMapperUtils.copyPropertiesOfListByMapper(timeSlotWrappers, com.kairos.activity.client.dto.TimeSlotWrapper.class));
        staffAdditionalInfoDTO.setOrganizationNightStartTimeFrom(organization.getNightStartTimeFrom());
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        staffAdditionalInfoDTO.setDayTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dayTypes,DayTypeDTO.class));
        UserAccessRoleDTO userAccessRole = accessGroupService.checkIfUserHasAccessByRoleInUnit(unitId);
        staffAdditionalInfoDTO.setUser(userAccessRole);
        if (Optional.ofNullable(unitPosition).isPresent()) {
            staffAdditionalInfoDTO.setUnitPosition(unitPosition);
            //WTAResponseDTO wtaResponseDTO = workingTimeAgreementGraphRepository.findRuleTemplateByWTAId(unitPositionId);
            //staffAdditionalInfoQueryResult.getUnitPosition().setWorkingTimeAgreement(wtaResponseDTO);
        }
        staffAdditionalInfoQueryResult.setUnitTimeZone(organization.getTimeZone());
        return staffAdditionalInfoDTO;
    }

    public StaffAdditionalInfoQueryResult verifyStaffBelongsToUnit(long staffId, long id, String type) {
        Long unitId = -1L;
        unitId = organizationService.getOrganization(id, type);
        StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult = new StaffAdditionalInfoQueryResult();
        staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffId(unitId, staffId);
        return staffAdditionalInfoQueryResult;
    }

    public StaffFilterDTO addStaffFavouriteFilters(StaffFilterDTO staffFilterDTO, long organizationId) {
        /*StaffFavouriteFilter alreadyExistFilter = staffGraphRepository.getStaffFavouriteFiltersByStaffAndView(staffId, staffFilterDTO.getModuleId());
        if(Optional.ofNullable(alreadyExistFilter).isPresent()){
            throw new DuplicateDataException("StaffFavouriteFilter already exist !");
        }*/


        StaffFavouriteFilter staffFavouriteFilter = new StaffFavouriteFilter();
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);
        AccessPage accessPage = accessPageService.findByModuleId(staffFilterDTO.getModuleId());
//        staffFavouriteFilter.setAccessPage(accessPage);
//        staffFavouriteFilter.setFilterJson(staffFilterDTO.getFilterJson());
        staffFavouriteFilter.setName(staffFilterDTO.getName());
        save(staffFavouriteFilter);
        staff.addFavouriteFilters(staffFavouriteFilter);
        save(staff);
//        staffFilterDTO.setFilterJson(staffFavouriteFilter.getFilterJson());
        staffFilterDTO.setModuleId(accessPage.getModuleId());
        staffFilterDTO.setName(staffFavouriteFilter.getName());
        staffFilterDTO.setId(staffFavouriteFilter.getId());
        return staffFilterDTO;


    }

    public StaffFilterDTO updateStaffFavouriteFilters(StaffFilterDTO staffFilterDTO, long organizationId) {
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);
        StaffFavouriteFilter staffFavouriteFilter = staffGraphRepository.getStaffFavouriteFiltersById(staff.getId(), staffFilterDTO.getId());
        if (!Optional.ofNullable(staffFavouriteFilter).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.stafffavouritefilter.notfound",staffFilterDTO.getId());

        }
        /*AccessPage accessPage = accessPageService.findByModuleId(staffFilterDTO.getModuleId());
        staffFavouriteFilter.setAccessPage(accessPage);*/
//        staffFavouriteFilter.setFilterJson(staffFilterDTO.getFilterJson());
        staffFavouriteFilter.setName(staffFilterDTO.getName());
//        staffFavouriteFilter.setEnabled(true);
        save(staffFavouriteFilter);
//        staffFilterDTO.setFilterJson(staffFavouriteFilter.getFilterJson());
        // staffFilterDTO.setModuleId(accessPage.getModuleId());
        return staffFilterDTO;

    }

    public boolean removeStaffFavouriteFilters(Long staffFavouriteFilterId, long organizationId) {
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);
        StaffFavouriteFilter staffFavouriteFilter = staffGraphRepository.getStaffFavouriteFiltersById(staff.getId(), staffFavouriteFilterId);
        if (!Optional.ofNullable(staffFavouriteFilter).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.stafffavouritefilter.notfound",staffFavouriteFilterId);

        }

//        staffFavouriteFilter.setEnabled(false);
        save(staffFavouriteFilter);
        return true;

    }

    public List<FavoriteFilterQueryResult> getStaffFavouriteFilters(String moduleId, long organizationId) {
        Long userId = UserContext.getUserDetails().getId();
        Staff staff = staffGraphRepository.getStaffByUserId(userId, organizationId);
        return staffGraphRepository.getStaffFavouriteFiltersByStaffAndView(staff.getId(), moduleId);
    }


    /**
     * This method return Staff from given user id
     *
     * @param userId
     * @return
     */
    public Staff getStaffByUserId(Long userId) {
        return staffGraphRepository.getByUser(userId);
    }

    public boolean importStaffFromTimeCare(List<TimeCareStaffDTO> timeCareStaffDTOS, String externalId) {

        Organization organization = organizationGraphRepository.findByExternalId(externalId);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.externalid.notfound");

        }

        List<TimeCareStaffDTO> timeCareStaffByWorkPlace = timeCareStaffDTOS.stream().filter(timeCareStaffDTO -> timeCareStaffDTO.getParentWorkPlaceId().equals(externalId)).
                collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();

        AccessGroup accessGroup = accessGroupRepository.findTaskGiverAccessGroup(organization.getId());
        if (accessGroup == null) {
            exceptionService.dataNotFoundByIdException("message.taskgiver.accesgroup.notPresent");

        }

        for (TimeCareStaffDTO timeCareStaffDTO : timeCareStaffByWorkPlace) {

            String email = (timeCareStaffDTO.getEmail() == null) ? timeCareStaffDTO.getFirstName() + KAIROS_EMAIL : timeCareStaffDTO.getEmail();
            User user = Optional.ofNullable(userGraphRepository.findByEmail(email.trim())).orElse(new User());
            if (staffGraphRepository.staffAlreadyInUnit(Long.valueOf(timeCareStaffDTO.getId()), organization.getId())) {
                exceptionService.duplicateDataException("message.staff.alreadyexist");

            }

            if (timeCareStaffDTO.getGender().equalsIgnoreCase("m")) {
                timeCareStaffDTO.setGender(Gender.MALE.toString());
            } else if (timeCareStaffDTO.getGender().equalsIgnoreCase("f")) {
                timeCareStaffDTO.setGender(Gender.FEMALE.toString());
            } else {
                timeCareStaffDTO.setGender(null);
            }
            StaffCreationDTO payload = objectMapper.convertValue(timeCareStaffDTO, StaffCreationDTO.class);
            payload.setAccessGroupId(accessGroup.getId());
            payload.setPrivateEmail(email);
            setBasicDetailsOfUser(user, payload);
            Staff staff = mapDataInStaffObject(timeCareStaffDTO, organization, email);
            boolean isEmploymentExist = (staff.getId()) != null;
            staff.setUser(user);
            staffGraphRepository.save(staff);
            createEmployment(organization, organization, staff, payload.getAccessGroupId(), null, isEmploymentExist);
        }
        return true;
    }

    private Staff mapDataInStaffObject(TimeCareStaffDTO timeCareStaffDTO, Organization organization, String email) {

        StaffQueryResult staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(organization.getId(), Long.valueOf(timeCareStaffDTO.getId()));

        Staff staff = (Optional.ofNullable(staffQueryResult).isPresent()) ? staffQueryResult.getStaff() : new Staff();
        ContactAddress contactAddress;
        if (timeCareStaffDTO.getZipCode() == null) {
            contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(organization);
            if (staffQueryResult != null) {
                contactAddress.setId(staffQueryResult.getContactAddressId());
            }
        } else {
            contactAddress = new ContactAddress();
            contactAddress.setStreet1(timeCareStaffDTO.getAddress());
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(timeCareStaffDTO.getZipCode());
            if (matcher.find()) {
                ZipCode zipCode = zipCodeGraphRepository.findByZipCode(Integer.valueOf(matcher.group(0)));
                contactAddress.setZipCode(zipCode);
            }
            if (staffQueryResult != null) {
                contactAddress.setId(staffQueryResult.getContactAddressId());
            }
            matcher = pattern.matcher(timeCareStaffDTO.getAddress());
            if (matcher.find()) {
                contactAddress.setHouseNumber(matcher.group(0));
            }
        }
        staff.setContactAddress(contactAddress);
        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setPrivatePhone(timeCareStaffDTO.getCellPhoneNumber());
        contactDetail.setLandLinePhone(timeCareStaffDTO.getTelephoneNumber());
        contactDetail.setPrivateEmail(email);
        if (staffQueryResult != null) {
            contactDetail.setId(staffQueryResult.getContactDetailId());
        }
        staff.setContactDetail(contactDetail);
        staff.setEmail(email);
        staff.setExternalId(Long.valueOf(timeCareStaffDTO.getId()));
        staff.setFirstName(timeCareStaffDTO.getFirstName());
        staff.setLastName(timeCareStaffDTO.getLastName());
        return staff;


    }

    public List<com.kairos.response.dto.web.StaffDTO> getStaffByExperties(Long unitId, List<Long> expertiesIds) {
        List<Staff> staffs = staffGraphRepository.getStaffByExperties(unitId, expertiesIds);
        List<Skill> skills = staffGraphRepository.getSkillByStaffIds(staffs.stream().map(s -> s.getId()).collect(Collectors.toList()));
        List<com.kairos.response.dto.web.StaffDTO> staffDTOS = new ArrayList<>(staffs.size());
        staffs.forEach(s -> {
            com.kairos.response.dto.web.StaffDTO staffDTO = new com.kairos.response.dto.web.StaffDTO(s.getId(), s.getFirstName(), getSkillSet(skills));
            EmploymentUnitPositionDTO employmentUnitPositionDTO = unitPositionService.getUnitPositionsOfStaff(unitId, s.getId(), true);
            List<UnitPositionQueryResult> unitPositions = employmentUnitPositionDTO.getUnitPositions();
            expertiesIds.forEach(expertiseId -> {
                unitPositions.forEach(unitPosition -> {
                    if (unitPosition.getExpertise().getId().equals(expertiseId)) {
                        staffDTO.setUnitEmploymentPositionId(unitPosition.getId());
                        staffDTOS.add(staffDTO);
                    }
                });
            });

        });
        return staffDTOS;
    }


    public Set<SkillDTO> getSkillSet(List<Skill> skills) {
        return skills.stream().map(skill -> new SkillDTO(skill.getId(), skill.getName(), skill.getDescription())).collect(Collectors.toSet());
    }

    private Integer nextSeniorityLevelInMonths( List<SeniorityLevel> seniorityLevels,int currentExperienceInMonths){
        Integer nextSeniorityLevelInMonths=null;
        for(int i=0;i<seniorityLevels.size();i++){
            if(currentExperienceInMonths < seniorityLevels.get(i).getFrom()*12){
                nextSeniorityLevelInMonths= seniorityLevels.get(i).getFrom()*12-currentExperienceInMonths;
                break;
            }
        }
        return nextSeniorityLevelInMonths;
    }

    public List<UnitStaffQueryResult> getUnitWiseStaffList(){
        return staffGraphRepository.getStaffListOfUnitWithBasicInfo();
    }

    public boolean savePersonalizedSettings(Long unitId, StaffPreferencesDTO staffPreferencesDTO){
        Organization parentOrganization=organizationService.fetchParentOrganization(unitId);
        Staff staff=staffGraphRepository.findByUserId(UserContext.getUserDetails().getId(),parentOrganization.getId());
        StaffSettingsQueryResult staffSettingsQueryResult=staffGraphRepository.fetchStaffSettingDetails(staff.getId());
        switch (staffPreferencesDTO.getShiftBlockType()){
            case SHIFT:
                staffSettingsQueryResult.getStaffPreferences().getActivityId().add(staffPreferencesDTO.getActivityId());
                break;
            case DAY:
                staffSettingsQueryResult.getStaffPreferences().getDateForDay().add(DateUtil.getIsoDateInLong(staffPreferencesDTO.getStartDate().toString()));
                break;
            case WEEK:
                staffSettingsQueryResult.getStaffPreferences().getDateForWeek().add(DateUtil.getStartDateOfWeekFromDate(staffPreferencesDTO.getStartDate()));
                break;
            default:
                exceptionService.actionNotPermittedException("exception.actionNotPermittedException","No Shift Block Type found");
        }
        StaffSettings staffSettings=staffSettingsQueryResult.getStaffSettings();
        staffSettings.setStaffPreferences(staffSettingsQueryResult.getStaffPreferences());
        staff.setStaffSettings(staffSettings);
        save(staff);
        return true;
    }

}

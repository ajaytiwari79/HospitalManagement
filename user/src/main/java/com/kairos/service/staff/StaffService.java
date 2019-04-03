package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.service.mail.MailService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.task.StaffAssignedTasksWrapper;
import com.kairos.dto.activity.task.StaffTaskDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.staff.client.ClientStaffInfoDTO;
import com.kairos.dto.user.staff.staff.StaffChatDetails;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.dto.user.user.password.PasswordUpdateByAdminDTO;
import com.kairos.dto.user.user.password.PasswordUpdateDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.UnitManagerDTO;
import com.kairos.persistence.model.staff.*;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitPermissionAccessPermissionRelationship;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.filter.FavoriteFilterQueryResult;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionLinesQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.rest_client.ChatRestClient;
import com.kairos.rest_client.TaskServiceRestClient;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.auth.UserService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TeamService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.system_setting.SystemLanguageService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.DateConverter;
import com.kairos.utils.FileUtil;
import com.kairos.utils.user_context.UserContext;
import com.kairos.wrapper.staff.StaffEmploymentTypeWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.service.unit_position.UnitPositionUtility.convertUnitPositionObject;
import static com.kairos.utils.FileUtil.createDirectory;

/**
 * Created by prabjot on 24/10/16.
 */
@Transactional
@Service
public class StaffService {
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
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    private TeamService teamService;
    @Inject
    private MailService mailService;
    @Inject
    private PositionService positionService;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private SkillService skillService;
    @Inject
    private StaffAddressService staffAddressService;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private UnitPermissionAndAccessPermissionGraphRepository unitPermissionAndAccessPermissionGraphRepository;
    @Inject
    private ClientGraphRepository clientGraphRepository;
    @Inject
    private TaskServiceRestClient taskServiceRestClient;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private UnitPositionGraphRepository unitPositionGraphRepository;
    @Inject
    private StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ChatRestClient chatRestClient;
    @Inject
    private SystemLanguageService systemLanguageService;
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private AccessPageRepository accessPageRepository;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private UserService userService;
    @Inject
    private StaffFavouriteFilterGraphRepository staffFavouriteFilterGraphRepository;
    @Inject
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Inject
    private TeamGraphRepository teamGraphRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffService.class);

    public String uploadPhoto(Long staffId, MultipartFile multipartFile) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        createDirectory(IMAGES_PATH);
        String fileName = DateUtils.getCurrentDate().getTime() + multipartFile.getOriginalFilename();
        final String path = IMAGES_PATH + File.separator + fileName;
        FileUtil.writeFile(path, multipartFile);
        staff.setProfilePic(fileName);
        staffGraphRepository.save(staff);
        return envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + fileName;

    }

    public boolean removePhoto(Long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return false;
        }
        staff.setProfilePic(null);
        staffGraphRepository.save(staff);
        return true;
    }

    public boolean updatePassword(PasswordUpdateDTO passwordUpdateDTO) {
        User user = userService.getUserById(UserContext.getUserDetails().getId());
        CharSequence oldPassword = CharBuffer.wrap(passwordUpdateDTO.getOldPassword());
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            CharSequence newPassword = CharBuffer.wrap(passwordUpdateDTO.getNewPassword());
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userGraphRepository.save(user);
        } else {
            exceptionService.dataNotMatchedException("message.staff.user.password.notmatch");
        }
        return true;
    }

    public boolean updatePasswordByManagement(Long staffId,PasswordUpdateByAdminDTO passwordUpdateDTO) {
        Staff staff = staffGraphRepository.findByStaffId(staffId);

        if(staff!=null){
            User userForStaff = staff.getUser();
            CharSequence newPassword = CharBuffer.wrap(passwordUpdateDTO.getNewPassword());
            userForStaff.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userGraphRepository.save(userForStaff);
        }else{
             exceptionService.dataNotMatchedException("message.staff.notfound");
        }
        return true;
    }

    public StaffPersonalDetail savePersonalDetail(long staffId, StaffPersonalDetail staffPersonalDetail, long unitId) throws ParseException {
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unitId);
        Organization parentOrganization = organizationService.fetchParentOrganization(unitId);
        Staff staffToUpdate = staffGraphRepository.findOne(staffId);
        if (staffToUpdate == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");
        }
        if (isNotNull(staffToUpdate.getContactDetail()) && isNotNull(staffToUpdate.getContactDetail().getPrivateEmail()) && !staffToUpdate.getContactDetail().getPrivateEmail().equals(staffPersonalDetail.getContactDetail().getPrivateEmail())) {
            if (staffGraphRepository.findStaffByEmailIdInOrganization(staffPersonalDetail.getContactDetail().getPrivateEmail(), parentOrganization.getId()) != null) {
                exceptionService.duplicateDataException("message.email.alreadyExist", "Staff", staffPersonalDetail.getContactDetail().getPrivateEmail());
            }
        }
        if (StaffStatusEnum.ACTIVE.equals(staffToUpdate.getCurrentStatus()) && StaffStatusEnum.FICTIVE.equals(staffPersonalDetail.getCurrentStatus())) {
            exceptionService.actionNotPermittedException("message.employ.notconvert.Fictive");
        }
        List<Expertise> oldExpertise = staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staffToUpdate.getId());
        List<Long> expertises = staffPersonalDetail.getExpertiseWithExperience().stream().map(StaffExperienceInExpertiseDTO::getExpertiseId).collect(Collectors.toList());
        if (!CollectionUtils.isEqualCollection(expertises, oldExpertise.stream().map(expertise -> expertise.getId()).collect(Collectors.toList())) && !userAccessRoleDTO.getManagement()) {
            exceptionService.actionNotPermittedException("message.unitposition.expertise.notchanged");
        }
        List<Expertise> expertiseList = expertiseGraphRepository.findAllById(expertises);
        Map<Long, Expertise> expertiseMap = expertiseList.stream().collect(Collectors.toMap(Expertise::getId, Function.identity()));

        List<StaffExperienceInExpertiseDTO> staffExperienceInExpertiseDTOList = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndExpertiseIds(staffId, expertises);
        Map<Long, StaffExperienceInExpertiseDTO> staffExperienceInExpertiseDTOMap = staffExperienceInExpertiseDTOList.stream().collect(Collectors.toMap(StaffExperienceInExpertiseDTO::getExpertiseId, Function.identity()));
        staffExpertiseRelationShipGraphRepository.unlinkExpertiseFromStaffExcludingCurrent(staffId, expertises);

        List<StaffExpertiseRelationShip> staffExpertiseRelationShips = new ArrayList<>();
        for (int i = 0; i < staffPersonalDetail.getExpertiseWithExperience().size(); i++) {
            Expertise expertise = expertiseMap.get(staffPersonalDetail.getExpertiseWithExperience().get(i).getExpertiseId());
            StaffExperienceInExpertiseDTO staffExperienceInExpertiseDTO = staffExperienceInExpertiseDTOMap.get(staffPersonalDetail.getExpertiseWithExperience().get(i).getExpertiseId());
            Long id = null;
            if (Optional.ofNullable(staffExperienceInExpertiseDTO).isPresent())
                id = staffExperienceInExpertiseDTO.getId();

            Date expertiseStartDate = staffPersonalDetail.getExpertiseWithExperience().get(i).getExpertiseStartDate();
            staffExpertiseRelationShips.add(new StaffExpertiseRelationShip(id, staffToUpdate, expertise, staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths(), expertiseStartDate));
            boolean isSeniorityLevelMatched = false;
            for (SeniorityLevel seniorityLevel : expertise.getSeniorityLevel()) {
                if (staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths() >= seniorityLevel.getFrom() * 12 &&
                        (seniorityLevel.getTo() == null || staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths() < seniorityLevel.getTo() * 12)) {
                    isSeniorityLevelMatched = true;
                    break;
                }
            }
            if (!isSeniorityLevelMatched) {
                exceptionService.actionNotPermittedException("error.noSeniorityLevelFound", "seniorityLevel " + staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths());
            }
        }
        if (CollectionUtils.isNotEmpty(staffExpertiseRelationShips)) {
            staffExpertiseRelationShipGraphRepository.saveAll(staffExpertiseRelationShips);
        }

        Language language = languageGraphRepository.findOne(staffPersonalDetail.getLanguageId());
        List<Expertise> expertise = expertiseGraphRepository.getExpertiseByIdsIn(staffPersonalDetail.getExpertiseIds());
        staffToUpdate.setLanguage(language);
        // Setting Staff Details)
        setStaffDetails(staffToUpdate, staffPersonalDetail);
        //saving addresses of staff
        staffAddressService.saveAddress(staffToUpdate,Arrays.asList(staffPersonalDetail.getPrimaryAddress(),staffPersonalDetail.getSecondaryAddress()));
        staffGraphRepository.save(staffToUpdate);

        if (oldExpertise != null) {
            List<Long> expertiseIds = oldExpertise.stream().map(Expertise::getId).collect(Collectors.toList());
            staffGraphRepository.removeSkillsByExpertise(staffToUpdate.getId(), expertiseIds);
        }
        List<Long> expertiseIds = expertise.stream().map(Expertise::getId).collect(Collectors.toList());
        staffGraphRepository.updateSkillsByExpertise(staffToUpdate.getId(), expertiseIds, DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime(), Skill.SkillLevel.ADVANCE);

        // Set if user is female and pregnant
        User user = userGraphRepository.getUserByStaffId(staffId);
        if (!user.getCprNumber().equals(staffPersonalDetail.getCprNumber())) {
            user.setCprNumber(staffPersonalDetail.getCprNumber());
            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(staffPersonalDetail.getCprNumber()));
        }
        user.setGender(staffPersonalDetail.getGender());
        user.setPregnant(Gender.FEMALE.equals(user.getGender()) && staffPersonalDetail.isPregnant());
        userGraphRepository.save(user);
        staffPersonalDetail.setPregnant(user.isPregnant());
        List<SectorAndStaffExpertiseQueryResult> staffExpertiseQueryResults = ObjectMapperUtils.copyPropertiesOfListByMapper(staffExpertiseRelationShipGraphRepository.getSectorWiseExpertiseWithExperience(staffId), SectorAndStaffExpertiseQueryResult.class);
        staffPersonalDetail.setSectorWiseExpertise(staffRetrievalService.getSectorWiseStaffAndExpertise(staffExpertiseQueryResults));

        if(isCollectionEmpty(staffPersonalDetail.getTeamIdsOfStaff())){
            teamGraphRepository.removeStaffFromAllTeams(staffId);
        }else{
            teamGraphRepository.assignStaffInTeams(staffId, staffPersonalDetail.getTeamIdsOfStaff());
        }

        return staffPersonalDetail;
    }


    public Map<String, Object> saveNotes(long staffId, String generalNote, String requestFromPerson) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff != null) {
            LOGGER.info("General note: " + generalNote + "\nPerson: " + requestFromPerson);
            staff.saveNotes(generalNote, requestFromPerson);
            staffGraphRepository.save(staff);
            return staff.retrieveNotes();
        }
        return null;
    }

    public Staff assignExpertiseToStaff(long staffId, List<Long> expertiseIds) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        List<StaffExpertiseRelationShip> staffExpertiseRelationShips = new ArrayList<>();
        List<Expertise> expertise = expertiseGraphRepository.getExpertiseByIdsIn(expertiseIds);
        for (Expertise currentExpertise : expertise) {
            StaffExpertiseRelationShip staffExpertiseRelationShip = new StaffExpertiseRelationShip(staff, currentExpertise, 0, DateUtils.getCurrentDate());
            staffExpertiseRelationShips.add(staffExpertiseRelationShip);
        }
        staffExpertiseRelationShipGraphRepository.saveAll(staffExpertiseRelationShips);
        return staff;
    }


    /*******************************************************************************************************/
    //Function to validate staff Mandatory Fields
    private List<String> validateStaffData(Row row, int[] mandatoryCellColumnIndexs,  XSSFSheet sheet) {
        List<String> errorMessage = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        for (int mandatoryCellColumnIndex : mandatoryCellColumnIndexs) {
            Cell cell = row.getCell(mandatoryCellColumnIndex, Row.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                errorMessage.add(headerRow.getCell(mandatoryCellColumnIndex).getStringCellValue());
            }
        }
        return errorMessage;
    }

    public StaffUploadBySheetQueryResult batchAddStaffToDatabase(long unitId, MultipartFile multipartFile, Long accessGroupId) {
        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGroup).isPresent()) {
            LOGGER.error("Access group not found");
            exceptionService.invalidRequestException("error.staff.accessgroup.notfound", accessGroupId);
        }

        if (accessGroup.getEndDate() != null && accessGroup.getEndDate().isBefore(DateUtils.getCurrentLocalDate())) {
            exceptionService.actionNotPermittedException("error.access.expired", accessGroup.getName());
        }

        List<StaffDTO> staffList = new ArrayList<>();
        List<StaffDTO> staffErrorList = new ArrayList<>();
        StaffUploadBySheetQueryResult staffUploadBySheetQueryResult = new StaffUploadBySheetQueryResult();
        staffUploadBySheetQueryResult.setStaffErrorList(staffErrorList);
        staffUploadBySheetQueryResult.setStaffList(staffList);

        Organization unit = organizationGraphRepository.findOne(unitId);
        if (unit == null) {
            LOGGER.info("Organization is null");
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
                exceptionService.internalServerError("error.xssfsheet.noMoreRow", 0);

            }

            Set<Long> externalIdsOfStaffToBeSaved = new HashSet<>();
            boolean headerSkipped = false;
            for (Row row : sheet) { // For each Row.
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                Cell cell = row.getCell(2); // Get the Cell at the Index / Column you want.
                if (cell != null) {
                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                    if (cell.getNumericCellValue() > 0) {
                        externalIdsOfStaffToBeSaved.add(new Double(cell.toString()).longValue());
                    }
                }
            }
            List<Long> alreadyAddedStaffIds = staffGraphRepository.findStaffByExternalIdIn(externalIdsOfStaffToBeSaved);
            // TODO get CountryId
            SystemLanguage defaultSystemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(unitId);
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (String.valueOf(row.getCell(0)) == null || String.valueOf(row.getCell(0)).isEmpty()) {
                    break;
                }
                if (row.getCell(0) == null) {
                    LOGGER.info("No more rows");
                    break;
                }
                // Skip headers
                if (row.getRowNum() == 0) {
                    continue;
                }
                // to check mandatory fields
                int[] mandatoryCellColumnIndexs = {2, 20, 21, 23, 24, 25, 28, 41};
                List<String> missingMandatoryFields = validateStaffData(row, mandatoryCellColumnIndexs, sheet);
                Long cprAsLong = null;
                String firstName = "";
                String lastName = "";
                String privateEmail = "";
                String externalIdValueAsString = "";
                if (isNotNull(row.getCell(41, Row.RETURN_BLANK_AS_NULL))) {
                    cprAsLong = new Double(getStringValueOfIndexedCell(row, 41)).longValue();
                }
                if (isNotNull(row.getCell(20, Row.RETURN_BLANK_AS_NULL))) {
                    firstName = getStringValueOfIndexedCell(row, 20);
                }
                if (isNotNull(row.getCell(21, Row.RETURN_BLANK_AS_NULL))) {
                    lastName = getStringValueOfIndexedCell(row, 21);
                }
                if (isNotNull(row.getCell(28, Row.RETURN_BLANK_AS_NULL))) {
                    privateEmail = getStringValueOfIndexedCell(row, 28);
                }
                externalIdValueAsString = getStringValueOfIndexedCell(row, 2);
                if (isCollectionNotEmpty(missingMandatoryFields) || cprAsLong == null || StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName) || StringUtils.isBlank(privateEmail) || StringUtils.isBlank(externalIdValueAsString)) {
                    StaffDTO staffDTO = new StaffDTO(firstName, lastName, privateEmail, "Missing field(s) : "+ StringUtils.join(missingMandatoryFields, ", "));
                    if (isNotNull(cprAsLong)) {
                        staffDTO.setCprNumber(BigInteger.valueOf(cprAsLong));
                    }
                    staffErrorList.add(staffDTO);
                } else {
                    Long externalId = (StringUtils.isBlank(externalIdValueAsString)) ? 0 : Long.parseLong(externalIdValueAsString);
                    if (alreadyAddedStaffIds.contains(externalId)) {
                        StaffDTO staffDTO = new StaffDTO(firstName, lastName, privateEmail, "Duplicate External Id");
                        staffDTO.setCprNumber(BigInteger.valueOf(cprAsLong));
                        staffErrorList.add(staffDTO);
                        continue;
                    }
                    // Check if Staff exists in organization with CPR Number
                    if (staffGraphRepository.isStaffExistsByCPRNumber(cprAsLong.toString(), Optional.ofNullable(parent).isPresent() ? parent.getId() : unitId)) {
                        StaffDTO staffDTO = new StaffDTO(firstName, lastName, privateEmail, "Staff already exist with CPR Number "+cprAsLong);
                        staffDTO.setCprNumber(BigInteger.valueOf(cprAsLong));
                        staffErrorList.add(staffDTO);
                        continue;
                    }

                    Staff staff = new Staff();
                    boolean isEmploymentExist = (staff.getId()) != null;
                    staff.setExternalId(externalId);
                    staff.setUserName(privateEmail);
                    staff.setFirstName(firstName);
                    staff.setLastName(lastName);
                    staff.setFamilyName(lastName);
                    if (row.getCell(17) != null) {
                        staff.setBadgeNumber(getStringValueOfIndexedCell(row, 17));
                    }
                    ContactAddress contactAddress = extractContactAddressFromRow(row);
                    if (!Optional.ofNullable(contactAddress).isPresent()) {
                        contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(unit);
                    }
                    ContactDetail contactDetail = extractContactDetailFromRow(row);
                    staff.setContactDetail(contactDetail);
                    staff.setContactAddress(contactAddress);
                    User user = null;
                    if (isCollectionEmpty(missingMandatoryFields)) {
                        user = userGraphRepository.findUserByCprNumberOrEmail(cprAsLong.toString(), "(?)" + privateEmail);
                        if (!Optional.ofNullable(user).isPresent()) {
                            user = new User();
                            // set User's default language
                            user.setUserLanguage(defaultSystemLanguage);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setCprNumber(cprAsLong.toString().trim());
                            user.setGender(CPRUtil.getGenderFromCPRNumber(user.getCprNumber()));
                            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(user.getCprNumber()));
                            if (Optional.ofNullable(contactDetail).isPresent() && Optional.ofNullable(contactDetail.getPrivateEmail()).isPresent()) {
                                user.setUserName(contactDetail.getPrivateEmail().toLowerCase());
                                user.setEmail(contactDetail.getPrivateEmail().toLowerCase());
                            } else {
                                user.setEmail(user.getFirstName().trim() + KAIROS_EMAIL);
                            }
                            String defaultPassword = user.getFirstName().replaceAll("\\s+","") + DEFAULT_PASSPHRASE_ENDS_WITH;
                            user.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
                            user.setAccessToken(defaultPassword);
                        }
                        staff.setUser(user);
                    }
                    staffGraphRepository.save(staff);
                    StaffDTO staffDTO = ObjectMapperUtils.copyPropertiesByMapper(staff, StaffDTO.class);
                    staffDTO.setGender(user.getGender());
                    staffDTO.setAge(Period.between(CPRUtil.getDateOfBirthFromCPR(user.getCprNumber()), LocalDate.now()).getYears());
                    staffList.add(staffDTO);
                    if (!staffGraphRepository.staffAlreadyInUnit(externalId, unit.getId())) {
                        createEmployment(parent, unit, staff, accessGroupId, DateUtils.getCurrentDateMillis(), isEmploymentExist);
                    }
                }
            }
            if (isCollectionNotEmpty(staffList)) {
                activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(staffList.stream().map(staff -> staff.getId()).collect(Collectors.toList())), unitId);
            }
            return staffUploadBySheetQueryResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staffUploadBySheetQueryResult;
    }

    /***************************************************************************************************/

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
                String houseNumber;
                StringBuilder fullStreetName = new StringBuilder();
                if (arr.length != 0) {
                    String street = arr[0];
                    String newArray[] = street.split(" ");
                    houseNumber = newArray[newArray.length - 1];
                    for (int i = 0; i < newArray.length - 1; i++) {
                        if (i == 0) {
                            fullStreetName.append(newArray[i]);
                        } else {
                            fullStreetName.append(" ").append(newArray[i]);
                        }
                    }
                    contactAddress.setHouseNumber(houseNumber);
                    contactAddress.setStreet(fullStreetName.toString());
                    contactAddress.setCity(row.getCell(25).toString());
                }
                return contactAddress;
            }
        }
        return null;
    }

    // function to convert cell value as String for given cellIndex

    /*******************************************************************************************************/

    private String getStringValueOfIndexedCell(Row row, int cellIndex) {
        Cell cellValue = row.getCell(cellIndex);
        cellValue.setCellType(Cell.CELL_TYPE_STRING);
        return cellValue.getStringCellValue().trim();
    }

    /*******************************************************************************************************/

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
                contactDetail.setPrivateEmail(email.toLowerCase().trim());
            }
        }
        return contactDetail;
    }

    private Staff createStaff(Staff staff, Long unitId) {

        if (checkStaffEmailConstraint(staff)) {

            LOGGER.info("Creating Staff.......... " + staff.getFirstName() + " " + staff.getLastName());
            LOGGER.info("Creating User for Staff");
            SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(unitId);
            User user = new User();
            user.setEmail(staff.getEmail());
            user.setUserLanguage(systemLanguage);
            staff.setUser(userGraphRepository.save(user));
            staffGraphRepository.save(staff);
            return staff;
        }
        LOGGER.info("Not Creating Staff.......... " + staff.getFirstName() + " " + staff.getLastName());
        return null;
    }

    private boolean checkStaffEmailConstraint(Staff staff) {
        LOGGER.info("Checking Email constraint");
        if (staff.getEmail() != null && userGraphRepository.findByEmail(staff.getEmail()) != null) {

            LOGGER.info("Email matched !");
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

    public boolean deleteStaffById(Long staffId, Long positionId) {
        staffGraphRepository.deleteStaffEmployment(staffId, positionId);
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
            Position position = new Position("working as country admin", adminAsStaff);
            organization.getPositions().add(position);
            organizationGraphRepository.save(organization);

            AccessGroup accessGroup = accessGroupRepository.findAccessGroupByName(organization.getId(), AppConstants.COUNTRY_ADMIN);
            UnitPermission unitPermission = new UnitPermission();
            unitPermission.setOrganization(organization);
            AccessPermission accessPermission = new AccessPermission(accessGroup);
            UnitPermissionAccessPermissionRelationship unitPermissionAccessPermissionRelationship = new UnitPermissionAccessPermissionRelationship(unitPermission, accessPermission);
            unitPermissionAccessPermissionRelationship.setEnabled(true);
            unitPermissionAndAccessPermissionGraphRepository.save(unitPermissionAccessPermissionRelationship);
            accessPageService.setPagePermissionToAdmin(accessPermission);
            position.getUnitPermissions().add(unitPermission);
            organization.getPositions().add(position);
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
        staff.setFamilyName(data.getFamilyName());
        staff.setCurrentStatus(data.getCurrentStatus());
        staff = createStaff(staff, unitId);
        if (staff != null) {
            if (data.getTeamId() != null) {
                //TODO hardcoded unit id to removes
                boolean result = teamService.addStaffInTeam(staff.getId(), data.getTeamId(), false, unitId);
                LOGGER.info("Assigning team to staff: " + result);
            }
            if (data.getSkills() != null) {
                List<Map<String, Object>> result = skillService.assignSkillToStaff(staff.getId(), data.getSkills(), false, unitId);
                LOGGER.info("Assigned Number of Skills to staff: " + result.size());
            }
            taskServiceRestClient.updateTaskForStaff(staff.getId(), data.getAnonymousStaffId());
            return staff;
        }
        return null;
    }


    public StaffDTO createStaffFromWeb(Long unitId, StaffCreationDTO payload) {
        if (payload.getCprNumber().length() != 10) {
            exceptionService.invalidSize("message.cprNumber.size");
        }
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        Organization parent = null;
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        if (staffGraphRepository.findStaffByEmailInOrganization(payload.getPrivateEmail(), unitId) != null) {
            exceptionService.duplicateDataException("message.email.alreadyExist", "Staff", payload.getPrivateEmail());
        }
        // Check if Staff exists in organization with CPR Number
        if (staffGraphRepository.isStaffExistsByCPRNumber(payload.getCprNumber(), Optional.ofNullable(parent).isPresent() ? parent.getId() : unitId)) {
            exceptionService.invalidRequestException("error.staff.exists.same.cprNumber", payload.getCprNumber());
        }
        User user = userGraphRepository.findUserByCprNumber(payload.getCprNumber());

        if (!Optional.ofNullable(user).isPresent()) {
            user = Optional.ofNullable(userGraphRepository.findByEmail(payload.getPrivateEmail().trim())).orElse(new User());
        }

        Staff staff = staffGraphRepository.findByExternalId(payload.getExternalId());
        if (Optional.ofNullable(staff).isPresent()) {
            exceptionService.duplicateDataException("message.staff.externalid.alreadyexist");

        }
        setBasicDetailsOfUser(user, payload);
        // Set default language of User
        Long countryId = organizationGraphRepository.getCountryId(Optional.ofNullable(parent).isPresent() ? parent.getId() : unitId);
        SystemLanguage systemLanguage = systemLanguageGraphRepository.getSystemLanguageOfCountry(countryId);
        user.setUserLanguage(systemLanguage);
        staff = createStaffObject(parent, unit, payload);
        boolean isEmploymentExist = (staff.getId()) != null;
        staff.setUser(user);
        addStaffInChatServer(staff);
        staffGraphRepository.save(staff);
        createEmployment(parent, unit, staff, payload.getAccessGroupId(), DateUtils.getCurrentDateMillis(), isEmploymentExist);
        activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(staff.getId())), unitId);
        return new StaffDTO(staff.getId(), staff.getFirstName(), staff.getLastName(), user.getGender(), user.getAge());
    }

    public User createUnitManagerForNewOrganization(Organization organization, StaffCreationDTO staffCreationData) {
        User user = userGraphRepository.findByEmail(staffCreationData.getPrivateEmail().trim());
        if (!Optional.ofNullable(user).isPresent()) {
            SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(organization.getId());
            user = new User();
            user.setUserLanguage(systemLanguage);
            setBasicDetailsOfUser(user, staffCreationData);
            userGraphRepository.save(user);
        }
        setUnitManagerAndEmployment(organization, user, staffCreationData.getAccessGroupId());
        return user;
    }

    private void setBasicDetailsOfUser(User user, StaffCreationDTO staffCreationDTO) {
        user.setEmail(staffCreationDTO.getPrivateEmail());
        user.setUserName(staffCreationDTO.getPrivateEmail());
        user.setFirstName(staffCreationDTO.getFirstName());
        user.setLastName(staffCreationDTO.getLastName());
        user.setPassword(Optional.ofNullable(user.getFirstName()).isPresent() ? new BCryptPasswordEncoder().encode(user.getFirstName().replaceAll("\\s+","") + DEFAULT_PASSPHRASE_ENDS_WITH) : null);
        user.setCprNumber(staffCreationDTO.getCprNumber());
        if (!StringUtils.isBlank(staffCreationDTO.getCprNumber())) {
            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(staffCreationDTO.getCprNumber()));
            user.setGender(CPRUtil.getGenderFromCPRNumber(staffCreationDTO.getCprNumber()));
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
            LOGGER.info("Creating new staff with kmd external id " + payload.getExternalId() + " in unit " + unit.getId());
            staff = new Staff();
        }
        staff.setEmail(payload.getPrivateEmail());
        staff.setInactiveFrom(payload.getInactiveFrom());
        staff.setExternalId(payload.getExternalId());
        staff.setFirstName(payload.getFirstName());
        staff.setLastName(payload.getLastName());
        staff.setFamilyName(payload.getFamilyName());
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

    private void createEmployment(Organization organization,
                                  Organization unit, Staff staff, Long accessGroupId, Long employedSince, boolean employmentAlreadyExist) {
        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.staff.accessgroup.notfound", accessGroupId);

        }
        if (accessGroup.getEndDate() != null && accessGroup.getEndDate().isBefore(DateUtils.getCurrentLocalDate())) {
            exceptionService.actionNotPermittedException("error.access.expired", accessGroup.getName());
        }
        Position position;
        if (employmentAlreadyExist) {
            position = (Optional.ofNullable(organization).isPresent()) ?
                    positionGraphRepository.findPosition(organization.getId(), staff.getId()) :
                    positionGraphRepository.findPosition(unit.getId(), staff.getId());
        } else {
            position = new Position();
        }
        position.setName("Working as staff");
        position.setStaff(staff);
        position.setStartDateMillis(employedSince);

        UnitPermission unitPermission = new UnitPermission();
        unitPermission.setOrganization(unit);
        unitPermission.setAccessGroup(accessGroup);
        position.getUnitPermissions().add(unitPermission);
        positionGraphRepository.save(position);

        if (Optional.ofNullable(organization).isPresent()) {
            if (Optional.ofNullable(organization.getPositions()).isPresent()) {
                organization.getPositions().add(position);
                organizationGraphRepository.save(organization);
            } else {
                List<Position> positions = new ArrayList<>();
                positions.add(position);
                organization.setPositions(positions);
                organizationGraphRepository.save(organization);
            }
        } else {
            if (Optional.ofNullable(unit.getPositions()).isPresent()) {
                unit.getPositions().add(position);
                organizationGraphRepository.save(unit);
            } else {
                List<Position> positions = new ArrayList<>();
                positions.add(position);
                unit.setPositions(positions);
                organizationGraphRepository.save(unit);
            }

        }
    }

    public void setAccessGroupInUserAccount(User user, Long organizationId, Long accessGroupId, boolean union) {
        UnitPermission unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfUser(organizationId, user.getId());

        unitPermission = unitPermission == null ? new UnitPermission() : unitPermission;
        AccessGroup accessGroup = union ? accessGroupRepository.findOne(accessGroupId) : accessGroupRepository.getAccessGroupByParentId(organizationId, accessGroupId);
        if (Optional.ofNullable(accessGroup).isPresent()) {
            unitPermission.setAccessGroup(accessGroup);
            linkAccessOfModules(accessGroup, unitPermission);
        }
        unitPermissionGraphRepository.save(unitPermission);
    }

    private void linkAccessOfModules(AccessGroup accessGroup, UnitPermission unitPermission) {
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        UnitPermissionAccessPermissionRelationship unitPermissionAccessPermissionRelationship = new UnitPermissionAccessPermissionRelationship(unitPermission, accessPermission);
        unitPermissionAccessPermissionRelationship.setEnabled(true);
        unitPermissionAndAccessPermissionGraphRepository.save(unitPermissionAccessPermissionRelationship);
        accessPageRepository.setDefaultPermission(accessPermission.getId(), accessGroup.getId());
    }

    public void setUserAndEmployment(Organization organization, User user, Long accessGroupId, boolean parentOrganization, boolean union) {
        Position position;
        // if the organization is not parent organization then adding position in parent organization.
        if (!parentOrganization) {
            Organization mainOrganization = organizationGraphRepository.getParentOfOrganization(organization.getId());
            position = positionGraphRepository.findPositionByOrganizationIdAndUserId(mainOrganization.getId(),user.getId());
            mainOrganization.getPositions().add(position);
            organizationGraphRepository.save(mainOrganization);
        } else {
                Staff staff = new Staff(user.getEmail(), user.getEmail(), user.getFirstName(), user.getLastName(),
                        user.getFirstName(), StaffStatusEnum.ACTIVE, null, user.getCprNumber());
                position=new Position();
                position.setStaff(staff);
                staff.setUser(user);
                position.setName(UNIT_MANAGER_EMPLOYMENT_DESCRIPTION);
                position.setStaff(staff);
                staff.setContactAddress(staffAddressService.getStaffContactAddressByOrganizationAddress(organization));
                position.setStartDateMillis(DateUtils.getCurrentDayStartMillis());
                  organization.getPositions().add(position);

        }
        organizationGraphRepository.save(organization);
        UnitPermission unitPermission = new UnitPermission();
        unitPermission.setOrganization(organization);
        if (accessGroupId != null) {
            AccessGroup accessGroup = (union || parentOrganization) ? accessGroupRepository.getAccessGroupByParentAccessGroupId(organization.getId(), accessGroupId) : accessGroupRepository.getAccessGroupByParentId(organization.getId(), accessGroupId);
            if (Optional.ofNullable(accessGroup).isPresent()) {
                unitPermission.setAccessGroup(accessGroup);
                linkAccessOfModules(accessGroup, unitPermission);
            }
        }
        position.getUnitPermissions().add(unitPermission);
        positionGraphRepository.save(position);
    }

    private void setUnitManagerAndEmployment(Organization organization, User user, Long accessGroupId) {
        Staff staff = new Staff(user.getEmail(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getFirstName(), StaffStatusEnum.ACTIVE, null, user.getCprNumber());
        Position position = new Position();
        position.setStaff(staff);
        staff.setUser(user);
        position.setName(UNIT_MANAGER_EMPLOYMENT_DESCRIPTION);
        position.setStaff(staff);
        position.setStartDateMillis(DateUtils.getCurrentDateMillis());
        organization.getPositions().add(position);
        organizationGraphRepository.save(organization);
        if (accessGroupId != null) {
            UnitPermission unitPermission = new UnitPermission();
            unitPermission.setOrganization(organization);
            AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
            if (Optional.ofNullable(accessGroup).isPresent()) {
                unitPermission.setAccessGroup(accessGroup);
            }
            position.getUnitPermissions().add(unitPermission);
        }
        staff.setContactAddress(staffAddressService.getStaffContactAddressByOrganizationAddress(organization));
        positionGraphRepository.save(position);
        activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(position.getStaff().getId())), organization.getId());

    }

    public Staff createStaffObject(User user, Staff staff, Long engineerTypeId, Organization unit) {
        ContactAddress contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(unit);

        if (contactAddress != null) {
            staff.setContactAddress(contactAddress);
        }
        if (engineerTypeId != null) {
            staff.setEngineerType(engineerTypeGraphRepository.findOne(engineerTypeId));
        }
        staff.setUser(user);
        staff.setOrganizationId(unit.getId());
        staff = staffGraphRepository.save(staff);
        Organization parent = null;
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        if (parent == null) {
            if (positionGraphRepository.findPosition(unit.getId(), staff.getId()) == null) {
                positionGraphRepository.createPositions(unit.getId(), Collections.singletonList(staff.getId()), unit.getId());
            }
        } else {
            if (positionGraphRepository.findPosition(parent.getId(), staff.getId()) == null) {
                positionGraphRepository.createPositions(parent.getId(), Collections.singletonList(staff.getId()), unit.getId());
            }
        }
        return staff;
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
                exceptionService.internalServerError("error.xssfsheet.noMoreRow", 2);

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
        LOGGER.info("total staff updated  " + staffUpdated);
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
            final String password = unitManagerDTO.getFirstName().replaceAll("\\s+","") + DEFAULT_PASSPHRASE_ENDS_WITH;
        ObjectMapper mapper = new ObjectMapper();
        Map unitManagerDTOMap = mapper.convertValue(unitManagerDTO, Map.class);
        if (user == null) {
            LOGGER.info("Unit manager is null..creating new user first");
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
            positionService.createPositionForUnitManager(staff, parent, unit, unitManagerDTO.getAccessGroupId());
            sendEmailToUnitManager(unitManagerDTO, password);
            return unitManagerDTOMap;
        } else {
            long organizationId = (parent == null) ? unitId : parent.getId();
            if (staffGraphRepository.countOfUnitEmployment(organizationId, unitId, user.getEmail()) == 0) {
                Staff staff = createStaff(user);
                unitManagerDTOMap.put("id", staff.getId());
                positionService.createPositionForUnitManager(staff, parent, unit, unitManagerDTO.getAccessGroupId());
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
        mailService.sendPlainMailWithSendGrid(unitManagerDTO.getEmail(), body, subject);
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
        List<Long> citizenIds = tasks.stream().map(StaffAssignedTasksWrapper::getId).collect(Collectors.toList());
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

    /**
     * @param staffId
     * @return
     * @Desc We are checking null in another ms
     */
    public Staff getStaffById(long staffId) {
        return staffGraphRepository.findOne(staffId, 0);
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

    public List<StaffPersonalDetailDTO> getAllStaffByUnitId(Long unitId, Boolean allStaffRequired) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound", unitId);

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
            exceptionService.dataNotFoundByIdException("message.staffandunit.id.notfound", staffId, unitId);

        }
        return staffPersonalDetailList;
    }

    public StaffUnitPositionDetails getUnitPositionOfStaff(long staffId, long unitId) {
        UnitPositionQueryResult unitPosition = unitPositionGraphRepository.getUnitPositionOfStaff(staffId, unitId);
        StaffUnitPositionDetails unitPositionDetails = null;
        if (Optional.ofNullable(unitPosition).isPresent()) {
            unitPositionDetails = convertUnitPositionObject(unitPosition);
            unitPositionDetails.setUnitId(unitId);
            List<UnitPositionLinesQueryResult> data = unitPositionGraphRepository.findFunctionalHourlyCost(Collections.singletonList(unitPosition.getId()));
            unitPositionDetails.setHourlyCost(CollectionUtils.isNotEmpty(data) ? data.get(0).getHourlyCost() : new BigDecimal(0));
        }
        return unitPositionDetails;
    }

    public StaffFilterDTO addStaffFavouriteFilters(StaffFilterDTO staffFilterDTO, long unitId) {
        StaffFavouriteFilter staffFavouriteFilter = new StaffFavouriteFilter();
        Long userId = UserContext.getUserDetails().getId();
        Organization parent = organizationService.fetchParentOrganization(unitId);
        Staff staff = staffGraphRepository.getStaffByUserId(userId, parent.getId());
        AccessPage accessPage = accessPageService.findByModuleId(staffFilterDTO.getModuleId());
        staffFavouriteFilter.setName(staffFilterDTO.getName());
        staffFavouriteFilterGraphRepository.save(staffFavouriteFilter);
        staff.addFavouriteFilters(staffFavouriteFilter);
        staffGraphRepository.save(staff);
        staffFilterDTO.setModuleId(accessPage.getModuleId());
        staffFilterDTO.setName(staffFavouriteFilter.getName());
        staffFilterDTO.setId(staffFavouriteFilter.getId());
        return staffFilterDTO;
    }

    public StaffFilterDTO updateStaffFavouriteFilters(StaffFilterDTO staffFilterDTO, long unitId) {
        Long userId = UserContext.getUserDetails().getId();
        Organization parent = organizationService.fetchParentOrganization(unitId);
        Staff staff = staffGraphRepository.getStaffByUserId(userId, parent.getId());
        StaffFavouriteFilter staffFavouriteFilter = staffGraphRepository.getStaffFavouriteFiltersById(staff.getId(), staffFilterDTO.getId());
        if (!Optional.ofNullable(staffFavouriteFilter).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.stafffavouritefilter.notfound", staffFilterDTO.getId());
        }
        staffFavouriteFilter.setName(staffFilterDTO.getName());
        staffFavouriteFilterGraphRepository.save(staffFavouriteFilter);
        return staffFilterDTO;
    }

    public boolean removeStaffFavouriteFilters(Long staffFavouriteFilterId, long unitId) {
        Long userId = UserContext.getUserDetails().getId();
        Organization parent = organizationService.fetchParentOrganization(unitId);
        Staff staff = staffGraphRepository.getStaffByUserId(userId, parent.getId());
        StaffFavouriteFilter staffFavouriteFilter = staffGraphRepository.getStaffFavouriteFiltersById(staff.getId(), staffFavouriteFilterId);
        if (!Optional.ofNullable(staffFavouriteFilter).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.stafffavouritefilter.notfound", staffFavouriteFilterId);

        }
        staffFavouriteFilterGraphRepository.save(staffFavouriteFilter);
        return true;
    }

    public List<FavoriteFilterQueryResult> getStaffFavouriteFilters(String moduleId, long unitId) {
        Long userId = UserContext.getUserDetails().getId();
        Organization parent = organizationService.fetchParentOrganization(unitId);
        Staff staff = staffGraphRepository.getStaffByUserId(userId, parent.getId());
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
        SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(organization.getId());

        for (TimeCareStaffDTO timeCareStaffDTO : timeCareStaffByWorkPlace) {

            String email = (timeCareStaffDTO.getEmail() == null) ? timeCareStaffDTO.getFirstName() + KAIROS_EMAIL : timeCareStaffDTO.getEmail();
            User user = Optional.ofNullable(userGraphRepository.findByEmail(email.trim())).orElse(new User());
            user.setUserLanguage(systemLanguage);
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
            contactAddress.setStreet(timeCareStaffDTO.getAddress());
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

    public List<String> getEmailsOfStaffByStaffIds(List<Long> staffIds) {
        return staffGraphRepository.getEmailsOfStaffByStaffIds(staffIds);
    }


    public boolean registerAllStaffsToChatServer() {
        List<Staff> staffList = staffGraphRepository.findAll();
        staffList.forEach(staff -> {
            if (isNull(staff.getAccess_token())) {
                addStaffInChatServer(staff);
            }
        });
        staffGraphRepository.saveAll(staffList);
        return true;
    }

    public void addStaffInChatServer(Staff staff) {
        try {
            Map<String, String> auth = new HashMap<>();
            auth.put("type", "m.login.dummy");
            auth.put("session", staff.getEmail());
            StaffChatDetails staffChatDetails = new StaffChatDetails(auth, staff.getEmail(), staff.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH);
            StaffChatDetails chatDetails = chatRestClient.registerUser(staffChatDetails);
            staff.setAccess_token(chatDetails.getAccess_token());
            staff.setUser_id(chatDetails.getUser_id());
        }
        catch (Exception ex){
            LOGGER.error(ex.getMessage());
        }
    }

    private void setStaffDetails(Staff staffToUpdate, StaffPersonalDetail staffPersonalDetail) throws ParseException {
        staffToUpdate.setFirstName(staffPersonalDetail.getFirstName());
        staffToUpdate.setLastName(staffPersonalDetail.getLastName());
        staffToUpdate.setFamilyName(staffPersonalDetail.getFamilyName());
        staffToUpdate.setCurrentStatus(staffPersonalDetail.getCurrentStatus());
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
        staffToUpdate.setSignature(staffPersonalDetail.getSignature());
        staffToUpdate.setContactDetail(staffPersonalDetail.getContactDetail());
        staffPersonalDetail.setExpertiseIds(staffPersonalDetail.getExpertiseWithExperience().stream().map(StaffExperienceInExpertiseDTO::getExpertiseId).collect(Collectors.toList()));

        if (staffPersonalDetail.getCurrentStatus() == StaffStatusEnum.INACTIVE) {
            staffToUpdate.setInactiveFrom(DateConverter.parseDate(staffPersonalDetail.getInactiveFrom()).getTime());
        }
    }


    /**
     * @param unitId
     * @return - login user staff id and list of staff present in unit
     */
    public StaffEmploymentTypeWrapper getStaffListAndLoginUserStaffIdByUnitId(Long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound", unitId);
        }
        Organization organization = unit.isParentOrganization() ? unit : organizationService.fetchParentOrganization(unitId);
        Long loggedInStaffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId());
        StaffEmploymentTypeWrapper staffEmploymentTypeWrapper = new StaffEmploymentTypeWrapper();
        staffEmploymentTypeWrapper.setLoggedInStaffId(loggedInStaffId);
        staffEmploymentTypeWrapper.setStaffList(staffGraphRepository.findAllStaffBasicDetailsByOrgIdAndUnitId(organization.getId(),unitId));
        return staffEmploymentTypeWrapper;
    }


}

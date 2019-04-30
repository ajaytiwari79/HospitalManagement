package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.service.mail.MailService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.task.StaffAssignedTasksWrapper;
import com.kairos.dto.activity.task.StaffTaskDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.staff.client.ClientStaffInfoDTO;
import com.kairos.dto.user.staff.staff.StaffChatDetails;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.dto.user.user.password.PasswordUpdateByAdminDTO;
import com.kairos.dto.user.user.password.PasswordUpdateDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.UnitManagerDTO;
import com.kairos.persistence.model.staff.*;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.permission.UnitPermissionAccessPermissionRelationship;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.filter.FavoriteFilterQueryResult;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
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
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.parseDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.CommonConstants.DEFAULT_EMAIL_TEMPLATE;
import static com.kairos.service.employment.EmploymentUtility.convertEmploymentObject;
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
    private EmploymentGraphRepository employmentGraphRepository;
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
    @Inject
    private StaffCreationService staffCreationService;
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

    public boolean updatePasswordByManagement(Long staffId, PasswordUpdateByAdminDTO passwordUpdateDTO) {
        Staff staff = staffGraphRepository.findByStaffId(staffId);
        if (staff != null) {
            User userForStaff = staff.getUser();
            CharSequence newPassword = CharBuffer.wrap(passwordUpdateDTO.getNewPassword());
            userForStaff.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userGraphRepository.save(userForStaff);
        } else {
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
        if(!CollectionUtils.isEqualCollection(expertises, oldExpertise.stream().map(expertise -> expertise.getId()).collect(Collectors.toList())) && !userAccessRoleDTO.getManagement()) {
            exceptionService.actionNotPermittedException("message.employment.expertise.notchanged");
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
                if (staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths() >= seniorityLevel.getFrom() * 12 && (seniorityLevel.getTo() == null || staffPersonalDetail.getExpertiseWithExperience().get(i).getRelevantExperienceInMonths() < seniorityLevel.getTo() * 12)) {
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

        if (userAccessRoleDTO.getManagement() || staffToUpdate.getUser().getId().equals(UserContext.getUserDetails().getId())) {
            if (!staffToUpdate.getUser().getUserName().equalsIgnoreCase(staffPersonalDetail.getUserName()) && !staffToUpdate.getUser().isUserNameUpdated()) {
                User user = userGraphRepository.findUserByUserName("(?i)" + staffPersonalDetail.getUserName());
                if (!Optional.ofNullable(user).isPresent()) {
                    staffToUpdate.getUser().setUserName(staffPersonalDetail.getUserName());
                    staffToUpdate.getUser().setUserNameUpdated(true);
                    staffPersonalDetail.setUserNameUpdated(true);
                } else {
                    exceptionService.duplicateDataException("message.user.userName.already.use");
                }
            }
        }

        //saving addresses of staff
        staffAddressService.saveAddress(staffToUpdate, Arrays.asList(staffPersonalDetail.getPrimaryAddress(), staffPersonalDetail.getSecondaryAddress()));
        Staff staff = staffGraphRepository.save(staffToUpdate);
        staffPersonalDetail.setUserName(staff.getUser().getUserName());
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
        user.setUserName(staffPersonalDetail.getUserName());
        userGraphRepository.save(user);
        staffPersonalDetail.setPregnant(user.isPregnant());
        List<SectorAndStaffExpertiseQueryResult> staffExpertiseQueryResults = ObjectMapperUtils.copyPropertiesOfListByMapper(staffExpertiseRelationShipGraphRepository.getSectorWiseExpertiseWithExperience(staffId), SectorAndStaffExpertiseQueryResult.class);
        staffPersonalDetail.setSectorWiseExpertise(staffRetrievalService.getSectorWiseStaffAndExpertise(staffExpertiseQueryResults));
        if (isCollectionEmpty(staffPersonalDetail.getTeamIdsOfStaff())) {
            teamGraphRepository.removeStaffFromAllTeams(staffId);
        } else {
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
    private List<String> validateStaffData(Row row, int[] mandatoryCellColumnIndexs, XSSFSheet sheet) {
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
                String userName = "";
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

                if (String.valueOf(row.getCell(19)) == null || String.valueOf(row.getCell(19)).isEmpty()) {
                    userName = createNewUserName(firstName, lastName);

                } else {
                    User user = userGraphRepository.findUserByUserName(getStringValueOfIndexedCell(row, 19));
                    if (Optional.ofNullable(user).isPresent()) {
                        StaffDTO staffDTO = new StaffDTO(firstName, lastName, privateEmail, "UserName already exist");
                        staffDTO.setCprNumber(BigInteger.valueOf(cprAsLong));
                        staffErrorList.add(staffDTO);
                    } else {
                        userName = getStringValueOfIndexedCell(row, 19);
                    }
                }
                externalIdValueAsString = getStringValueOfIndexedCell(row, 2);
                if (isCollectionNotEmpty(missingMandatoryFields) || cprAsLong == null || StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName) || StringUtils.isBlank(privateEmail) || StringUtils.isBlank(externalIdValueAsString)) {
                    StaffDTO staffDTO = new StaffDTO(firstName, lastName, privateEmail, "Missing field(s) : " + StringUtils.join(missingMandatoryFields, ", "));
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
                        StaffDTO staffDTO = new StaffDTO(firstName, lastName, privateEmail, "Staff already exist with CPR Number " + cprAsLong);
                        staffDTO.setCprNumber(BigInteger.valueOf(cprAsLong));
                        staffErrorList.add(staffDTO);
                        continue;
                    }
                    Staff staff = new Staff();
                    boolean isEmploymentExist = (staff.getId()) != null;
                    staff.setExternalId(externalId);
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
                        user = userGraphRepository.findByEmail("(?i)" + privateEmail);
                        if (!Optional.ofNullable(user).isPresent()) {
                            user = new User();
                            // set User's default language
                            user.setUserLanguage(defaultSystemLanguage);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setCprNumber(cprAsLong.toString().trim());
                            user.setGender(CPRUtil.getGenderFromCPRNumber(user.getCprNumber()));
                            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(user.getCprNumber()));
                            user.setUserName(userName);
                            boolean userNameUpdated = !(String.valueOf(row.getCell(19)) == null || String.valueOf(row.getCell(19)).trim().isEmpty());
                            user.setUserNameUpdated(userNameUpdated);
                            if (Optional.ofNullable(contactDetail).isPresent() && Optional.ofNullable(contactDetail.getPrivateEmail()).isPresent()) {
                                //user.setUserName(contactDetail.getPrivateEmail().toLowerCase());
                                user.setEmail(contactDetail.getPrivateEmail().toLowerCase());
                            } else {
                                user.setEmail(user.getFirstName().trim() + KAIROS_EMAIL);
                            }
                            String defaultPassword = user.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH;
                            user.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
                            user.setAccessToken(defaultPassword);
                        }
                        user.setUserName(userName);
                        staff.setUser(user);
                    }
                    staffGraphRepository.save(staff);
                    StaffDTO staffDTO = ObjectMapperUtils.copyPropertiesByMapper(staff, StaffDTO.class);
                    staffDTO.setGender(user.getGender());
                    staffDTO.setAge(Period.between(CPRUtil.getDateOfBirthFromCPR(user.getCprNumber()), LocalDate.now()).getYears());
                    staffList.add(staffDTO);
                    if (!staffGraphRepository.staffAlreadyInUnit(externalId, unit.getId())) {
                        staffCreationService.createEmployment(parent, unit, staff, accessGroupId, DateUtils.getCurrentDateMillis(), isEmploymentExist);
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

    private String getStringValueOfIndexedCell(Row row, int cellIndex) {
        Cell cellValue = row.getCell(cellIndex);
        cellValue.setCellType(Cell.CELL_TYPE_STRING);
        return cellValue.getStringCellValue().trim();
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
                contactDetail.setPrivateEmail(email.toLowerCase().trim());
            }
        }
        return contactDetail;
    }

    public boolean checkStaffEmailConstraint(Staff staff) {
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

    public void linkAccessOfModules(AccessGroup accessGroup, UnitPermission unitPermission) {
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        UnitPermissionAccessPermissionRelationship unitPermissionAccessPermissionRelationship = new UnitPermissionAccessPermissionRelationship(unitPermission, accessPermission);
        unitPermissionAccessPermissionRelationship.setEnabled(true);
        unitPermissionAndAccessPermissionGraphRepository.save(unitPermissionAccessPermissionRelationship);
        accessPageRepository.setDefaultPermission(accessPermission.getId(), accessGroup.getId());
    }

    public void setUserAndPosition(Organization organization, User user, Long accessGroupId, boolean parentOrganization, boolean union) {
        Position position = positionGraphRepository.findPositionByOrganizationIdAndUserId(organization.getId(), user.getId());
        if (isNull(position)) {

            Staff staff = new Staff(user.getEmail(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getFirstName(), StaffStatusEnum.ACTIVE, null, user.getCprNumber());

            position = new Position();
            position.setStaff(staff);
            staff.setUser(user);
            position.setName(UNIT_MANAGER_EMPLOYMENT_DESCRIPTION);
            position.setStaff(staff);
            staff.setContactAddress(staffAddressService.getStaffContactAddressByOrganizationAddress(organization));
            position.setStartDateMillis(DateUtils.getCurrentDayStartMillis());
        }
        // if the organization is not parent organization then adding position in parent organization.
        if (!parentOrganization) {
            Organization mainOrganization = organizationGraphRepository.getParentOfOrganization(organization.getId());
            mainOrganization.getPositions().add(position);
            organizationGraphRepository.save(mainOrganization);
        } else {
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

    public void setUnitManagerAndPosition(Organization organization, User user, Long accessGroupId) {
        Staff staff = new Staff(user.getEmail(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getFirstName(), StaffStatusEnum.ACTIVE, null, user.getCprNumber());
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

    public Map<String, Object> getUnitManager(long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        List<Map<String, Object>> unitManagers;
        if (parent == null) unitManagers = staffGraphRepository.getUnitManagers(unitId, unitId);
        else unitManagers = staffGraphRepository.getUnitManagers(parent.getId(), unitId);
        List<Map<String, Object>> unitManagerList = new ArrayList<>();
        for (Map<String, Object> unitManager : unitManagers) {
            unitManagerList.add((Map<String, Object>) unitManager.get("data"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("unitManager", unitManagerList);
        map.put("accessGroups", accessGroupRepository.getAccessGroups(unitId));
        return map;
    }

    public void sendEmailToUnitManager(UnitManagerDTO unitManagerDTO, String password) {
        String body = "Hi,\n\n" + "You are assigned as an unit manager and to get access in KairosPlanning.\n" + "Your username " + unitManagerDTO.getEmail() + " and password is " + password + "\n\n Thanks";
        //TODO SUBJECT AND MAIL BODY SHOULD IN A SINGLE FILE
        String subject = "You are a unit manager at KairosPlanning";
        Map<String, Object> templateParam = new HashMap<>();
        templateParam.put("receiverName", unitManagerDTO.getFullName());
        templateParam.put("description", body);
        //TODO This API doesn't have staff image
        //templateParam.put("imageResourceName",envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath()+staffDTO.getProfilePic());
        mailService.sendMailWithSendGrid(DEFAULT_EMAIL_TEMPLATE, templateParam, null, subject, unitManagerDTO.getEmail());
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

    public ClientStaffInfoDTO getStaffInfo(String loggedInUserName) {
        Staff staff = staffGraphRepository.getByUser(userGraphRepository.findByUserNameIgnoreCase(loggedInUserName).getId());
        if (staff == null) {
            exceptionService.dataNotFoundByIdException("message.staff.id.notFound");

        }
        return new ClientStaffInfoDTO(staff.getId());
    }

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
        if (parent == null) unitManagers = staffGraphRepository.getUnitManagersIds(unitId, unitId);
        else unitManagers = staffGraphRepository.getUnitManagersIds(parent.getId(), unitId);
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
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffHavingEmploymentByUnitId(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
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

    public StaffEmploymentDetails getEmploymentOfStaff(long staffId, long unitId) {
        EmploymentQueryResult employment = employmentGraphRepository.getEmploymentOfStaff(staffId, unitId);
        StaffEmploymentDetails employmentDetails = null;
        if(Optional.ofNullable(employment).isPresent()) {
            employmentDetails = convertEmploymentObject(employment);
            employmentDetails.setUnitId(unitId);
            List<EmploymentLinesQueryResult> data = employmentGraphRepository.findFunctionalHourlyCost(Collections.singletonList(employment.getId()));
            employmentDetails.setHourlyCost(CollectionUtils.isNotEmpty(data) ? data.get(0).getHourlyCost() : new BigDecimal(0));
        }
        return employmentDetails;
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

    public Staff getStaffByUserId(Long userId) {
        return staffGraphRepository.getByUser(userId);
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
        } catch (Exception ex) {
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
        staffToUpdate.getUser().setFirstName(staffPersonalDetail.getFirstName());
        staffToUpdate.getUser().setLastName(staffPersonalDetail.getLastName());
        staffPersonalDetail.setExpertiseIds(staffPersonalDetail.getExpertiseWithExperience().stream().map(StaffExperienceInExpertiseDTO::getExpertiseId).collect(Collectors.toList()));
        if (staffPersonalDetail.getCurrentStatus() == StaffStatusEnum.INACTIVE) {
            staffToUpdate.setInactiveFrom(parseDate(staffPersonalDetail.getInactiveFrom()).getTime());
        }
    }

    public StaffEmploymentTypeWrapper getStaffListAndLoginUserStaffIdByUnitId(Long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound", unitId);
        }
        Organization organization = unit.isParentOrganization() ? unit : organizationService.fetchParentOrganization(unitId);
        Long loggedInStaffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId());
        StaffEmploymentTypeWrapper staffEmploymentTypeWrapper = new StaffEmploymentTypeWrapper();
        staffEmploymentTypeWrapper.setLoggedInStaffId(loggedInStaffId);
        staffEmploymentTypeWrapper.setStaffList(staffGraphRepository.findAllStaffBasicDetailsByOrgIdAndUnitId(organization.getId(), unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath()));
        return staffEmploymentTypeWrapper;
    }

    public String createNewUserName(String firstName, String lastName) {
        String newUserName = null;
        User existingUserName = null;
        Random rand = new Random();
        String newGeneratedUserName = null;
        while (newUserName == null) {
            newGeneratedUserName = firstName.concat(lastName).concat(String.valueOf(rand.nextInt(1000)));
            existingUserName = userGraphRepository.findUserByUserName(newGeneratedUserName);
            if (!Optional.ofNullable(existingUserName).isPresent()) {
                newUserName = newGeneratedUserName;
                break;
            }
        }
        return newUserName;
    }
}
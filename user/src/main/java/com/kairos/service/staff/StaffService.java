package com.kairos.service.staff;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.service.mail.SendGridMailService;
import com.kairos.commons.utils.*;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.staff.staff.StaffChatDetails;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user.user.password.PasswordUpdateByAdminDTO;
import com.kairos.dto.user.user.password.PasswordUpdateDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.Gender;
import com.kairos.enums.SkillLevel;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.UnitManagerDTO;
import com.kairos.persistence.model.staff.*;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.permission.UnitPermissionAccessPermissionRelationship;
import com.kairos.persistence.model.staff.personal_details.*;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.filter.FavoriteFilterQueryResult;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ContactDetailsGraphRepository;
import com.kairos.persistence.repository.user.country.TagGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
import com.kairos.rest_client.ChatRestClient;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.auth.UserService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TeamService;
import com.kairos.service.redis.RedisService;
import com.kairos.service.system_setting.SystemLanguageService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.parseDate;
import static com.kairos.commons.utils.ObjectMapperUtils.copyCollectionPropertiesByMapper;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.CommonConstants.DEFAULT_EMAIL_TEMPLATE;
import static com.kairos.constants.UserMessagesConstants.*;
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
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private TeamService teamService;
    @Inject
    private SendGridMailService sendGridMailService;
    @Inject
    private PositionService positionService;
    @Inject
    private StaffAddressService staffAddressService;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private UnitPermissionAndAccessPermissionGraphRepository unitPermissionAndAccessPermissionGraphRepository;
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
    private PasswordEncoder passwordEncoder;
    @Inject
    private RedisService redisService;
    @Inject
    private TagGraphRepository tagGraphRepository;
    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private ContactDetailsGraphRepository contactDetailsGraphRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffService.class);

    public String uploadPhoto(Long staffId, MultipartFile multipartFile) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        createDirectory(IMAGES_PATH);
        String fileName = DateUtils.getDate().getTime() + multipartFile.getOriginalFilename();
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
            redisService.invalidateAllTokenOfUser(user.getUserName());
            userGraphRepository.save(user);
        } else {
            exceptionService.dataNotMatchedException(MESSAGE_STAFF_USER_PASSCODE_NOTMATCH);
        }
        return true;
    }

    public boolean updatePasswordByManagement(Long staffId, PasswordUpdateByAdminDTO passwordUpdateDTO) {
        Staff staff = staffGraphRepository.findByStaffId(staffId);
        if (staff != null) {
            User userForStaff = staff.getUser();
            CharSequence newPassword = CharBuffer.wrap(passwordUpdateDTO.getNewPassword());
            userForStaff.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            redisService.invalidateAllTokenOfUser(userForStaff.getUserName());
            userGraphRepository.save(userForStaff);
        } else {
            exceptionService.dataNotMatchedException(MESSAGE_STAFF_USER_PASSCODE_NOTMATCH);
        }
        return true;
    }

    public StaffDTO savePersonalDetail(long staffId, StaffDTO staffDTO, long unitId) {
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unitId);
        Organization parentUnit = organizationService.fetchParentOrganization(unitId);

        Staff staffToUpdate = staffGraphRepository.findOne(staffId);
        if (StaffStatusEnum.ACTIVE.equals(staffDTO.getCurrentStatus()) && StringUtils.isBlank(staffDTO.getUserName())) {
            exceptionService.dataNotFoundByIdException(ERROR_STAFF_USERNAME_NOTNULL);
        }
        if (staffToUpdate == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNITID_NOTFOUND);
        }
        if (isNotNull(staffToUpdate) && isNotNull(staffToUpdate.getContactDetail()) && isNotNull(staffToUpdate.getContactDetail().getPrivateEmail()) && !staffToUpdate.getContactDetail().getPrivateEmail().equals(staffDTO.getContactDetail().getPrivateEmail())) {
            if (staffGraphRepository.findStaffByEmailIdInOrganization(staffDTO.getContactDetail().getPrivateEmail(), parentUnit.getId()) != null) {
                exceptionService.duplicateDataException(MESSAGE_EMAIL_ALREADYEXIST, "Staff", staffDTO.getContactDetail().getPrivateEmail());
            }
        }
        if (StaffStatusEnum.ACTIVE.equals(staffToUpdate.getCurrentStatus()) && StaffStatusEnum.FICTIVE.equals(staffDTO.getCurrentStatus())) {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOY_NOTCONVERT_FICTIVE);
        }
        if(!validTeamDetails(staffDTO.getTeams())){
            exceptionService.actionNotPermittedException(MESSAGE_TEAM_DETAIL_NOT_VALID);
        }
        //todo we might create a job to inactive user from particular date
        if (StaffStatusEnum.INACTIVE.equals(staffDTO.getCurrentStatus())) {
            redisService.invalidateAllTokenOfUser(staffToUpdate.getUser().getUserName());
        }
        List<Expertise> oldExpertise = assignExpertise(staffId, staffDTO, userAccessRoleDTO, staffToUpdate);
        if (isNotNull(staffDTO.getLanguageId())) {
            Language language = languageGraphRepository.findOne(staffDTO.getLanguageId());
            staffToUpdate.setLanguage(language);
        }
        List<Expertise> expertise = expertiseGraphRepository.getExpertiseByIdsIn(staffDTO.getExpertiseIds());
        // Setting Staff Details)
        setStaffDetails(staffToUpdate, staffDTO);
        staffToUpdate.setStaffChildDetails(ObjectMapperUtils.copyCollectionPropertiesByMapper(staffDTO.getStaffChildDetails(), StaffChildDetail.class));
        updateUserDetails(staffDTO, userAccessRoleDTO, staffToUpdate);
        // Set if user is female and pregnant
        User user = updateUserDetails(staffId, staffDTO);
        staffDTO.setPregnant(user.isPregnant());
        //saving addresses of staff
        staffAddressService.saveAddress(staffToUpdate, Arrays.asList(staffDTO.getContactAddress(), staffDTO.getSecondaryContactAddress()));
        contactDetailsGraphRepository.save(staffToUpdate.getContactDetail());
        assignTags(staffId, staffDTO, staffToUpdate);
        staffGraphRepository.unlinkStaffChilds(staffId);
        staffToUpdate.getStaffChildDetails().forEach(staffChildDetail -> staffChildDetail.setId(null));
        Staff staff = staffGraphRepository.save(staffToUpdate);
        staffDTO.setUserName(staff.getUser().getUserName());
        setStaffChildDetailsInPersonalDetail(staff, staffDTO);
        if (oldExpertise != null) {
            List<Long> expertiseIds = oldExpertise.stream().map(Expertise::getId).collect(Collectors.toList());
            staffGraphRepository.removeSkillsByExpertise(staffToUpdate.getId(), expertiseIds);
        }
        List<Long> expertiseIds = expertise.stream().map(Expertise::getId).collect(Collectors.toList());
        staffGraphRepository.updateSkillsByExpertise(staffToUpdate.getId(), expertiseIds, DateUtils.getDate().getTime(), DateUtils.getDate().getTime(), SkillLevel.ADVANCE);
        List<SectorAndStaffExpertiseQueryResult> staffExpertiseQueryResults = copyCollectionPropertiesByMapper(staffExpertiseRelationShipGraphRepository.getSectorWiseExpertiseWithExperience(staffId), SectorAndStaffExpertiseQueryResult.class);
        staffDTO.setSectorWiseExpertise(copyCollectionPropertiesByMapper(staffRetrievalService.getSectorWiseStaffAndExpertise(staffExpertiseQueryResults), SectorAndStaffExpertiseDTO.class));
        teamService.assignStaffInTeams(staff, staffDTO.getTeams(), unitId);
        return staffRetrievalService.getPersonalInfo(staffId, unitId);
    }

    private boolean validTeamDetails(List<TeamDTO> teams) {
        boolean isValidTeam = true;
        for (TeamDTO team : teams) {
            if(!team.isTeamMembership() && isNull(team.getLeaderType())){
                isValidTeam = false;
                break;
            }
        }
        return isValidTeam;
    }

    private User updateUserDetails(long staffId, StaffDTO staffDTO) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        if (!ObjectUtils.isEquals(user.getCprNumber(), staffDTO.getCprNumber())) {
            user.setCprNumber(staffDTO.getCprNumber());
            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(staffDTO.getCprNumber()));
            user.setGender(CPRUtil.getGenderFromCPRNumber(staffDTO.getCprNumber()));
        }
        user.setPregnant(Gender.FEMALE.equals(user.getGender()) && staffDTO.isPregnant());
        user.setUserName(staffDTO.getUserName());
        userGraphRepository.save(user);
        return user;
    }

    private void assignTags(long staffId, StaffDTO staffDTO, Staff staffToUpdate) {
        Map<Long, TagDTO> tagDTOMap = staffDTO.getTags().stream().collect(Collectors.toMap(k -> k.getId().longValue(), Function.identity()));
        List<Tag> tagList = tagGraphRepository.findAllById(staffDTO.getTags().stream().map(k -> k.getId().longValue()).collect(Collectors.toList()));
        tagList.forEach(tag -> {
            tag.setStartDate(tagDTOMap.get(tag.getId()).getStartDate());
            tag.setEndDate(tagDTOMap.get(tag.getId()).getEndDate());
        });
        staffToUpdate.setTags(tagList);

        if (isCollectionNotEmpty(staffDTO.getTags())) {

            staffGraphRepository.unlinkTagsFromStaff(staffId, staffDTO.getTags().stream().map(tagDTO -> tagDTO.getId().longValue()).collect(Collectors.toList()));
        } else {
            staffGraphRepository.unlinkAllTagsFromStaff(staffId);
        }
    }

    private void updateUserDetails(StaffDTO staffDTO, UserAccessRoleDTO userAccessRoleDTO, Staff staffToUpdate) {
        if (userAccessRoleDTO.isManagement() || staffToUpdate.getUser().getId().equals(UserContext.getUserDetails().getId())) {
            if (isNull(staffToUpdate.getUser().getUserName()) || !staffToUpdate.getUser().getUserName().equalsIgnoreCase(staffDTO.getUserName()) && !staffToUpdate.getUser().isUserNameUpdated()) {
                User user = userGraphRepository.findUserByUserName("(?i)" + staffDTO.getUserName());
                if (!Optional.ofNullable(user).isPresent()) {
                    staffToUpdate.getUser().setUserName(staffDTO.getUserName());
                    staffToUpdate.getUser().setUserNameUpdated(true);
                    staffDTO.setUserNameUpdated(true);
                } else {
                    exceptionService.duplicateDataException("message.user.userName.already.use");
                }
            }
        }
    }

    public List<Expertise> assignExpertise(long staffId, StaffDTO staffDTO, UserAccessRoleDTO userAccessRoleDTO, Staff staffToUpdate) {
        List<Expertise> oldExpertise = staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staffToUpdate.getId());
        List<Long> expertises = staffDTO.getExpertiseWithExperience().stream().map(StaffExpertiseDTO::getExpertiseId).collect(Collectors.toList());
        if (!CollectionUtils.isEqualCollection(expertises, oldExpertise.stream().map(expertise -> expertise.getId()).collect(Collectors.toList())) && !userAccessRoleDTO.isManagement()) {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_EXPERTISE_NOTCHANGED);
        }
        List<Expertise> expertiseList = expertiseGraphRepository.findAllById(expertises);
        Map<Long, Expertise> expertiseMap = expertiseList.stream().collect(Collectors.toMap(Expertise::getId, Function.identity()));
        List<StaffExperienceInExpertiseDTO> staffExperienceInExpertiseDTOList = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndExpertiseIds(staffId, expertises);
        Map<Long, StaffExperienceInExpertiseDTO> staffExperienceInExpertiseDTOMap = staffExperienceInExpertiseDTOList.stream().collect(Collectors.toMap(StaffExperienceInExpertiseDTO::getExpertiseId, Function.identity()));
        List<StaffExpertiseRelationShip> staffExpertiseRelationShips = staffExpertiseRelationShipGraphRepository.getRelationShipByStaffExcludingCurrentExpertise(staffId, expertises);
        staffExpertiseRelationShipGraphRepository.deleteAll(staffExpertiseRelationShips);
        expertiseService.assignExpertiseToStaff(staffDTO, staffToUpdate, expertiseMap, staffExperienceInExpertiseDTOMap);
        return oldExpertise;
    }

    private void setStaffChildDetailsInPersonalDetail(Staff staff, StaffDTO staffDTO) {
        Map<Long, StaffChildDetailDTO> staffChildDetailDTOMap = new HashMap<>();
        staff.getStaffChildDetails().forEach(staffChildDetail -> {
            if (!staffChildDetailDTOMap.containsKey(staffChildDetail.getId())) {
                staffChildDetailDTOMap.put(staffChildDetail.getId(), ObjectMapperUtils.copyPropertiesByMapper(staffChildDetail, StaffChildDetailDTO.class));
            }
        });
        staffDTO.setStaffChildDetails(new ArrayList<>(staffChildDetailDTOMap.values()));
    }

    public Map<String, Object> saveNotes(long staffId, String generalNote, String requestFromPerson) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff != null) {
            staff.saveNotes(generalNote, requestFromPerson);
            staffGraphRepository.save(staff);
            return staff.retrieveNotes();
        }
        return null;
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
        AccessGroup accessGroup = accessGroupRepository.findById(accessGroupId).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(ERROR_STAFF_ACCESSGROUP_NOTFOUND, accessGroupId)));
        validateAccessGroup(accessGroup);
        List<StaffDTO> staffList = new ArrayList<>();
        List<StaffDTO> staffErrorList = new ArrayList<>();
        StaffUploadBySheetQueryResult staffUploadBySheetQueryResult = new StaffUploadBySheetQueryResult();
        staffUploadBySheetQueryResult.setStaffErrorList(staffErrorList);
        staffUploadBySheetQueryResult.setStaffList(staffList);
        Organization organization = organizationService.fetchParentOrganization(unitId);
        Random random = new Random();
        try (InputStream stream = multipartFile.getInputStream()) {
            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) {
                exceptionService.internalServerError(ERROR_XSSFSHEET_NOMOREROW, 0);

            }
            Set<Long> externalIdsOfStaffToBeSaved = new HashSet<>();
            boolean headerSkipped = false;
            skipHeaders(sheet, externalIdsOfStaffToBeSaved, headerSkipped);
            List<Long> alreadyAddedStaffIds = staffGraphRepository.findStaffByExternalIdIn(externalIdsOfStaffToBeSaved);
            SystemLanguage defaultSystemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(unitId);
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (validateRow(row)) break;
                // Skip headers
                if (row.getRowNum() == 0) {
                    continue;
                }
                // to check mandatory fields
                int[] mandatoryCellColumnIndexs = {2, 20, 21, 23, 24, 25, 28, 41};
                List<String> missingMandatoryFields = validateStaffData(row, mandatoryCellColumnIndexs, sheet);
                Long cprAsLong = getCPR(row, null);
                String firstName = getFirstName(row, "");
                String lastName = getLastName(row, "");
                String privateEmail = getStringValue(row, "", 28);
                String userName = getUserName(staffErrorList, row, cprAsLong, firstName, lastName, "", random);
                String externalIdValueAsString = getStringValueOfIndexedCell(row, 2);
                if (isCollectionNotEmpty(missingMandatoryFields) || cprAsLong == null || StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName) || StringUtils.isBlank(privateEmail) || StringUtils.isBlank(externalIdValueAsString)) {
                    StaffDTO staffDTO = new StaffDTO();
                    staffDTO.setFirstName(firstName);
                    staffDTO.setLastName(lastName);
                    staffDTO.setErrorMessage("Missing field(s) : " + StringUtils.join(missingMandatoryFields, ", "));

                    if (isNotNull(cprAsLong)) {
                        staffDTO.setCprNumber(BigInteger.valueOf(cprAsLong).toString());
                    }
                    staffErrorList.add(staffDTO);
                } else {
                    setInformationAndSaveStaff(accessGroupId, staffList, staffErrorList, organization, alreadyAddedStaffIds, defaultSystemLanguage, row, missingMandatoryFields, cprAsLong, firstName, lastName, privateEmail, externalIdValueAsString, userName);
                }
            }
            if (isCollectionNotEmpty(staffList)) {
                activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(staffList.stream().map(staff -> staff.getId()).collect(Collectors.toList())), unitId);
            }
            return staffUploadBySheetQueryResult;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return staffUploadBySheetQueryResult;
    }

    private String getUserName(List<StaffDTO> staffErrorList, Row row, Long cprAsLong, String firstName, String lastName, String userName, Random rand) {
        if (StringUtils.isEmpty(String.valueOf(row.getCell(19)))) {
            userName = createNewUserName(firstName, lastName, rand);

        } else {
            userName = getExistingUserName(staffErrorList, row, cprAsLong, firstName, lastName, userName);
        }
        return userName;
    }

    private void validateAccessGroup(AccessGroup accessGroup) {
        if (accessGroup.getEndDate() != null && accessGroup.getEndDate().isBefore(DateUtils.getCurrentLocalDate())) {
            exceptionService.actionNotPermittedException(ERROR_ACCESS_EXPIRED, accessGroup.getName());
        }
    }

    private String getExistingUserName(List<StaffDTO> staffErrorList, Row row, Long cprAsLong, String firstName, String lastName, String userName) {
        User user = userGraphRepository.findUserByUserName(getStringValueOfIndexedCell(row, 19));
        if (Optional.ofNullable(user).isPresent()) {
            prepareStaffDTO(staffErrorList, cprAsLong, firstName, lastName, "UserName already exist");
        } else {
            userName = getStringValueOfIndexedCell(row, 19);
        }
        return userName;
    }

    private boolean validateRow(Row row) {
        if (String.valueOf(row.getCell(0)) == null || String.valueOf(row.getCell(0)).isEmpty()) {
            return true;
        }
        if (row.getCell(0) == null) {
            LOGGER.info("No more rows");
            return true;
        }
        return false;
    }

    private void skipHeaders(XSSFSheet sheet, Set<Long> externalIdsOfStaffToBeSaved, boolean headerSkipped) {
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
    }

    private void setInformationAndSaveStaff(Long accessGroupId, List<StaffDTO> staffList, List<StaffDTO> staffErrorList, Organization organization, List<Long> alreadyAddedStaffIds, SystemLanguage defaultSystemLanguage, Row row, List<String> missingMandatoryFields, Long cprAsLong, String firstName, String lastName, String privateEmail, String externalIdValueAsString, String userName) {
        Long externalId = (StringUtils.isBlank(externalIdValueAsString)) ? 0 : Long.parseLong(externalIdValueAsString);
        if (alreadyAddedStaffIds.contains(externalId)) {
            prepareStaffDTO(staffErrorList, cprAsLong, firstName, lastName, "Duplicate External Id");
            return;
        }
        if (staffGraphRepository.isStaffExistsByCPRNumber(cprAsLong.toString(), organization.getId())) {
            prepareStaffDTO(staffErrorList, cprAsLong, firstName, lastName, "Staff already exist with CPR Number " + cprAsLong);
            return;
        }
        Staff staff = getStaff(firstName, lastName, externalId);
        setBadgeNumber(row, staff);
        ContactAddress contactAddress = extractContactAddressFromRow(row);
        if (!Optional.ofNullable(contactAddress).isPresent()) {
            contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(organization);
        }
        ContactDetail contactDetail = extractContactDetailFromRow(row);
        staff.setContactDetail(contactDetail);
        staff.setContactAddress(contactAddress);
        User user = null;
        user = setMissingMandatoryFieldInUser(defaultSystemLanguage, row, missingMandatoryFields, cprAsLong, firstName, lastName, privateEmail, userName, staff, contactDetail, user);
        staffGraphRepository.save(staff);
        StaffDTO staffDTO = ObjectMapperUtils.copyPropertiesByMapper(staff, StaffDTO.class);
        staffDTO.setGender(user.getGender());
        staffDTO.setAge(Period.between(CPRUtil.getDateOfBirthFromCPR(user.getCprNumber()), LocalDate.now()).getYears());
        staffList.add(staffDTO);
        if (!staffGraphRepository.staffAlreadyInUnit(externalId, organization.getId())) {
            positionService.createPosition(organization, staff, accessGroupId, DateUtils.getCurrentDateMillis(),organization.getId());
        }
    }

    private void setBadgeNumber(Row row, Staff staff) {
        if (row.getCell(17) != null) {
            staff.setBadgeNumber(getStringValueOfIndexedCell(row, 17));
        }
    }

    private Staff getStaff(String firstName, String lastName, Long externalId) {
        Staff staff = new Staff();
        staff.setExternalId(externalId);
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setFamilyName(lastName);
        return staff;
    }

    private void prepareStaffDTO(List<StaffDTO> staffErrorList, Long cprAsLong, String firstName, String lastName, String s) {
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setFirstName(firstName);
        staffDTO.setLastName(lastName);
        staffDTO.setErrorMessage(s);
        staffDTO.setCprNumber(BigInteger.valueOf(cprAsLong).toString());
        staffErrorList.add(staffDTO);
    }

    private User setMissingMandatoryFieldInUser(SystemLanguage defaultSystemLanguage, Row row, List<String> missingMandatoryFields, Long cprAsLong, String firstName, String lastName, String privateEmail, String userName, Staff staff, ContactDetail contactDetail, User user) {
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
                    user.setEmail(contactDetail.getPrivateEmail().toLowerCase());
                } else {
                    user.setEmail(user.getFirstName().trim() + KAIROS_EMAIL);
                }
                String defaultPassword = user.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH;
                user.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
            }
            user.setUserName(userName);
            staff.setUser(user);
        }
        return user;
    }

    private String getStringValue(Row row, String privateEmail, int i) {
        if (isNotNull(row.getCell(i, Row.RETURN_BLANK_AS_NULL))) {
            privateEmail = getStringValueOfIndexedCell(row, i);
        }
        return privateEmail;
    }

    private String getLastName(Row row, String lastName) {
        lastName = getStringValue(row, lastName, 21);
        return lastName;
    }

    private String getFirstName(Row row, String firstName) {
        firstName = getStringValue(row, firstName, 20);
        return firstName;
    }

    private Long getCPR(Row row, Long cprAsLong) {

        if (isNotNull(row.getCell(41, Row.RETURN_BLANK_AS_NULL))) {
            cprAsLong = new Double(getStringValueOfIndexedCell(row, 41)).longValue();
        }
        return cprAsLong;
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
                    appendFullStreetName(fullStreetName, newArray);
                    contactAddress.setHouseNumber(houseNumber);
                    contactAddress.setStreet(fullStreetName.toString());
                    contactAddress.setCity(row.getCell(25).toString());
                }
                return contactAddress;
            }
        }
        return null;
    }

    private void appendFullStreetName(StringBuilder fullStreetName, String[] newArray) {
        for (int i = 0; i < newArray.length - 1; i++) {
            if (i == 0) {
                fullStreetName.append(newArray[i]);
            } else {
                fullStreetName.append(" ").append(newArray[i]);
            }
        }
    }

    private String getStringValueOfIndexedCell(Row row, int cellIndex) {
        Cell cellValue = row.getCell(cellIndex);
        cellValue.setCellType(Cell.CELL_TYPE_STRING);
        return cellValue.getStringCellValue().trim();
    }

    private ContactDetail extractContactDetailFromRow(Row row) {
        Cell cell = row.getCell(26);
        ContactDetail contactDetail = null;
        cell.setCellType(Cell.CELL_TYPE_STRING);
        String telephoneNumber = cell.getStringCellValue();
        if (!StringUtils.isBlank(telephoneNumber)) {
            contactDetail = new ContactDetail();
            contactDetail.setPrivatePhone(telephoneNumber.trim());
        }
        cell = row.getCell(27);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        String cellPhoneNumber = cell.getStringCellValue();
        if (!StringUtils.isBlank(cellPhoneNumber)) {
            if (!Optional.ofNullable(contactDetail).isPresent()) {
                contactDetail = new ContactDetail();
            }
            contactDetail.setMobilePhone(cellPhoneNumber.trim());
        }
        cell = row.getCell(28);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        String email = cell.getStringCellValue();
        if (!StringUtils.isBlank(email)) {
            if (!Optional.ofNullable(contactDetail).isPresent()) {
                contactDetail = new ContactDetail();
            }
            contactDetail.setPrivateEmail(email.toLowerCase().trim());
        }
        return contactDetail;
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

    public void setUserAndPosition(OrganizationBaseEntity organizationBaseEntity, User user, Long accessGroupId, boolean parentOrganization, boolean union) {
        Organization organization = organizationService.fetchParentOrganization(organizationBaseEntity.getId());
        Position position = positionGraphRepository.findPositionByOrganizationIdAndUserId(organization.getId(), user.getId());
        if (isNull(position)) {
            position = createPositionAndStaff(user, organization);
        }
        // if the organization is not parent organization then adding position in parent organization.
        organization.getPositions().add(position);
        organizationGraphRepository.save(organization);
        user.setCountryId(organization.getCountry().getId());

        UnitPermission unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfUser(organization.getId(), user.getId(), organizationBaseEntity.getId()).orElse(new UnitPermission());
        if (organizationBaseEntity instanceof Organization) {
            unitPermission.setOrganization((Organization) organizationBaseEntity);
        } else {
            unitPermission.setUnit((Unit) organizationBaseEntity);
        }
        if (accessGroupId != null) {
            AccessGroup accessGroup = (union || parentOrganization) ? accessGroupRepository.getAccessGroupByParentAccessGroupId(organization.getId(), accessGroupId) : accessGroupRepository.getAccessGroupByParentId(organization.getId(), accessGroupId);
            if (Optional.ofNullable(accessGroup).isPresent()) {
                unitPermission.setAccessGroup(accessGroup);
                linkAccessOfModules(accessGroup, unitPermission);
            }
        }
        position.getUnitPermissions().add(unitPermission);
        positionGraphRepository.save(position, 2);
    }

    private Position createPositionAndStaff(User user, Organization organization) {
        Position position;
        Staff staff = new Staff(user.getEmail(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getFirstName(), StaffStatusEnum.ACTIVE, null, user.getCprNumber());
        position = new Position();
        position.setStaff(staff);
        staff.setGender(user.getGender());
        staff.setDateOfBirth(user.getDateOfBirth());
        staff.setUser(user);
        position.setName(UNIT_MANAGER_EMPLOYMENT_DESCRIPTION);
        position.setStaff(staff);
        staff.setContactAddress(staffAddressService.getStaffContactAddressByOrganizationAddress(organization));
        position.setStartDateMillis(DateUtils.getCurrentDayStartMillis());
        return position;
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
                exceptionService.internalServerError(ERROR_XSSFSHEET_NOMOREROW, 2);
            }
            long staffId;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() > 0) {
                    Cell cell = row.getCell(0);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    staffId = Long.parseLong(cell.getStringCellValue());
                    Staff staff = staffGraphRepository.findOne(staffId);
                    staffUpdated = updateStaffDetails(staffUpdated, staffList, staff, row);
                }
            }
            staffGraphRepository.saveAll(staffList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private int updateStaffDetails(int staffUpdated, List<Staff> staffList, Staff staff, Row row) {
        Cell cell;
        String firstName;
        String lastName;
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
        return staffUpdated;
    }

    public void sendEmailToUnitManager(UnitManagerDTO unitManagerDTO, String password) {
        String body = "Hi,\n\n" + "You are assigned as an unit manager and to get access in KairosPlanning.\n" + "Your username " + unitManagerDTO.getEmail() + " and password is " + password + "\n\n Thanks";
        String subject = "You are a unit manager at KairosPlanning";
        Map<String, Object> templateParam = new HashMap<>();
        templateParam.put("receiverName", unitManagerDTO.getFullName());
        templateParam.put("description", body);
        sendGridMailService.sendMailWithSendGrid(DEFAULT_EMAIL_TEMPLATE, templateParam, null, subject, unitManagerDTO.getEmail());
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

    public StaffDTO getStaffById(long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId, 1);
        StaffDTO staffDTO = ObjectMapperUtils.copyPropertiesByMapper(staff, StaffDTO.class);
        staffDTO.getContactDetail().setPrivateEmail(staff.getUser().getEmail());
        return staffDTO;
    }

    public List<Long> getCountryAdminIds(long organizationId) {
        return staffGraphRepository.getCountryAdminIds(organizationId);
    }

    public List<Long> getUnitManagerIds(long unitId) {
        Organization organization = organizationService.fetchParentOrganization(unitId);
        return staffGraphRepository.getUnitManagersIds(organization.getId(), unitId);
    }

    public StaffEmploymentDetails getMainEmploymentOfStaff(long staffId, long unitId) {
        StaffEmploymentDetails employmentDetails = null;
        List<EmploymentQueryResult> employmentQueryResults = employmentGraphRepository.getAllEmploymentsForCurrentOrganization(staffId, unitId);
        if (isCollectionNotEmpty(employmentQueryResults)) {
            EmploymentQueryResult employment = employmentQueryResults.stream().filter(employmentQueryResult -> EmploymentSubType.MAIN.equals(employmentQueryResult.getEmploymentSubType())).findAny().orElse(null);
            if (isNotNull(employment)) {
                employmentDetails = new StaffEmploymentDetails(employment.getId(), ObjectMapperUtils.copyPropertiesByMapper(employment.getExpertise(), com.kairos.dto.activity.shift.Expertise.class), employment.getEndDate(), employment.getStartDate(), employment.getUnitId(), employment.getEmploymentSubType());
            }
        }
        return employmentDetails;
    }


    public StaffEmploymentDetails getEmploymentOfStaff(long staffId, long unitId) {
        EmploymentQueryResult employment = employmentGraphRepository.getEmploymentOfStaff(staffId, unitId);
        return getStaffEmploymentDetails(unitId, employment);
    }

    private StaffEmploymentDetails getStaffEmploymentDetails(long unitId, EmploymentQueryResult employment) {
        StaffEmploymentDetails employmentDetails = null;
        if (Optional.ofNullable(employment).isPresent()) {
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
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFFFAVOURITEFILTER_NOTFOUND, staffFilterDTO.getId());
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
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFFFAVOURITEFILTER_NOTFOUND, staffFavouriteFilterId);
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
        staffList.forEach(this::addStaffInChatServer);
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

    private void setStaffDetails(Staff staffToUpdate, StaffDTO staffDTO) {
        staffToUpdate.setFirstName(staffDTO.getFirstName());
        staffToUpdate.setLastName(staffDTO.getLastName());
        staffToUpdate.setFamilyName(staffDTO.getFamilyName());
        staffToUpdate.setCurrentStatus(staffDTO.getCurrentStatus());
        staffToUpdate.setSpeedPercent(staffDTO.getSpeedPercent());
        staffToUpdate.setWorkPercent(staffDTO.getWorkPercent());
        staffToUpdate.setOvertime(staffDTO.getOvertime());
        staffToUpdate.setCostDay(staffDTO.getCostDay());
        staffToUpdate.setCostCall(staffDTO.getCostCall());
        staffToUpdate.setCostKm(staffDTO.getCostKm());
        staffToUpdate.setCostHour(staffDTO.getCostHour());
        staffToUpdate.setCostHourOvertime(staffDTO.getCostHourOvertime());
        staffToUpdate.setCapacity(staffDTO.getCapacity());
        staffToUpdate.setCareOfName(staffDTO.getCareOfName());
        staffToUpdate.setSignature(staffDTO.getSignature());
        Long contactId = staffToUpdate.getContactDetail().getId();
        staffToUpdate.setContactDetail(ObjectMapperUtils.copyPropertiesByMapper(staffDTO.getContactDetail(), ContactDetail.class));
        staffToUpdate.getContactDetail().setId(contactId);
        staffToUpdate.getUser().setFirstName(staffDTO.getFirstName());
        staffToUpdate.getUser().setLastName(staffDTO.getLastName());
        staffDTO.setExpertiseIds(staffDTO.getExpertiseWithExperience().stream().map(StaffExpertiseDTO::getExpertiseId).collect(Collectors.toList()));
        if (staffDTO.getCurrentStatus() == StaffStatusEnum.INACTIVE) {
            staffToUpdate.setInactiveFrom(parseDate(staffDTO.getInactiveFrom()).getTime());
        }
    }

    public String createNewUserName(String firstName, String lastName, Random rand) {
        String newUserName = null;
        User existingUserName;

        String newGeneratedUserName;
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

    public List<StaffDTO> getAllStaffPersonalDetailsByUnit(Long unitId) {
        List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults = staffGraphRepository.getAllStaffPersonalDetailsByUnit(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        return copyCollectionPropertiesByMapper(staffPersonalDetailQueryResults, StaffDTO.class);
    }

    public void unlinkTagFromStaff(Long tagId) {
        staffGraphRepository.unlinkTagFromStaff(tagId);
    }

    public List<StaffPermissionRelatedDataQueryResult> getStaffsDataForPermissionByUnitId(Long unitId) {
        Organization organization = organizationService.fetchParentOrganization(unitId);
        return staffGraphRepository.getStaffsPermissionRelatedDataQueryResult(organization.getId());
    }

    public Map<Long, OtherPermissionDTO> getStaffDataForPermissionByStaffIds(Collection<Long> staffIds) {
        return staffGraphRepository.getStaffPermissionRelatedDataQueryResult(staffIds).stream().collect(Collectors.toMap(StaffPermissionRelatedDataQueryResult::getStaffId, v -> v.getForOtherPermissions()));
    }

    public Long getStaffIdByUserId(Long userId, Long parentOrganizationId) {
        return staffGraphRepository.getStaffIdByUserId(userId, parentOrganizationId);
    }
}

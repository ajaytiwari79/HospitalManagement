package com.kairos.service.skill;

import com.kairos.commons.service.mail.MailService;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user.organization.OrganizationSkillDTO;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.time_care.TimeCareSkill;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.repository.organization.OrganizationMetadataRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.TagGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillCategoryGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.persistence.repository.user.skill.UserSkillLevelRelationshipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.SkillServiceTemplateClient;
import com.kairos.rest_client.TaskDemandRestClient;
import com.kairos.service.country.CitizenStatusService;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TeamService;
import com.kairos.service.organization.TimeSlotService;
import com.kairos.service.staff.StaffRetrievalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.*;

/**
 * Created by oodles on 15/9/16.
 */
@Service
@Transactional
public class SkillService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private SkillGraphRepository skillGraphRepository;
    @Inject
    private SkillCategoryGraphRepository skillCategoryGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private TeamService teamService;
    @Inject
    private TeamGraphRepository teamGraphRepository;
    @Inject
    private MailService mailService;
    @Inject
    private UserSkillLevelRelationshipGraphRepository userSkillLevelRelationshipGraphRepository;
    @Inject
    private OrganizationMetadataRepository organizationMetadataRepository;
    @Inject
    private CitizenStatusService citizenStatusService;
    @Inject
    private EnvConfig envConfig;
    @Inject
    SkillServiceTemplateClient skillServiceTemplateClient;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private TaskDemandRestClient taskDemandRestClient;
    @Inject
    private TagService tagService;
    @Inject
    private TagGraphRepository tagGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffRetrievalService staffRetrievalService;

    public Map<String, Object> createSkill(SkillDTO skillDTO, long skillCategoryId) {
        SkillCategory skillCategory = skillCategoryGraphRepository.findOne(skillCategoryId);
        if (skillCategory == null) {
            return null;
        }
        String name = "(?i)" + skillDTO.getName();
        logger.info("Added regex to Name: " + name);
        if (skillGraphRepository.checkDuplicateSkill(skillCategoryId, name).isEmpty()) {
            logger.info("Creating unique skill");
            Skill skill = new Skill(skillDTO);
            skill.setSkillCategory(skillCategory);
            List<Tag> tags = tagService.getCountryTagsByIdsAndMasterDataType(skillDTO.getTags(), MasterDataTypeEnum.SKILL);
            logger.info("tags for skill : " + tags);
            skill.setTags(tags);
            skillGraphRepository.save(skill);
            Map<String, Object> response = skill.retrieveDetails();
            return response;
        }
        exceptionService.duplicateDataException(MESSAGE_SKILL_NAME_DUPLICATE);
        return null;

    }

    public List<Map<String, Object>> getAllSkills(long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            return null;
        }
        List<Map<String, Object>> response = new ArrayList<>();
        for (Map<String, Object> result : skillGraphRepository.getSkillsByCountryId(countryId)) {
            response.add((Map<String, Object>) result.get("result"));
        }
        return response;
    }

    public Skill getSkillById(Long id, int depth) {
        return skillGraphRepository.findOne(id, depth);
    }


    public Map<String, Object> updateSkill(long countryId, SkillDTO data) {
        if (data != null) {
            Skill skill = skillGraphRepository.findOne(data.getId());

            if (skill != null) {
                skill.setName(data.getName());
                skill.setDescription(data.getDescription());
                skill.setShortName(data.getShortName());
                skillGraphRepository.removeAllCountryTags(data.getId());
                List<Tag> listOfTags = tagGraphRepository.getTagsOfSkillByDeleted(data.getId(), false);
                listOfTags.addAll(tagService.getCountryTagsByIdsAndMasterDataType(data.getTags(), MasterDataTypeEnum.SKILL));
                skill.setTags(listOfTags);
                return skillGraphRepository.save(skill).retrieveDetails();
            }

            return null;
        }
        return null;
    }


    public SkillCategory safeDeleteSkill(Long categoryId, Long skillId) {
        return skillGraphRepository.safeDelete(categoryId, skillId);
    }

    public List<Skill> getSkillsByCategoryId(Long id) {
        return skillGraphRepository.skillsByCategoryId(id);
    }


    /**
     * @param id   {id of team or organization based on type}
     * @param type type could be oranization or team
     * @return
     * @author prabjot
     * this method returns all skills based on type of node{all skills of organization or team it depends on type parameter} and relationship of staff and skills
     */
    public HashMap<String, Object> getAllAvailableSkills(long id, String type) {

        HashMap<String, Object> response = new HashMap<>();
        Organization parent = organizationService.fetchParentOrganization(id);
        List<Map<String, Object>> organizationSkills;
        if (parent == null) {
            organizationSkills = unitGraphRepository.getSkillsOfParentOrganizationWithActualName(id);
        } else {
            organizationSkills = unitGraphRepository.getSkillsOfChildOrganizationWithActualName(parent.getId(), id);
        }

        List<Map<String, Object>> orgSkillRel = new ArrayList<>(organizationSkills.size());
        for (Map<String, Object> map : organizationSkills) {
            orgSkillRel.add((Map<String, Object>) map.get("data"));
        }

        response.put("orgData", orgSkillRel);
        response.put("skillLevels", Skill.SkillLevel.values());
        response.put("teamList", teamService.getAllTeamsInOrganization(id));

        return response;

    }


    /**
     * @param id         {id of team or organization based on type}
     * @param skillId
     * @param isSelected {true or false if true skill will be added if not exist otherwise updated, if false skill will be removed}
     * @param type       {organization,team}
     * @return updated skills irrespective of team or organization
     * @author prabjot
     * to add new skill based onn type of node {organization,team}
     * if type is an organozation then skill will be added to an organization otherwise it will added to team
     */
    public HashMap<String, Object> addNewSkill(long id, long skillId, boolean isSelected, String type) {


        if (ORGANIZATION.equalsIgnoreCase(type)) {

            if (isSelected) {
                if (unitGraphRepository.isSkillAlreadyExist(id, skillId) == 0) {
                    unitGraphRepository.addSkillInOrganization(id, Arrays.asList(skillId), DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
                } else {
                    unitGraphRepository.updateSkillInOrganization(id, Arrays.asList(skillId), DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
                }
            } else {
                unitGraphRepository.removeSkillFromOrganization(id, skillId, DateUtils.getCurrentDate().getTime());
            }
            return getAllAvailableSkills(id, type);

        } else if (TEAM.equalsIgnoreCase(type)) {
            long createdDate = DateUtils.getCurrentDate().getTime();
            if (isSelected) {
                teamGraphRepository.addSkillInTeam(id, skillId, createdDate, createdDate, true);
            } else {
                teamGraphRepository.addSkillInTeam(id, skillId, createdDate, createdDate, false);
            }
            return getAllAvailableSkills(id, type);
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_TYPE_NOTVALID);

        }
        return null;
    }


    public boolean deleteSkill(long skillId) {
        Skill skill = skillGraphRepository.findOne(skillId);
        if (skill == null) {
            return false;
        }
        skill.setEnabled(false);
        skillGraphRepository.save(skill);
        return true;
    }

    /**
     * to update visitour id of skill for particular organization
     *
     * @param unitId
     * @param skillId
     * @return
     */
   /* public boolean updateVisitourIdOfSkill(long unitId, long skillId, String visitourId,String type) {

        if(ORGANIZATION.equalsIgnoreCase(type)){
            return skillGraphRepository.updateVisitourIdOfSkillInOrganization(unitId, skillId, visitourId);
        } else if(TEAM.equalsIgnoreCase(type)) {
            return skillGraphRepository.updateVisitourIdOfSkillInTeam(unitId,skillId,visitourId);
        } else {
            throw new InternalError("Type incorrect");
        }
    }*/
    public boolean updateSkillOfOrganization(long unitId, long skillId, String type, OrganizationSkillDTO organizationSkillDTO) {
        Boolean skillUpdated = false;
        if (ORGANIZATION.equalsIgnoreCase(type)) {

            if (organizationSkillDTO.getCustomName() == null || organizationSkillDTO.getCustomName() == "") {
                skillUpdated = skillGraphRepository.updateSkillOfOrganization(unitId, skillId);
            } else {
//                updateOrganizationTagsOfSkill
                skillUpdated = skillGraphRepository.updateSkillOfOrganizationWithCustomName(unitId, skillId, organizationSkillDTO.getCustomName());
            }
            if (skillUpdated) {
                tagService.updateOrganizationTagsOfSkill(skillId, unitId, organizationSkillDTO.getTags());
            }
            return skillUpdated;
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_TYPE_NOTVALID);
        }
        return false;
    }

    public boolean requestForCreateNewSkill(long unitId, Skill skill) {
        Unit unit = unitGraphRepository.findOne(unitId);

        if (unit == null) {
            return false;
        }
        skill.setEnabled(false);
        skill.setSkillStatus(Skill.SkillStatus.PENDING);
        skillGraphRepository.save(skill);
        mailService.sendMailWithSendGrid(null, null, "Request for create new skill", "Skill creation request", ADMIN_EMAIL);
        return true;
    }

    public Map<String, Object> getSkills(long staffId, long id, String type) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }

        long unitId = 0;
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            unitId = id;
        } else if (TEAM.equalsIgnoreCase(type)) {
            Unit unit = unitGraphRepository.getOrganizationByTeamId(id);
            unitId = unit.getId();
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_TYPE_NOTVALID);
            //throw new InternalError("Type incorrect");
        }

        List<Long> selectedSkillId = new ArrayList<>();
        List<Map<String, Object>> treeData = new ArrayList<>();
        for (Map<String, Object> data : staffGraphRepository.getSkills(staffId, unitId)) {
            Map<String, Object> map = (Map<String, Object>) data.get("data");
            for (Map<String, Object> skill : (List<Map<String, Object>>) map.get("children")) {
                if (skill.get("isSelected") != null && (boolean) skill.get("isSelected")) {
                    selectedSkillId.add((long) skill.get("id"));
                }
            }
            treeData.add(map);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> map : userSkillLevelRelationshipGraphRepository.getStaffSkillRelationship(staffId, selectedSkillId, unitId)) {
            Map<String, Object> data = (Map<String, Object>) map.get("data");
            list.add(data);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("tableData", list);
        map.put("treeData", treeData);
        map.put("skillLevels", Arrays.asList(Skill.SkillLevel.ADVANCE, Skill.SkillLevel.BASIC, Skill.SkillLevel.EXPERT));
        return map;
    }

    /**
     * @param
     * @param staffId
     * @param removedSkillIds
     * @param isSelected
     * @param unitId
     * @return
     */
    public List assignSkillToStaff(long staffId, List<Long> removedSkillIds, boolean isSelected, long unitId) {

        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        List<Map<String, Object>> response;
        if (isSelected) {
            staffGraphRepository.addSkillInStaff(staffId, removedSkillIds, DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime(), Skill.SkillLevel.ADVANCE, true);
            response = prepareSelectedSkillResponse(staffId, removedSkillIds, unitId);
        } else {
            staffGraphRepository.deleteSkillFromStaff(staffId, removedSkillIds, DateUtils.getCurrentDate().getTime());
            response = Collections.emptyList();
        }
        /*if (staffGraphRepository.checkIfStaffIsTaskGiver(staffId, unitId) != 0) {
            logger.info("Staff  is TaskGiver: Now Syncing Skills in Visitour");
            updateSkillsOfStaffInVisitour(staff, unitId);
        }*/
        return response;

    }

    private List<Map<String, Object>> prepareSelectedSkillResponse(long staffId, List<Long> skillId, long unitId) {

        List<Map<String, Object>> staffSkillInfo = staffGraphRepository.getStaffSkillInfo(staffId, skillId, unitId);

        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> copyMap;
        for (Map<String, Object> staffSkillRel : staffSkillInfo) {
            Map<String, Object> staffSkillRelInfo = (Map<String, Object>) staffSkillRel.get("data");
            copyMap = new HashMap<>();
            copyMap.putAll(staffSkillRelInfo);
            copyMap.put("startDate", getDate((long) staffSkillRelInfo.get("startDate")));
            copyMap.put("endDate", getDate((long) staffSkillRelInfo.get("endDate")));
            copyMap.put("lastSyncInVisitour", getDate((long) staffSkillRelInfo.get("lastSyncInVisitour")));
            list.add(copyMap);
        }
        return list;

    }


    public void updateStaffSkillLevel(long staffId, long skillId, Skill.SkillLevel skillLevel, long startDate, long endDate, boolean status, long unitId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        userSkillLevelRelationshipGraphRepository.updateStaffSkill(staffId, skillId, skillLevel, startDate, endDate, status);
    }


    public boolean assignSkillToStaff(long id, long staffId, long skillId, boolean isSelected, String type) {

        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_ID_NOTFOUND);

        }

        long lastModificationDate = DateUtils.getCurrentDate().getTime();
        if (isSelected) {
            staffGraphRepository.addSkillInStaff(staffId, Arrays.asList(skillId), lastModificationDate, lastModificationDate, Skill.SkillLevel.ADVANCE, true);
        } else {
            staffGraphRepository.addSkillInStaff(staffId, Arrays.asList(skillId), lastModificationDate, lastModificationDate, Skill.SkillLevel.ADVANCE, false);
        }
        return true;
    }

    public Map<String, Object> getStaffSkills(long id, String type) {


        List<Map<String, Object>> skills = null;
        List<Map<String, Object>> response = new ArrayList<>();
        List<StaffPersonalDetailDTO> staffList = new ArrayList<>();
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            staffList = staffRetrievalService.getStaffWithBasicInfo(id, false);
            List<Long> staffIds = new ArrayList<>(staffList.size());
            staffList.stream().forEach(staffPersonalDetailDTO -> {
                staffIds.add(staffPersonalDetailDTO.getId());
            });
            skills = unitGraphRepository.getAssignedSkillsOfStaffByOrganization(id, staffIds);

        } else if (TEAM.equalsIgnoreCase(type)) {
            staffList = staffGraphRepository.getStaffByTeamId(id, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
            List<Long> staffIds = new ArrayList<>(staffList.size());
            staffList.stream().forEach(staffPersonalDetailDTO -> {
                staffIds.add(staffPersonalDetailDTO.getId());
            });
            skills = teamGraphRepository.getAssignedSkillsOfStaffByTeam(id, staffIds);
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_TYPE_NOTVALID);
            // throw new InternalError("Type is not valid");
        }
        List<Map<String, Object>> skillsResponse = new ArrayList<>();
        for (Map<String, Object> map : skills) {
            skillsResponse.add((Map<String, Object>) map.get("data"));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("skills", skillsResponse);
        map.put("staffList", ObjectMapperUtils.copyPropertiesOfListByMapper(staffList, Map.class));
        return map;
    }


    public List<Map<String, Object>> getSkillsOfOrganization(long organizationId) {
        return unitGraphRepository.getSkillsOfOrganization(organizationId);
    }

    public List<Map<String, Object>> getSkillsForTaskType(@PathVariable long countryId) {
        return skillGraphRepository.getSkillsByCountryForTaskType(countryId);
    }

    public Iterable<Skill> importSkillsFromTimeCare(List<TimeCareSkill> timeCareSkills, Long countryId) {

        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }

        List<String> externalIds = timeCareSkills.stream().map(timeCareSkill -> String.valueOf(timeCareSkill.getId())).
                collect(Collectors.toList());


        List<Skill> skillsByExternalIds = (externalIds.isEmpty()) ? new ArrayList<>() :
                skillGraphRepository.findByExternalIdInAndIsEnabledTrue(externalIds);

        SkillCategory skillCategory = skillCategoryGraphRepository.findByNameIgnoreCaseAndIsEnabledTrue(countryId, "(?i)" + SKILL_CATEGORY_FOR_TIME_CARE);
        if (!Optional.ofNullable(skillCategory).isPresent()) {
            skillCategory = new SkillCategory(SKILL_CATEGORY_FOR_TIME_CARE);
        }
        skillCategory.setCountry(country);
        List<Skill> skillsToCreate = new ArrayList<>();
        for (TimeCareSkill timeCareSkill : timeCareSkills) {
            Optional<Skill> result = skillsByExternalIds.stream().filter(skillByExternalId -> skillByExternalId.getExternalId().equals(String.valueOf(timeCareSkill.getId()))).findFirst();
            Skill skill = (result.isPresent()) ? result.get() : new Skill();
            skill.setName(timeCareSkill.getName());
            skill.setShortName(timeCareSkill.getShortName());
            skill.setSkillCategory(skillCategory);
            skill.setExternalId(String.valueOf(timeCareSkill.getId()));
            skillsToCreate.add(skill);
        }
        return skillGraphRepository.saveAll(skillsToCreate);
    }

    public List<Skill> getSkillsByName(Set<String> skillNames) {
        int sizeOfSkillNames = skillNames.size();
        int skip = 0;
        List<Skill> skills = new ArrayList<>();
        if (sizeOfSkillNames > DB_RECORD_LIMIT) {
            do {
                List<String> skillsToFind = skillNames.stream().skip(skip).limit(DB_RECORD_LIMIT).collect(Collectors.toList());
                skills.addAll(skillGraphRepository.findSkillByNameIn(skillsToFind));
                skip += DB_RECORD_LIMIT;
            } while (skip <= sizeOfSkillNames);
        } else {
            List<String> skillsToFind = skillNames.stream().skip(skip).limit(DB_RECORD_LIMIT).collect(Collectors.toList());
            skills.addAll(skillGraphRepository.findSkillByNameIn(skillsToFind));
        }
        return skills;
    }
}

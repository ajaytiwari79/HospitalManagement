package com.kairos.activity.service.wta;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.WTADetailRestClient;
import com.kairos.activity.response.dto.WTADTO;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.tag.Tag;
import com.kairos.activity.persistence.model.wta.*;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.WTABuilderService;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.wta.RuleTemplateCategoryMongoRepository;
import com.kairos.activity.persistence.repository.wta.WTABaseRuleTemplateMongoRepository;
import com.kairos.activity.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.activity.ActivityService;
import com.kairos.activity.service.activity.TimeTypeService;
import com.kairos.activity.service.tag.TagService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;

import com.kairos.response.dto.web.wta.WTABaseRuleTemplateDTO;
import com.kairos.response.dto.web.wta.WTABasicDetailsDTO;
import com.kairos.response.dto.web.wta.WTADefaultDataInfoDTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by pawanmandhan on 2/8/17.
 */

@Transactional
@Service
public class WTAService extends MongoBaseService {
    @Inject
    private WorkingTimeAgreementMongoRepository wtaRepository;
    @Inject
    private CountryRestClient countryRestClient;
    @Inject
    private RuleTemplateCategoryMongoRepository ruleTemplateCategoryRepository;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateGraphRepository;
    @Inject
    private RuleTemplateService ruleTemplateService;
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private TagService tagService;
    @Inject
    private WTAOrganizationService wtaOrganizationService;
    @Inject
    private WTADetailRestClient wtaDetailRestClient;
    @Inject
    private WTABuilderService wtaBuilderService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private TimeTypeService timeTypeService;


    private final Logger logger = LoggerFactory.getLogger(WTAService.class);

    /**
     * @param countryId
     * @param wtaDTO
     * @return
     * @Author Vipul
     */
    public WTAResponseDTO createWta(long countryId, WTADTO wtaDTO) {

        //TODO  API functionality has been changed for now KP-958
        //  checkUniquenessOfData(countryId, wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId());
        WorkingTimeAgreement wta = wtaRepository.getWtaByName(wtaDTO.getName(), countryId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new DuplicateDataException("Duplicate WTA name" + wtaDTO.getName());
        }
        wta = new WorkingTimeAgreement();
        // Link tags to WTA
        Date startDate = (wtaDTO.getStartDateMillis() == 0) ? DateUtils.getCurrentDate() : new Date(wtaDTO.getStartDateMillis());
        if (wtaDTO.getEndDateMillis() != null && wtaDTO.getEndDateMillis() > 0) {
            if (startDate.getTime() > wtaDTO.getEndDateMillis()) {
                throw new InvalidRequestException("End Date must not be greater than start date");
            }
            wta.setEndDate(new Date(wtaDTO.getEndDateMillis()));
        }
        WTABasicDetailsDTO wtaBasicDetailsDTO = wtaDetailRestClient.getWtaRelatedInfo(wtaDTO.getExpertiseId(), wtaDTO.getOrganizationSubType(), countryId, 0l, wtaDTO.getOrganizationType());
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getCountryDTO()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country id " + countryId);
        }
        wta.setStartDate(startDate);
        if (wtaDTO.getTags().size() > 0) {
            List<Tag> tags = tagService.getCountryTagsByIdsAndMasterDataType(wtaDTO.getTags(), MasterDataTypeEnum.WTA);
            wta.setTags(tags.stream().map(t -> t.getId()).collect(Collectors.toList()));
        }

        prepareWtaWhileCreate(wta, wtaDTO, wtaBasicDetailsDTO);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (wtaDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(wtaDTO.getRuleTemplates(), true);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            wta.setRuleTemplateIds(ruleTemplatesIds);

        }
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(wta, WTAResponseDTO.class);
        //wtaResponseDTO.setRuleTemplateIds(wtaBuilderService.getRuleTemplateDTO(wta));
        wtaResponseDTO.setStartDateMillis(wta.getStartDate().getTime());
        if (wta.getEndDate() != null) {
            wtaResponseDTO.setEndDateMillis(wta.getEndDate().getTime());
        }
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        wta.setCountryId(countryId);
        save(wta);
        wtaResponseDTO.setId(wta.getId());
        assignWTAToOrganization(wta, wtaDTO, wtaBasicDetailsDTO);


        // Adding this wta to all organization type


        // setting basic details

        return wtaResponseDTO;
    }


    private void assignWTAToOrganization(WorkingTimeAgreement wta, WTADTO wtadto, WTABasicDetailsDTO wtaBasicDetailsDTO) {
        List<WorkingTimeAgreement> workingTimeAgreements = new ArrayList<>(wtaBasicDetailsDTO.getOrganizations().size());

        wtaBasicDetailsDTO.getOrganizations().forEach(organization ->
        {
            if (!organization.isKairosHub()) {
                WorkingTimeAgreement workingTimeAgreement = new WorkingTimeAgreement();
                wtaBuilderService.copyWta(wta, workingTimeAgreement);
                workingTimeAgreement.setCountryParentWTA(wta.getId());
                workingTimeAgreement.setDisabled(false);
                if (wtadto.getRuleTemplates().size() > 0) {
                    List<WTABaseRuleTemplate> ruleTemplates = wtaBuilderService.copyRuleTemplates(wtadto.getRuleTemplates(), true);
                    ruleTemplates.forEach(wtaBaseRuleTemplate -> {
                        wtaBaseRuleTemplate.setCountryId(null);
                    });
                    save(ruleTemplates);
                    List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
                    workingTimeAgreement.setRuleTemplateIds(ruleTemplatesIds);
                }
                workingTimeAgreement.setCountryParentWTA(wta.getId());
                workingTimeAgreement.setOrganization(new WTAOrganization(organization.getId(), organization.getName(), organization.getDescription()));
                workingTimeAgreements.add(workingTimeAgreement);
            }
        });
        if (!workingTimeAgreements.isEmpty()) {
            save(workingTimeAgreements);
        }
    }

    // @Async
    /*private void assignUpdatedWTAToOrganization(WorkingTimeAgreement wta, Long organizationSubTypeId, Long oldWTAId) {
        List<Organization> organizations = organizationTypeRepository.getOrganizationsByOrganizationType(organizationSubTypeId);
        organizations.forEach(organization ->
        {
            if (!organization.isKairosHub()) {
                WorkingTimeAgreement previousWTACopyOfOrganization = wtaRepository.getOrganizationCopyOfWTA(organization.getId(), oldWTAId);
                WorkingTimeAgreement workingTimeAgreement = new WorkingTimeAgreement();
                copyWta(wta, workingTimeAgreement);
                workingTimeAgreement.setCountryParentWTA(wta);
                workingTimeAgreement.setParentWTA(previousWTACopyOfOrganization);
                // INITIALLY disabled this as when organization wish to use It will make active.
                workingTimeAgreement.setDisabled(true);
                List<WTABaseRuleTemplateDTO> ruleTemplates = new ArrayList<>();
                if (wta.getRuleTemplateIds().size() > 0) {
                    ruleTemplates = copyRuleTemplates(wta.getRuleTemplateIds());
                    workingTimeAgreement.setRuleTemplateIds(ruleTemplates);
                }
                organization.getWorkingTimeAgreements().add(workingTimeAgreement);
                save(organization);
            }
        });
    }
*/
    /*private void checkUniquenessOfData(long countryId, long organizationSubTypeId, long organizationTypeId, long expertiseId) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfData(organizationSubTypeId, organizationTypeId, expertiseId, countryId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new InvalidRequestException("WTA combination of expertise ,organization type and Sub type already exist.");

        }
        return;
    }*/

    private WorkingTimeAgreement prepareWtaWhileCreate(WorkingTimeAgreement wta, WTADTO wtaDTO, WTABasicDetailsDTO wtaBasicDetailsDTO) {
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getExpertiseResponse()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid expertiseId " + wtaDTO.getExpertiseId());
        }
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationType()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization type " + wtaDTO.getOrganizationType());
        }
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationSubType()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization sub type " + wtaDTO.getOrganizationSubType());
        }
        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());
        wta.setExpertise(new WTAExpertise(wtaBasicDetailsDTO.getExpertiseResponse().getId(), wtaBasicDetailsDTO.getExpertiseResponse().getName(), wtaBasicDetailsDTO.getExpertiseResponse().getDescription()));
        wta.setOrganizationType(new WTAOrganizationType(wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationType().getName(), wtaBasicDetailsDTO.getOrganizationType().getDescription()));
        wta.setOrganizationSubType(new WTAOrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));


        return wta;
    }

   /* private void checkUniquenessOfDataExcludingCurrent(long countryId, long wtaId, WTADTO wtaDTO) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfDataExcludingCurrent(wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId(), countryId, wtaId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new InvalidRequestException("WTA combination of exp,org,level,region already exist.");

        }
        return;
    }*/

    // FOR COUNTRY
    public WTAResponseDTO updateWtaOfCountry(Long countryId, BigInteger wtaId, WTADTO updateDTO) {

        if (updateDTO.getStartDateMillis() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date cant be less than current Date " + wtaId);
        }
        WorkingTimeAgreement workingTimeAgreement = wtaRepository.getWtaByNameExcludingCurrent(updateDTO.getName(), countryId, wtaId, updateDTO.getOrganizationType(), updateDTO.getOrganizationSubType());
        if (Optional.ofNullable(workingTimeAgreement).isPresent()) {
            throw new DuplicateDataException("Duplicate WTA name " + updateDTO.getName());
        }
        WorkingTimeAgreement oldWta = wtaRepository.getWTAByCountryId(countryId, wtaId);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            throw new DataNotFoundByIdException(" Invalid wtaId  " + wtaId);
        }
        WTABasicDetailsDTO wtaBasicDetailsDTO = wtaDetailRestClient.getWtaRelatedInfo(updateDTO.getExpertiseId(), updateDTO.getOrganizationSubType(), countryId, 0l, updateDTO.getOrganizationType());
        oldWta = prepareWtaWhileUpdate(oldWta, updateDTO, wtaBasicDetailsDTO);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(updateDTO.getRuleTemplates(), true);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            oldWta.setRuleTemplateIds(ruleTemplatesIds);
        }
        save(oldWta);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        wtaResponseDTO.setStartDateMillis(oldWta.getStartDate().getTime());
        if (oldWta.getEndDate() != null) {
            wtaResponseDTO.setEndDateMillis(oldWta.getEndDate().getTime());
        }
        //wtaResponseDTO.setRuleTemplateIds( wtaBuilderService.getRuleTemplateDTO(oldWta));
        //WTAQueryResultDTO parentWTA = wtaRepository.getOne(oldWta.getParentWTA());
        //WTAResponseDTO PrentDTO = ObjectMapperUtils.copyPropertiesByMapper(parentWTA,WTAResponseDTO.class);
        //wtaResponseDTO.setParentWTA(PrentDTO);
        //assignUpdatedWTAToOrganization(newWta, updateDTO.getOrganizationSubType(), oldWta.getId());
//        oldWta.setCountryId(null);
//        oldWta.setOrganizationType(null);
//        oldWta.setOrganizationSubType(null);
//        save(oldWta);

        return wtaResponseDTO;
    }


    private WorkingTimeAgreement prepareWtaWhileUpdate(WorkingTimeAgreement oldWta, WTADTO updateDTO, WTABasicDetailsDTO wtaBasicDetailsDTO) {
        if (!oldWta.getOrganizationType().getId().equals(updateDTO.getOrganizationType())) {
            throw new ActionNotPermittedException("Organization  type cant be changed" + updateDTO.getOrganizationType());
        }
        if (!oldWta.getOrganizationSubType().getId().equals(updateDTO.getOrganizationSubType())) {
            throw new ActionNotPermittedException("Organization Sub type cant be changed" + updateDTO.getOrganizationSubType());
        }
        WorkingTimeAgreement versionWTA = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WorkingTimeAgreement.class);
        versionWTA.setId(null);
        versionWTA.setDeleted(true);
        versionWTA.setStartDate(oldWta.getStartDate());
        versionWTA.setEndDate(new Date(updateDTO.getStartDateMillis()));
        save(versionWTA);
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());

        oldWta.setStartDate(new Date(updateDTO.getStartDateMillis()));
        if (oldWta.getEndDate() != null) {
            oldWta.setEndDate(new Date(updateDTO.getEndDateMillis()));
        }

        if (!oldWta.getExpertise().getId().equals(updateDTO.getExpertiseId())) {
            if (!Optional.ofNullable(wtaBasicDetailsDTO.getExpertiseResponse()).isPresent()) {
                throw new DataNotFoundByIdException("Expertize not found by Id" + updateDTO.getExpertiseId());
            }
            oldWta.setExpertise(new WTAExpertise(wtaBasicDetailsDTO.getExpertiseResponse().getId(), wtaBasicDetailsDTO.getExpertiseResponse().getName(), wtaBasicDetailsDTO.getExpertiseResponse().getDescription()));
        }
        oldWta.setOrganizationType(new WTAOrganizationType(wtaBasicDetailsDTO.getOrganizationType().getId(), wtaBasicDetailsDTO.getOrganizationType().getName(), wtaBasicDetailsDTO.getOrganizationType().getDescription()));
        oldWta.setOrganizationSubType(new WTAOrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));

        //versionWTA.setOrganizationSubType(oldWta.getOrganizationSubType());
        oldWta.setParentWTA(versionWTA.getId());
        return oldWta;

    }

    public WTAResponseDTO getWta(BigInteger wtaId) {
        return wtaRepository.getVersionOfWTA(wtaId);
    }

    public boolean removeWta(BigInteger wtaId) {
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }
        wta.setDeleted(true);
        save(wta);
        return true;
    }

    public List<WTAResponseDTO> getAllWTAByOrganizationId(long organizationId) {
        return wtaRepository.getAllWTAByOrganizationTypeId(organizationId);
    }

    public List<WTAResponseDTO> getAllWTAByCountryId(long countryId) {
        List<WTAResponseDTO> wtaResponseDTOS = wtaRepository.getAllWTAByCountryId(countryId);
        wtaResponseDTOS.forEach(wtaResponseDTO -> {
            wtaResponseDTO.setStartDateMillis(wtaResponseDTO.getStartDate().getTime());
            if (wtaResponseDTO.getEndDate() != null) {
                wtaResponseDTO.setEndDateMillis(wtaResponseDTO.getEndDate().getTime());
            }
        });
        return wtaResponseDTOS;
    }

    public List<WTAResponseDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId) {
        return wtaRepository.getAllWTAByOrganizationSubType(organizationSubTypeId);
    }

    public List<WTAResponseDTO> getAllWTAWithOrganization(long countryId) {
        /*List<Map<String, Object>> map =
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }*/
        return wtaRepository.getAllWTAWithOrganization(countryId);
    }

    public List<WTAResponseDTO> getAllWTAWithWTAId(long countryId, BigInteger wtaId) {
        /*List<Map<String, Object>> map =
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }*/
        return wtaRepository.getAllWTAWithWTAId(countryId, wtaId);
    }

    /*public List<ExpertiseDTO> getAllAvailableExpertise(Long organizationSubTypeId, Long countryId) {
        ExpertiseIdListDTO map = wtaRepository.getAvailableAndFreeExpertise(countryId, organizationSubTypeId);
        List<Long> linkedExpertiseIds = map.getLinkedExpertise();
        List<Long> allExpertiseIds = map.getAllExpertiseIds();
        allExpertiseIds.removeAll(linkedExpertiseIds);
        List<ExpertiseDTO> expertiseDTOS = new ArrayList<ExpertiseDTO>();
        expertiseDTOS = expertiseService.getAllFreeExpertise(allExpertiseIds);
        return expertiseDTOS;

    }*/

    public Map<String, Object> setWtaWithOrganizationType(Long countryId, BigInteger wtaId, long organizationSubTypeId, boolean checked) {
        Map<String, Object> map = new HashMap<>();
        /*List<WTARuleTemplateDTO> wtaRuleTemplateQueryResponseArrayList = new ArrayList<WTARuleTemplateDTO>();*/
        WTABasicDetailsDTO wtaBasicDetailsDTO = wtaDetailRestClient.getWtaRelatedInfo(null, organizationSubTypeId, countryId, null, null);
        if (!Optional.ofNullable(wtaBasicDetailsDTO.getOrganizationSubType()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organisation Sub type Id " + organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        //TODO need to again activate check
        //checkUniquenessOfData(countryId, organizationSubTypeId, wta.getOrganizationType().getId(), wta.getExpertise().getId());
        if (!Optional.ofNullable(wta).isPresent()) {
            throw new DataNotFoundByIdException("wta not found " + wtaId);
        }
        if (checked) {
            WorkingTimeAgreement newWtaObject = new WorkingTimeAgreement();
            wtaBuilderService.copyWta(wta, newWtaObject);

            newWtaObject.setCountryId(wta.getCountryId());
            newWtaObject.setOrganizationType(wta.getOrganizationType());

            newWtaObject.setOrganizationSubType(new WTAOrganizationType(wtaBasicDetailsDTO.getOrganizationSubType().getId(), wtaBasicDetailsDTO.getOrganizationSubType().getName(), wtaBasicDetailsDTO.getOrganizationSubType().getDescription()));
            wtaBuilderService.copyRuleTemplateToNewWTA(wta, newWtaObject);
            save(newWtaObject);
            WTAResponseDTO wtaResponseDTO = wtaRepository.getOne(wta.getId());
            newWtaObject.setCountryId(null);
            map.put("wta", newWtaObject);
            map.put("ruleTemplate", wtaResponseDTO.getRuleTemplates());
        } else {
            wta.setDeleted(true);
            save(wta);
        }
        return map;

    }

   /* public List<WTABaseRuleTemplateDTO> copyRuleTemplates(List<WTABaseRuleTemplateDTO> ruleTemplates) {
        List<WTABaseRuleTemplateDTO> copiedRuleTemplate = new ArrayList<>(ruleTemplates.size());
        ruleTemplates.forEach(ruleTemplate -> {
            ObjectMapper objectMapper = new ObjectMapper();
            WTABaseRuleTemplateDTO wtaBaseRuleTemplate = objectMapper.convertValue(ruleTemplate, WTABaseRuleTemplateDTO.class);
            wtaBaseRuleTemplate.setRuleTemplateCategory(ruleTemplate.getRuleTemplateCategory());
            wtaBaseRuleTemplate.setId(null);

            if (Optional.ofNullable(wtaBaseRuleTemplate.getPhaseTemplateValues()).isPresent()) {
                wtaBaseRuleTemplate.getPhaseTemplateValues().forEach(PhaseTemplateValue -> {
                    PhaseTemplateValue.setId(null);
                });
            }

            copiedRuleTemplate.add(wtaBaseRuleTemplate);
        });
        return copiedRuleTemplate;
    }*/


   /* public List<WTARuleTemplateDTO> retrieveRuleTemplateResponse(List<WTABaseRuleTemplateDTO> ruleTemplates) {
        List<WTARuleTemplateDTO> copiedRuleTemplate = new ArrayList<>(ruleTemplates.size());
        ObjectMapper objectMapper = new ObjectMapper();
        ruleTemplates.forEach(ruleTemplate -> {
            WTARuleTemplateDTO wtaBaseRuleTemplateDTO = objectMapper.convertValue(ruleTemplate, WTARuleTemplateDTO.class);
            ruleTemplate.setId(null);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplate.getRuleTemplateCategory());

            copiedRuleTemplate.add(wtaBaseRuleTemplateDTO);
        });
        return copiedRuleTemplate;
    }*/



    /*public WorkingTimeAgreement copyWta(WorkingTimeAgreement oldWta, WTADTO updatedWta) {
        WorkingTimeAgreement newWta = new WorkingTimeAgreement();
        newWta.setName(updatedWta.getName());
        newWta.setDescription(updatedWta.getDescription());
        if (updatedWta.getStartDate() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date cant be less than current Date " + oldWta.getId());
        }
        newWta.setStartDate(updatedWta.getStartDate());
        newWta.setEndDate(updatedWta.getEndDate());
        newWta.setId(null);
        List<WTABaseRuleTemplateDTO> ruleTemplates = new ArrayList<>();
        if (updatedWta.getRuleTemplateIds().size() > 0) {
            WTAQueryResultDTO wtaQueryResultDTO = new WTAQueryResultDTO();
            wtaBuilderService.copyRuleTemplates(wtaQueryResultDTO,updatedWta.getRuleTemplateIds());
            wtaBuilderService.copyWTARuleTemplateToWTA(newWta,wtaQueryResultDTO);
        }
        return newWta;

    }*/

    public WTAResponseDTO updateWtaOfUnitPosition(Long unitId, WTADTO wtadto) {
        WorkingTimeAgreement oldWta = wtaRepository.findOne(wtadto.getId());
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating unit Employment Position for staff %d");
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtadto.getId());
        }

        WorkingTimeAgreement newWta = new WorkingTimeAgreement();

        if (oldWta.getExpertise().getId() != wtadto.getExpertiseId()) {
            logger.info("Expertise cant be changed :", wtadto.getId());
            throw new ActionNotPermittedException("Expertise can't be changed");
        }
        BeanUtils.copyProperties(oldWta, newWta, "id");
        oldWta.setEndDate(new Date(wtadto.getEndDateMillis()));
        save(oldWta);
        newWta.setParentWTA(oldWta.getId());
        newWta.setDisabled(false);
        newWta.setParentWTA(oldWta.getId());
        save(newWta);
        //WorkingTimeAgreement workingTimeAgreement = wtaRepository.findOne(newWta.getId());
        WTAResponseDTO wtaResponseDTO = new WTAResponseDTO();
        //BeanUtils.copyProperties(workingTimeAgreement,wtaResponseDTO);
        //WTAResponseDTO parentWta = new WTAResponseDTO();
        //BeanUtils.copyProperties(oldWta,parentWta);
        //wtaResponseDTO.setParentWTA(parentWta);
        return wtaResponseDTO;
    }

    public WTADefaultDataInfoDTO getDefaultWtaInfo(Long countryId) {
        List<Activity> activities = activityMongoRepository.findByDeletedFalseAndUnitId(countryId);
        List<ActivityDTO> activityDTOS = new ArrayList<>();
        activities.forEach(a -> {
            ActivityDTO activityDTO = new ActivityDTO();
            try {
                PropertyUtils.copyProperties(activityDTO, a);
                activityDTOS.add(activityDTO);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, countryId);
        WTADefaultDataInfoDTO wtaDefaultDataInfoDTO = wtaDetailRestClient.getWtaTemplateDefaultDataInfo(countryId);
        wtaDefaultDataInfoDTO.setTimeTypes(timeTypeDTOS);
        wtaDefaultDataInfoDTO.setActivityList(activityDTOS);
        return wtaDefaultDataInfoDTO;
    }

    public List<WTAResponseDTO> getWTAByIds(List<BigInteger> wtaIds){
        return wtaRepository.getAllWTAByIds(wtaIds);
    }

    public WTAResponseDTO assignWTAToUnitPosition(BigInteger wtaId){
        WTAResponseDTO wtaResponseDTO = wtaRepository.getOne(wtaId);
        if (!Optional.ofNullable(wtaResponseDTO).isPresent()) {
            throw new DuplicateDataException("WTA doesn't exist" + wtaId);
        }
        WorkingTimeAgreement workingTimeAgreement = ObjectMapperUtils.copyPropertiesByMapper(wtaResponseDTO,WorkingTimeAgreement.class);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (wtaResponseDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(wtaResponseDTO.getRuleTemplates(), true);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList());
            workingTimeAgreement.setRuleTemplateIds(ruleTemplatesIds);
        }
        workingTimeAgreement.setOrganization(null);
        workingTimeAgreement.setOrganizationParentWTA(wtaResponseDTO.getId());
        workingTimeAgreement.setParentWTA(wtaResponseDTO.getId());
        save(workingTimeAgreement);
        wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(workingTimeAgreement,WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        return wtaResponseDTO;

    }


}
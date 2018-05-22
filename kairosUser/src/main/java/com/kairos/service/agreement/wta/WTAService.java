package com.kairos.service.agreement.wta;

import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by pawanmandhan on 2/8/17.
 */

@Transactional
@Service
public class WTAService extends UserBaseService {
    /*@Inject
    private WorkingTimeAgreementGraphRepository wtaRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeRepository;
    @Inject
    private CountryGraphRepository countryRepository;
    @Inject
    private ExpertiseGraphRepository expertiseRepository;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryRepository;
    @Inject
    private RegionGraphRepository regionRepository;
    @Inject
    private WTABaseRuleTemplateGraphRepository wtaBaseRuleTemplateGraphRepository;
    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private RuleTemplateService ruleTemplateService;
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private TagService tagService;
    @Inject
    private WTAOrganizationService wtaOrganizationService;


    private final Logger logger = LoggerFactory.getLogger(WTAService.class);

    *//**
     * @param countryId
     * @param wtaDTO
     * @return
     * @Author Vipul
     *//*
    public WTAResponseDTO createWta(long countryId, WTADTO wtaDTO) {
        Country country = countryRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country id " + countryId);
        }
        //TODO  API functionality has been changed for now KP-958
        //  checkUniquenessOfData(countryId, wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId());
        WorkingTimeAgreement wta = wtaRepository.getWtaByName("(?i)" + wtaDTO.getName(), countryId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new DuplicateDataException("Duplicate WTA name" + wtaDTO.getName());
        }
        wta = new WorkingTimeAgreement();
        // Link tags to WTA
        if (wtaDTO.getTags().size() > 0) {
            List<Tag> tags = tagService.getCountryTagsByIdsAndMasterDataType(wtaDTO.getTags(), MasterDataTypeEnum.WTA);
            wta.setTags(tags);
        }

        prepareWtaWhileCreate(wta, countryId, wtaDTO);
        wta.setCountry(country);
        save(wta);
        assignWTAToOrganization(wta, wtaDTO);
        WTAResponseDTO wtaResponseDTO = wta.retriveBasicResponse();
        wtaResponseDTO.setRuleTemplates(retrieveRuleTemplateResponse(wta.getRuleTemplates()));
        wtaResponseDTO.setId(wta.getId());
        // Adding this wta to all organization type


        // setting basic details

        return wtaResponseDTO;
    }


    private void assignWTAToOrganization(WorkingTimeAgreement wta, WTADTO wtadto) {
        List<Organization> organizations = organizationTypeRepository.getOrganizationsByOrganizationType(wtadto.getOrganizationSubType());
        organizations.forEach(organization ->
        {
            if (!organization.isKairosHub()) {
                WorkingTimeAgreement workingTimeAgreement = new WorkingTimeAgreement();
                copyWta(wta, workingTimeAgreement);
                workingTimeAgreement.setCountryParentWTA(wta);
                workingTimeAgreement.setDisabled(false);
                List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();

                if (wtadto.getRuleTemplates().size() > 0) {
                    ruleTemplates = wtaOrganizationService.copyRuleTemplates(null, wtadto.getRuleTemplates());
                }
                workingTimeAgreement.setRuleTemplates(ruleTemplates);
                organization.getWorkingTimeAgreements().add(workingTimeAgreement);
                save(organization);
            }
        });
    }

    // @Async
    private void assignUpdatedWTAToOrganization(WorkingTimeAgreement wta, Long organizationSubTypeId, Long oldWTAId) {
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
                List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
                if (wta.getRuleTemplates().size() > 0) {
                    ruleTemplates = copyRuleTemplate(wta.getRuleTemplates());
                    workingTimeAgreement.setRuleTemplates(ruleTemplates);
                }
                organization.getWorkingTimeAgreements().add(workingTimeAgreement);
                save(organization);
            }
        });
    }

    private void checkUniquenessOfData(long countryId, long organizationSubTypeId, long organizationTypeId, long expertiseId) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfData(organizationSubTypeId, organizationTypeId, expertiseId, countryId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new InvalidRequestException("WTA combination of expertise ,organization type and Sub type already exist.");

        }
        return;
    }

    private WorkingTimeAgreement prepareWtaWhileCreate(WorkingTimeAgreement wta, long countryId, WTADTO wtaDTO) {

        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());


        Expertise expertise = expertiseRepository.findOne(((long) wtaDTO.getExpertiseId()));
        if (!Optional.ofNullable(expertise).isPresent()) {
            throw new DataNotFoundByIdException("Invalid expertiseId " + wtaDTO.getExpertiseId());
        }
        wta.setExpertise(expertise);

        OrganizationType organizationType = organizationTypeRepository.findOne(wtaDTO.getOrganizationType());
        if (!Optional.ofNullable(organizationType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization type " + wtaDTO.getOrganizationType());
        }
        wta.setOrganizationType(organizationType);

        OrganizationType organizationSubType = organizationTypeRepository.findOne(wtaDTO.getOrganizationSubType());
        if (!Optional.ofNullable(organizationSubType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization sub type " + wtaDTO.getOrganizationSubType());
        }


        wta.setOrganizationSubType(organizationSubType);

        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (wtaDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaOrganizationService.copyRuleTemplates(null, wtaDTO.getRuleTemplates());
        }
        wta.setRuleTemplates(ruleTemplates);
        Long dateInMillies = (wtaDTO.getStartDate() == 0) ? DateUtil.getCurrentDate().getTime() : wtaDTO.getStartDate();
        wta.setStartDate(dateInMillies);

        if (wtaDTO.getEndDate() != null && wtaDTO.getEndDate() > 0) {
            if (dateInMillies > wtaDTO.getEndDate()) {
                throw new InvalidRequestException("End Date must not be greater than start date");
            }
            wta.setEndDate(wtaDTO.getEndDate());
        }

        return wta;
    }

    private void checkUniquenessOfDataExcludingCurrent(long countryId, long wtaId, WTADTO wtaDTO) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfDataExcludingCurrent(wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId(), countryId, wtaId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new InvalidRequestException("WTA combination of exp,org,level,region already exist.");

        }
        return;
    }

    // FOR COUNTRY
    public WTAResponseDTO updateWtaOfCountry(Long countryId, Long wtaId, WTADTO updateDTO) {
        WorkingTimeAgreement oldWta = wtaRepository.findOne(wtaId, 2);
        if (!Optional.ofNullable(oldWta).isPresent() || oldWta.getCountry() == null) {
            logger.info("wta not found while updating at unit %d", wtaId);
            throw new DataNotFoundByIdException(" Invalid wtaId  " + wtaId);
        }
        // TODO may be again changed in future.

        WorkingTimeAgreement workingTimeAgreement = wtaRepository.getWtaByNameExcludingCurrent("(?i)" + updateDTO.getName(), countryId, wtaId, updateDTO.getOrganizationType(), updateDTO.getOrganizationSubType());
        if (Optional.ofNullable(workingTimeAgreement).isPresent()) {
            throw new DuplicateDataException("Duplicate WTA name " + updateDTO.getName());
        }
        oldWta = prepareWtaWhileUpdate(oldWta, updateDTO, countryId);

        save(oldWta);
        WTAResponseDTO wtaResponseDTO = oldWta.retriveBasicResponse();

        wtaResponseDTO.setRuleTemplates(retrieveRuleTemplateResponse(oldWta.getRuleTemplates()));
        wtaResponseDTO.setParentWTA(oldWta.getParentWTA().retriveBasicResponse());
        //assignUpdatedWTAToOrganization(newWta, updateDTO.getOrganizationSubType(), oldWta.getId());
//        oldWta.setCountry(null);
//        oldWta.setOrganizationType(null);
//        oldWta.setOrganizationSubType(null);
//        save(oldWta);

        return wtaResponseDTO;
    }

    private WorkingTimeAgreement prepareWtaWhileUpdate(WorkingTimeAgreement oldWta, WTADTO updateDTO, Long countryId) {
        if (updateDTO.getStartDate() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date cant be less than current Date " + oldWta.getId());
        }
        WorkingTimeAgreement versionWTA = new WorkingTimeAgreement();

        BeanUtils.copyProperties(oldWta, versionWTA);
        versionWTA.setId(null);
        versionWTA.setDeleted(true);
        versionWTA.getRuleTemplates().forEach(ruleTemplate -> {
            ruleTemplate.setId(null);
            if (Optional.ofNullable(ruleTemplate.getPhaseTemplateValues()).isPresent()) {
                ruleTemplate.getPhaseTemplateValues().forEach(phaseTemplateValue -> {
                    phaseTemplateValue.setId(null);
                });
            }
        });
        versionWTA.setStartDate(oldWta.getStartDate());
        versionWTA.setEndDate(updateDTO.getStartDate());
        save(versionWTA);


        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());

        oldWta.setStartDate(updateDTO.getStartDate());
        if (oldWta.getEndDate() != null) {
            oldWta.setEndDate(updateDTO.getEndDate());
        }


        if (!oldWta.getExpertise().getId().equals(updateDTO.getExpertiseId())) {
            Expertise expertise = expertiseRepository.findOne(updateDTO.getExpertiseId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Expertize not found by Id" + updateDTO.getExpertiseId());
            }
            oldWta.setExpertise(expertise);
        } else {
            oldWta.setExpertise(oldWta.getExpertise());
        }

        if (!oldWta.getOrganizationType().getId().equals(updateDTO.getOrganizationType())) {
            throw new ActionNotPermittedException("Organization  type cant be changed" + updateDTO.getOrganizationType());
        }
        versionWTA.setOrganizationType(oldWta.getOrganizationType());
        if (!oldWta.getOrganizationSubType().getId().equals(updateDTO.getOrganizationSubType())) {
            throw new ActionNotPermittedException("Organization Sub type cant be changed" + updateDTO.getOrganizationSubType());
        }
        versionWTA.setOrganizationSubType(oldWta.getOrganizationSubType());
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaOrganizationService.copyRuleTemplates(null, updateDTO.getRuleTemplates());
            oldWta.setRuleTemplates(ruleTemplates);
        }
        oldWta.setParentWTA(versionWTA);
        return oldWta;

    }

    public WTAResponseDTO getWta(long wtaId) {
        return wtaRepository.getVersionOfWTA(wtaId);
    }

    public boolean removeWta(long wtaId) {
        WorkingTimeAgreement wta = wtaRepository.getWta(wtaId);
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
        return wtaRepository.getAllWTAByCountryId(countryId);
    }

    public List<WTAResponseDTO> getAllWTAByOrganizationSubType(long organizationSubTypeId) {
        return wtaRepository.getAllWTAByOrganizationSubType(organizationSubTypeId);
    }

    public List<Object> getAllWTAWithOrganization(long countryId) {
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithOrganization(countryId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public List<Object> getAllWTAWithWTAId(long countryId, long wtaId) {
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithWTAId(countryId, wtaId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public List<ExpertiseDTO> getAllAvailableExpertise(Long organizationSubTypeId, Long countryId) {
        ExpertiseIdListDTO map = wtaRepository.getAvailableAndFreeExpertise(countryId, organizationSubTypeId);
        List<Long> linkedExpertiseIds = map.getLinkedExpertise();
        List<Long> allExpertiseIds = map.getAllExpertiseIds();
        allExpertiseIds.removeAll(linkedExpertiseIds);
        List<ExpertiseDTO> expertiseDTOS = new ArrayList<ExpertiseDTO>();
        expertiseDTOS = expertiseService.getAllFreeExpertise(allExpertiseIds);
        return expertiseDTOS;

    }

    public Map<String, Object> setWtaWithOrganizationType(Long countryId, long wtaId, long organizationSubTypeId, boolean checked) {
        Map<String, Object> map = new HashMap<>();
        List<RuleTemplateCategoryDTO> wtaRuleTemplateQueryResponseArrayList = new ArrayList<RuleTemplateCategoryDTO>();

        OrganizationType organizationSubType = organizationTypeRepository.findOne(organizationSubTypeId);
        if (!Optional.ofNullable(organizationSubType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organisation Sub type Id " + organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId, 2);
        //TODO need to again activate check
        //checkUniquenessOfData(countryId, organizationSubTypeId, wta.getOrganizationType().getId(), wta.getParentExpertise().getId());
        if (!Optional.ofNullable(wta).isPresent()) {
            throw new DataNotFoundByIdException("wta not found " + wtaId);
        }
        if (checked) {
            WorkingTimeAgreement newWtaObject = new WorkingTimeAgreement();
            copyWta(wta, newWtaObject);

            newWtaObject.setCountry(wta.getCountry());
            newWtaObject.setOrganizationType(wta.getOrganizationType());

            newWtaObject.setOrganizationSubType(organizationSubType);

            List<WTABaseRuleTemplate> ruleTemplateWithCategory = copyRuleTemplate(wta.getRuleTemplates());
            newWtaObject.setRuleTemplates(ruleTemplateWithCategory);

            save(newWtaObject);
            // TODO
            // assignWTAToOrganization(newWtaObject, organizationSubTypeId);
            // setting basic propery for response
            newWtaObject.setOrganizationType(newWtaObject.getOrganizationType().basicDetails());
            newWtaObject.setOrganizationSubType(newWtaObject.getOrganizationSubType().basicDetails());
            newWtaObject.setExpertise(newWtaObject.getExpertise().retrieveBasicDetails());
            newWtaObject.setCountry(null);
            map.put("wta", newWtaObject);
            map.put("ruleTemplate", ruleTemplateWithCategory);
        } else {
            wta.setDeleted(true);
            save(wta);
        }
        return map;

    }

    public List<WTABaseRuleTemplate> copyRuleTemplate(List<WTABaseRuleTemplate> ruleTemplates) {
        List<WTABaseRuleTemplate> copiedRuleTemplate = new ArrayList<>(ruleTemplates.size());
        ruleTemplates.forEach(ruleTemplate -> {
            ObjectMapper objectMapper = new ObjectMapper();
            WTABaseRuleTemplate wtaBaseRuleTemplate = objectMapper.convertValue(ruleTemplate, WTABaseRuleTemplate.class);
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
    }


    public List<RuleTemplateCategoryDTO> retrieveRuleTemplateResponse(List<WTABaseRuleTemplate> ruleTemplates) {
        List<RuleTemplateCategoryDTO> copiedRuleTemplate = new ArrayList<>(ruleTemplates.size());
        ObjectMapper objectMapper = new ObjectMapper();
        ruleTemplates.forEach(ruleTemplate -> {
            RuleTemplateCategoryDTO wtaBaseRuleTemplateDTO = objectMapper.convertValue(ruleTemplate, RuleTemplateCategoryDTO.class);
            ruleTemplate.setId(null);
            wtaBaseRuleTemplateDTO.setRuleTemplateCategory(ruleTemplate.getRuleTemplateCategory());

            copiedRuleTemplate.add(wtaBaseRuleTemplateDTO);
        });
        return copiedRuleTemplate;
    }

    public WorkingTimeAgreement copyWta(WorkingTimeAgreement oldWta, WorkingTimeAgreement newWta) {
        newWta.setName(COPY_OF + oldWta.getName());
        newWta.setDescription(oldWta.getDescription());
        newWta.setStartDate(oldWta.getStartDate());
        newWta.setEndDate(oldWta.getEndDate());
        newWta.setExpertise(oldWta.getExpertise());
        newWta.setId(null);
        return newWta;

    }

    public WorkingTimeAgreement copyWta(WorkingTimeAgreement oldWta, WTADTO updatedWta) {
        WorkingTimeAgreement newWta = new WorkingTimeAgreement();
        newWta.setName(updatedWta.getName());
        newWta.setDescription(updatedWta.getDescription());
        if (updatedWta.getStartDate() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date cant be less than current Date " + oldWta.getId());
        }
        newWta.setStartDate(updatedWta.getStartDate());
        newWta.setEndDate(updatedWta.getEndDate());
        newWta.setId(null);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (updatedWta.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaOrganizationService.copyRuleTemplates(oldWta.getRuleTemplates(), updatedWta.getRuleTemplates());
            newWta.setRuleTemplates(ruleTemplates);
        }
        return newWta;

    }*/
}
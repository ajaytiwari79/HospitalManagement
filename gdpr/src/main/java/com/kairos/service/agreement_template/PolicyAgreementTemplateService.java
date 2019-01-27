package com.kairos.service.agreement_template;


import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.agreement_template.AgreementTemplateDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationTypeAndSubTypeIdDTO;
import com.kairos.dto.gdpr.agreement_template.AgreementTemplateClauseUpdateDTO;
import com.kairos.dto.gdpr.agreement_template.MasterAgreementTemplateDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.dto.gdpr.agreement_template.CoverPageVO;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplateMD;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.model.clause.ClauseCkEditorVO;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.template_type.TemplateTypeMD;
import com.kairos.persistence.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementRepository;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistence.repository.clause.ClauseMongoRepository;
import com.kairos.persistence.repository.clause.ClauseRepository;
import com.kairos.persistence.repository.template_type.TemplateTypeRepository;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import com.kairos.response.dto.clause.UnitLevelClauseResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateBasicResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.s3bucket.AWSBucketService;
import com.kairos.service.template_type.TemplateTypeService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PolicyAgreementTemplateService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAgreementTemplateService.class);

    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;

    @Inject
    private PolicyAgreementRepository policyAgreementRepository;

    @Inject
    private ClauseRepository clauseRepository;


    @Inject
    private AgreementSectionService agreementSectionService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TemplateTypeService templateTypeService;

    @Inject
    private ClauseMongoRepository clauseMongoRepository;

    @Inject
    private AgreementSectionMongoRepository agreementSectionMongoRepository;

    @Inject
    private AWSBucketService awsBucketService;

    @Inject
    private TemplateTypeRepository templateTypeRepository;

    @Inject
    private GenericRestClient genericRestClient;


    /**
     * @param referenceId                - countryId or unitId
     * @param -                          isUnitId boolean to check whether referenceId id country id or unit id
     * @param policyAgreementTemplateDto
     * @return return object of basic policy agreement template.
     * @description this method creates a basic policy Agreement template with basic detail about organization type,
     * organizationSubTypes ,service Category and sub service Category.
     */
    public <E extends AgreementTemplateDTO> E saveAgreementTemplate(Long referenceId, boolean isUnitId, E policyAgreementTemplateDto) {

        PolicyAgreementTemplateMD previousTemplate = isUnitId ? policyAgreementRepository.findByOrganizationIdAndDeletedAndName(referenceId, false,  policyAgreementTemplateDto.getName())
                : policyAgreementRepository.findByCountryIdAndDeletedAndName(referenceId, false, policyAgreementTemplateDto.getName());
        if (Optional.ofNullable(previousTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.policy.agreementTemplate", policyAgreementTemplateDto.getName());
        }
        PolicyAgreementTemplateMD policyAgreementTemplate = buildAgreementTemplate(referenceId, isUnitId, policyAgreementTemplateDto);
        policyAgreementRepository.save(policyAgreementTemplate);
        policyAgreementTemplateDto.setId(policyAgreementTemplate.getId());
        return policyAgreementTemplateDto;

    }

    private <E extends AgreementTemplateDTO> PolicyAgreementTemplateMD buildAgreementTemplate(Long referenceId, boolean isUnitId, E policyAgreementTemplateDto) {

        PolicyAgreementTemplateMD policyAgreementTemplate = new PolicyAgreementTemplateMD(policyAgreementTemplateDto.getName(), policyAgreementTemplateDto.getDescription(), templateTypeRepository.getOne(policyAgreementTemplateDto.getTemplateTypeId()));
        if (isUnitId) {
            policyAgreementTemplate.setOrganizationId(referenceId);
        } else {
            MasterAgreementTemplateDTO agreementTemplateDTO = (MasterAgreementTemplateDTO) policyAgreementTemplateDto;
            policyAgreementTemplate.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getOrganizationTypes(), OrganizationType.class));
            policyAgreementTemplate.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getOrganizationSubTypes(), OrganizationSubType.class));
            policyAgreementTemplate.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getOrganizationServices(), ServiceCategory.class));
            policyAgreementTemplate.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getOrganizationSubServices(), SubServiceCategory.class));
            policyAgreementTemplate.setAccountTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getAccountTypes(), AccountType.class));
            policyAgreementTemplate.setCountryId(referenceId);
        }
        return policyAgreementTemplate;
    }


    /**
     * @param referenceId         - country Id or unitId
     * @param -                   isUnitId boolean to check whether referenceId id country id or unit id
     * @param agreementTemplateId - Agreement Template id
     * @param coverPageLogo       - Agreement Cover page
     * @return -Url of image uploaded at S3 bucket
     */
    public String uploadCoverPageLogo(Long referenceId, boolean isUnitId, BigInteger agreementTemplateId, MultipartFile coverPageLogo) {

        PolicyAgreementTemplate policyAgreementTemplate = isUnitId ? policyAgreementTemplateRepository.findByUnitIdAndId(referenceId, agreementTemplateId) : policyAgreementTemplateRepository.findByCountryIdAndId(referenceId, agreementTemplateId);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.policy.agreementTemplate", agreementTemplateId);
        }
        String coverPageLogoUrl = awsBucketService.uploadImage(coverPageLogo);
        if (policyAgreementTemplate.isCoverPageAdded()) {
            policyAgreementTemplate.getCoverPageData().setCoverPageLogoUrl(coverPageLogoUrl);
        } else {
            policyAgreementTemplate.setCoverPageData(new CoverPageVO(coverPageLogoUrl));
            policyAgreementTemplate.setCoverPageAdded(true);
        }

        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        return coverPageLogoUrl;
    }

    /**
     * @param countryId
     * @return return agreement section list with empty section array as per front end requirement
     */
    public List<PolicyAgreementTemplateResponseDTO> getAllAgreementTemplateByCountryId(Long countryId) {
        return ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementRepository.findAllByCountryId(countryId),PolicyAgreementTemplateResponseDTO.class);
    }

    /**
     * @param unitId -
     * @return return agreement section list with empty section array as per front end requirement
     */
    public List<PolicyAgreementTemplateResponseDTO> getAllAgreementTemplateByUnitId(Long unitId) {
        return ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementRepository.findAllByOrganizationId(unitId),PolicyAgreementTemplateResponseDTO.class);
    }


    /**
     * @param referenceId                - countryId or unitId
     * @param isUnitId                   isUnitId boolean to check whether referenceId id coutry id or unit id
     * @param agreementTemplateId        - Agreement Template id
     * @param policyAgreementTemplateDto
     * @return
     */
    public <E extends AgreementTemplateDTO> E updatePolicyAgreementTemplateBasicDetails(Long referenceId, boolean isUnitId, Long agreementTemplateId, E policyAgreementTemplateDto) {

        PolicyAgreementTemplateMD template = isUnitId ? policyAgreementRepository.findByOrganizationIdAndDeletedAndName(referenceId, false, policyAgreementTemplateDto.getName()) : policyAgreementRepository.findByCountryIdAndDeletedAndName(referenceId, false, policyAgreementTemplateDto.getName());
        if (Optional.ofNullable(template).isPresent() && !agreementTemplateId.equals(template.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.policy.agreementTemplate", policyAgreementTemplateDto.getName());
        }
        try {
            template = policyAgreementRepository.getOne(agreementTemplateId);
            template.setName(policyAgreementTemplateDto.getName());
            template.setDescription(policyAgreementTemplateDto.getDescription());
            template.setTemplateType(templateTypeRepository.getOne(policyAgreementTemplateDto.getTemplateTypeId()));
            if (!isUnitId) {
                MasterAgreementTemplateDTO agreementTemplateDTO = (MasterAgreementTemplateDTO) policyAgreementTemplateDto;
                template.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getOrganizationTypes(), OrganizationType.class));
                template.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getOrganizationSubTypes(), OrganizationSubType.class));
                template.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getOrganizationServices(), ServiceCategory.class));
                template.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getOrganizationSubServices(), SubServiceCategory.class));
                template.setAccountTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(agreementTemplateDTO.getAccountTypes(), AccountType.class));
            }
            policyAgreementRepository.save(template);
        }catch (EntityNotFoundException nfe){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.policy.agreementTemplate", agreementTemplateId);
        }
        return policyAgreementTemplateDto;

    }


    /**
     * @param referenceId         - countryId or unitId
     * @param isUnitId            - to check whether referenceId country id or unit id
     * @param agreementTemplateId
     * @return
     * @description method return list of Agreement sections with sub sections of policy agreement template
     */
    public AgreementTemplateSectionResponseDTO getAllSectionsAndSubSectionOfAgreementTemplateByAgreementTemplateIdAndReferenceId(Long referenceId, boolean isUnitId, Long agreementTemplateId) {
        PolicyAgreementTemplateMD template = isUnitId ? policyAgreementRepository.findByIdAndOrganizationIdAndDeleted( agreementTemplateId,referenceId,false) : policyAgreementRepository.findByIdAndCountryIdAndDeleted(agreementTemplateId, referenceId, false);
        if (!Optional.ofNullable(template).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.policy.agreementTemplate", agreementTemplateId);
        }
        AgreementTemplateSectionResponseDTO agreementTemplateResponse = new AgreementTemplateSectionResponseDTO();
        OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO=null;
        List<Long> organizationTypeIds = new ArrayList<>();
        List<Long> organizationSubTypeIds = new ArrayList<>();
        List<Long> serviceCategoryIds = new ArrayList<>();
        List<Long> subServiceCategoryIds = new ArrayList<>();
        if(!isUnitId) {
            organizationMetaDataDTO = new OrganizationTypeAndSubTypeIdDTO();
            organizationTypeIds = template.getOrganizationTypes().stream().map(OrganizationType::getId).collect(Collectors.toList());
            organizationSubTypeIds = template.getOrganizationSubTypes().stream().map(OrganizationSubType::getId).collect(Collectors.toList());
            serviceCategoryIds = template.getOrganizationServices().stream().map(ServiceCategory::getId).collect(Collectors.toList());
            subServiceCategoryIds = template.getOrganizationSubServices().stream().map(SubServiceCategory::getId).collect(Collectors.toList());
            organizationMetaDataDTO = new OrganizationTypeAndSubTypeIdDTO(organizationTypeIds, organizationSubTypeIds,serviceCategoryIds,subServiceCategoryIds);
        }
        List<UnitLevelClauseResponseDTO> clauseListForUnitLevelTemplate = new ArrayList<>();
        List<ClauseBasicResponseDTO> clauseListForTemplate =  new ArrayList<>();
        List<AgreementSectionResponseDTO> agreementSectionResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(template.getAgreementSections(),AgreementSectionResponseDTO.class);
        if(isUnitId){
            clauseListForUnitLevelTemplate = ObjectMapperUtils.copyPropertiesOfListByMapper(clauseRepository.findAllClauseByUnitId(referenceId), UnitLevelClauseResponseDTO.class);
            //agreementSectionResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementRepository.getAllAgreementSectionsAndSubSectionByOrganizationIdAndAgreementTemplateId(referenceId, agreementTemplateId),AgreementSectionResponseDTO.class) ;
        }else{
            clauseListForTemplate = ObjectMapperUtils.copyPropertiesOfListByMapper(clauseRepository.findAllClauseByAgreementTemplateMetadataAndCountryId(referenceId, organizationTypeIds, organizationSubTypeIds,serviceCategoryIds,subServiceCategoryIds, template.getTemplateType().getId()),ClauseBasicResponseDTO.class);
            //agreementSectionResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementRepository.getAllAgreementSectionsAndSubSectionByCountryIdAndAgreementTemplateId(referenceId, agreementTemplateId),AgreementSectionResponseDTO.class);
        }

        //List<AgreementSectionResponseDTO> agreementSectionResponseDTOS = policyAgreementTemplateRepository.getAllAgreementSectionsAndSubSectionByReferenceIdAndAgreementTemplateId(referenceId, isUnitId, agreementTemplateId);
        /*agreementSectionResponseDTOS.forEach(agreementSectionResponseDTO ->
                {
                    Map<BigInteger, ClauseBasicResponseDTO> clauseBasicResponseDTOS = agreementSectionResponseDTO.getClauses().stream().collect(Collectors.toMap(ClauseBasicResponseDTO::getId, clauseBasicDTO -> clauseBasicDTO));
                    sortClauseOfAgreementSectionAndSubSectionInResponseDTO(clauseBasicResponseDTOS, agreementSectionResponseDTO);
                    if (!Optional.ofNullable(agreementSectionResponseDTO.getAgreementSubSections().get(0).getId()).isPresent()) {
                        agreementSectionResponseDTO.getAgreementSubSections().clear();
                    } else {
                        agreementSectionResponseDTO.getAgreementSubSections().forEach(agreementSubSectionResponseDTO -> {
                            Map<BigInteger, ClauseBasicResponseDTO> subSectionClauseBasicResponseDTOS = agreementSubSectionResponseDTO.getClauses().stream().collect(Collectors.toMap(ClauseBasicResponseDTO::getId, clauseBasicDTO -> clauseBasicDTO));
                            sortClauseOfAgreementSectionAndSubSectionInResponseDTO(subSectionClauseBasicResponseDTOS, agreementSubSectionResponseDTO);
                        });
                    }
                }
        );*/
      /*  List<ClauseBasicResponseDTO> clauseBasicResponseDTOS=policyAgreementTemplateRepository.getAllClausesByAgreementTemplateIdNotEquals(referenceId,isUnitId,agreementTemplateId);
       Map<BigInteger,ClauseBasicResponseDTO> clauseBasicResponseDTOMap=clauseBasicResponseDTOS.stream().collect(Collectors.toMap(k->k.getId(),v->v ,(p1, p2) -> p1));
        clauseListForTemplate.stream().forEach(clauseBasicResponseDTO -> {
            if(clauseBasicResponseDTOMap.get(clauseBasicResponseDTO.getId())!=null){
                clauseBasicResponseDTO.setLinkedWithOtherTemplate(true);
            }
        });*/
        agreementTemplateResponse.setClauseListForUnitLevelTemplate(clauseListForUnitLevelTemplate);
        agreementTemplateResponse.setClauseListForTemplate(clauseListForTemplate);
        agreementTemplateResponse.setIncludeContentPage(template.isIncludeContentPage());
        agreementTemplateResponse.setAgreementSections(agreementSectionResponseDTOS);
        agreementTemplateResponse.setCoverPageAdded(template.isCoverPageAdded());
        agreementTemplateResponse.setCoverPageData(ObjectMapperUtils.copyPropertiesByMapper(template.getCoverPageData(), CoverPageVO.class));
        agreementTemplateResponse.setSignatureComponentAdded(template.isSignatureComponentAdded());
        agreementTemplateResponse.setSignatureComponentLeftAlign(template.isSignatureComponentLeftAlign());
        agreementTemplateResponse.setSignatureComponentRightAlign(template.isSignatureComponentRightAlign());
        agreementTemplateResponse.setSignatureHtml(template.getSignatureHtml());
        return agreementTemplateResponse;
    }

    private void sortClauseOfAgreementSectionAndSubSectionInResponseDTO(Map<BigInteger, ClauseBasicResponseDTO> clauseBasicResponseDTOS, AgreementSectionResponseDTO agreementSectionResponseDTO) {
        List<ClauseBasicResponseDTO> clauses = new ArrayList<>();
        Map<BigInteger, ClauseCkEditorVO> clauseCkEditorVOMap = new HashMap<>();
 /*       if (CollectionUtils.isNotEmpty(agreementSectionResponseDTO.getClauseCkEditorVOS())) {
            clauseCkEditorVOMap = agreementSectionResponseDTO.getClauseCkEditorVOS().stream().collect(Collectors.toMap(ClauseCkEditorVO::getId, clauseCkEditorVO -> clauseCkEditorVO));
        }
        List<BigInteger> clauseIdOrderIndex = agreementSectionResponseDTO.getClauseIdOrderedIndex();
        for (int i = 0; i < clauseIdOrderIndex.size(); i++) {
            ClauseBasicResponseDTO clause = clauseBasicResponseDTOS.get(clauseIdOrderIndex.get(i));
            if (clauseCkEditorVOMap.containsKey(clause.getId())) {
                ClauseCkEditorVO clauseCkEditorVO = clauseCkEditorVOMap.get(clause.getId());
                clause.setTitleHtml(clauseCkEditorVO.getTitleHtml());
                clause.setDescriptionHtml(clauseCkEditorVO.getDescriptionHtml());
            }
            clauses.add(clause);
        }
        agreementSectionResponseDTO.setClauses(clauses);
        agreementSectionResponseDTO.getClauseCkEditorVOS().clear();
        agreementSectionResponseDTO.getClauseIdOrderedIndex().clear();*/
    }


    /**
     * @param referenceId
     * @param clauseId
     * @description - return list of Agreement Template Conatining clause in Section and Sub Sections
     */
    public List<AgreementTemplateBasicResponseDTO> getAllAgreementTemplateByReferenceIdAndClauseId(Long referenceId, boolean isUnitId, BigInteger clauseId) {
        return policyAgreementTemplateRepository.findAllByReferenceIdAndClauseId(referenceId, isUnitId, clauseId);
    }


    /**
     * @param referenceId
     * @param agreementTemplateClauseUpdateDTO - agreement template ids , clause previous id and new clause id
     * @Description method update agreement template section containing previous clause with new clause
     */
    public boolean updateAgreementTemplateClauseWithNewVersionByReferenceIdAndTemplateIds(Long referenceId, boolean isUnitId, AgreementTemplateClauseUpdateDTO agreementTemplateClauseUpdateDTO) {

        List<AgreementSection> agreementSectionsAndSubSectionsContainingClause = policyAgreementTemplateRepository.getAllAgreementSectionAndSubSectionByReferenceIdAndClauseId(referenceId, isUnitId, agreementTemplateClauseUpdateDTO.getAgreementTemplateIds(), agreementTemplateClauseUpdateDTO.getPreviousClauseId());
        Clause clause = clauseMongoRepository.findOne(agreementTemplateClauseUpdateDTO.getNewClauseId());

        if (CollectionUtils.isNotEmpty(agreementSectionsAndSubSectionsContainingClause)) {
            agreementSectionsAndSubSectionsContainingClause.forEach(agreementSection -> {
              //  ClauseCkEditorVO clauseCkEditorVO = new ClauseCkEditorVO(agreementTemplateClauseUpdateDTO.getNewClauseId(), "<p>" + clause.getTitle() + "</p>", "<p>" + clause.getDescription() + "</p>");
                List<ClauseCkEditorVO> clauseCkEditorVOS = new ArrayList<>(agreementSection.getClauseCkEditorVOS());
                ListIterator<ClauseCkEditorVO> clauseCkEditorVOIterator = clauseCkEditorVOS.listIterator();
                while (clauseCkEditorVOIterator.hasNext()) {
                    ClauseCkEditorVO clauseCkEditorVO1 = clauseCkEditorVOIterator.next();
                    if (agreementTemplateClauseUpdateDTO.getPreviousClauseId().equals(clauseCkEditorVO1.getId())) {
                       //clauseCkEditorVOIterator.set(clauseCkEditorVO);
                    }
                }
                agreementSection.setClauseCkEditorVOS(new HashSet<>(clauseCkEditorVOS));
                int clauseIndex = agreementSection.getClauseIdOrderedIndex().indexOf(agreementTemplateClauseUpdateDTO.getPreviousClauseId());
                agreementSection.getClauseIdOrderedIndex().set(clauseIndex, agreementTemplateClauseUpdateDTO.getNewClauseId());
            });
            agreementSectionMongoRepository.saveAll(getNextSequence(agreementSectionsAndSubSectionsContainingClause));
        }
        return true;
    }


    /**
     * @param referenceId - countryId or unitId
     * @param isUnitId    - isUnitId boolean to check whether referenceId id country id or unit id
     * @param templateId  - Agreement Template id
     * @return
     */
    public boolean deletePolicyAgreementTemplate(Long referenceId, boolean isUnitId, Long templateId) {

        PolicyAgreementTemplateMD policyAgreementTemplate = isUnitId ? policyAgreementRepository.findByIdAndOrganizationIdAndDeleted(templateId, referenceId, false) : policyAgreementRepository.findByIdAndCountryIdAndDeleted(templateId, referenceId, false);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.policy.agreementTemplate", templateId);
        }
        policyAgreementTemplate.delete();
        policyAgreementRepository.save(policyAgreementTemplate);
        return true;

    }

    //get country template by unitId
    public List<TemplateTypeMD> getAllTemplateType(Long unitId) {
        Long countryId= genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/country_id", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() { });
        return templateTypeRepository.getAllTemplateType(countryId);
    }


}



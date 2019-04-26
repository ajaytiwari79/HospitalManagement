package com.kairos.service.clause_tag;

import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.master_data.ClauseTagDTO;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.repository.clause_tag.ClauseTagRepository;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClauseTagService{

    @Inject
    private
    MessageSource messageSource;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseTagRepository clauseTagRepository;


    public List<ClauseTag> getAllClauseTagByCountryId(Long countryId) {
        return clauseTagRepository.findAllByCountryId(countryId);
    }

    public List<ClauseTag> getAllClauseTagByUnitId(Long unitId) {
        return clauseTagRepository.findAllClauseTagByUnitId(unitId);
    }


    /**
     * @param referenceId
     * @param tagList     list of clause tags
     * @return list of clause Tags
     * @throws DuplicateDataException if tag with same name is present in tagList
     * @description method new create tags and if tag already exist with same name then simply add tag id to  existClauseTagIds which later add to clause ,
     */
    public List<ClauseTag> saveClauseTagList(Long referenceId, boolean isOrganization, List<ClauseTagDTO> tagList) {

        List<ClauseTag> clauseTagList = new ArrayList<>();
        List<Long> existClauseTagIds = new ArrayList<>();
        Set<String> clauseTagsName = new HashSet<>();
        for (ClauseTagDTO tagDto : tagList) {
            if (tagDto.getId() == null) {
                if (clauseTagsName.contains(tagDto.getName())) {
                    exceptionService.duplicateDataException("message.duplicate", "message.tag", tagDto.getName());
                }
                clauseTagsName.add(tagDto.getName());
                ClauseTag clauseTag = new ClauseTag(tagDto.getName());
                if (isOrganization)
                    clauseTag.setOrganizationId(referenceId);
                else
                    clauseTag.setCountryId(referenceId);
                clauseTagList.add(clauseTag);

            } else {
                existClauseTagIds.add(tagDto.getId());
            }
        }
        if(!clauseTagsName.isEmpty()){
        Set<String> nameInLowerCase = clauseTagsName.stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
        List<ClauseTag> previousClauseTags = isOrganization ? clauseTagRepository.findByUnitIdAndTitles(referenceId, nameInLowerCase) : clauseTagRepository.findByCountryIdAndTitles(referenceId, nameInLowerCase);
        if (CollectionUtils.isNotEmpty(previousClauseTags)) {
            exceptionService.duplicateDataException("message.duplicate", "message.tag", previousClauseTags.get(0).getName());
        }
        }
        clauseTagRepository.saveAll(clauseTagList);
        if(!existClauseTagIds.isEmpty()) {
            clauseTagList.addAll(clauseTagRepository.findAllClauseTagByIds(existClauseTagIds));
        }

        return clauseTagList;
    }


}

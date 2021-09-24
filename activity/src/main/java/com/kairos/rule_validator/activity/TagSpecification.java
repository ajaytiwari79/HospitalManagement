package com.kairos.rule_validator.activity;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;

import java.util.List;

import static com.kairos.constants.ActivityMessagesConstants.STAFF_NOT_ALLOWED_ON_TAG;

public class TagSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private List<TagDTO> staffTags;
    private ExceptionService exceptionService;
    private RuleTemplateSpecificInfo ruleTemplateSpecificInfo;

    public TagSpecification(List<TagDTO> staffTags, RuleTemplateSpecificInfo ruleTemplateSpecificInfo, ExceptionService exceptionService) {
        this.staffTags = staffTags;
        this.exceptionService = exceptionService;
        this.ruleTemplateSpecificInfo = ruleTemplateSpecificInfo;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shiftWithActivityDTO) {
        shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> {
            TagDTO tagDTO = getTagFromActivity(shiftActivityDTO);
            if (ObjectUtils.isNotNull(tagDTO)) {
                DateTimeInterval timeInterval = new DateTimeInterval(tagDTO.getStartDate(), tagDTO.getEndDate());
                if (timeInterval.contains(shiftActivityDTO.getStartDate())) {
                    exceptionService.actionNotPermittedException(STAFF_NOT_ALLOWED_ON_TAG, shiftActivityDTO.getActivityName());
                }
            }
        });
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        return null;
    }

    private TagDTO getTagFromActivity(ShiftActivityDTO shiftActivityDTO) {
        for (TagDTO tagDTO : staffTags) {
            if (ruleTemplateSpecificInfo.getActivityWrapperMap().get(shiftActivityDTO.getActivityId()).getActivity().getTags().contains(tagDTO.getId())) {
                return tagDTO;
            }
        }
        return null;
    }
}

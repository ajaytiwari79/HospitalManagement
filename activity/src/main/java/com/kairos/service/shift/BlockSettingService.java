package com.kairos.service.shift;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.BlockSettingDTO;
import com.kairos.persistence.model.shift.BlockSetting;
import com.kairos.persistence.repository.shift.BlockSettingMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.ERROR_BLOCK_SETTING_NOT_FOUND;


/**
 * Created By G.P.Ranjan on 3/12/19
 **/
@Service
public class BlockSettingService {
    @Inject
    private BlockSettingMongoRepository blockSettingMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ActivityService activityService;
    @Inject
    private ExceptionService exceptionService;

    public BlockSettingDTO saveBlockSettingDetails(Long unitId, BlockSettingDTO blockSettingDTO) {
        Map<Long, Set<BigInteger>> blockDetails = blockSettingDTO.getBlockDetails();
        if(isMapEmpty(blockDetails) || blockDetails.size() == 1 && blockDetails.containsKey(null)){
            Set<BigInteger> activitySet;
            if(isMapNotEmpty(blockDetails)){
                activitySet = blockDetails.get(null);
            }else{
                activitySet = activityService.getFullDayOrWeekAndApprovalRequiredActivityIds(unitId, asDate(blockSettingDTO.getDate()));
            }
            Set<Long> staffIds = userIntegrationService.getStaffByUnitId(unitId).stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toSet());
            blockDetails = new HashMap<>();
            for (Long staffId : staffIds) {
                blockDetails.put(staffId,activitySet);
            }
        }
        BlockSetting blockSetting = blockSettingMongoRepository.findBlockSettingByUnitIdAndDate(unitId, blockSettingDTO.getDate());
        if(isNull(blockSetting)){
            blockSetting = new BlockSetting(unitId, blockSettingDTO.getDate(), blockDetails);
        }else{
            blockSetting.setBlockDetails(blockDetails);
        }
        blockSettingMongoRepository.save(blockSetting);
        blockSettingDTO.setId(blockSetting.getId());
        blockSettingDTO.setBlockDetails(blockDetails);
        return blockSettingDTO;
    }

    public BlockSettingDTO getBlockSettingDetail(Long unitId, LocalDate date) {
        return ObjectMapperUtils.copyPropertiesByMapper(blockSettingMongoRepository.findBlockSettingByUnitIdAndDate(unitId, date), BlockSettingDTO.class);
    }

    public boolean deleteBlockSettingDetail(Long unitId, LocalDate date) {
        BlockSetting blockSetting = blockSettingMongoRepository.findBlockSettingByUnitIdAndDate(unitId, date);
        if(isNull(blockSetting)){
            exceptionService.dataNotFoundException(ERROR_BLOCK_SETTING_NOT_FOUND);
        }
        blockSetting.setDeleted(true);
        blockSettingMongoRepository.save(blockSetting);
        return true;
    }
}

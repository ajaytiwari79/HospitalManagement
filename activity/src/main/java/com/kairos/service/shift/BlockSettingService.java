package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.startDateIsEqualsOrBeforeEndDate;
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

    public BlockSettingDTO saveBlockSetting(Long unitId, BlockSettingDTO blockSettingDTO) {
        Map<Long, Set<BigInteger>> blockDetails = blockSettingDTO.getBlockDetails();
        if(isMapEmpty(blockDetails) || blockDetails.size() == 1 && blockDetails.containsKey(null)){
            Set<BigInteger> activitySet;
            if(isMapNotEmpty(blockDetails)){
                activitySet = blockDetails.get(null);
            }else{
                activitySet = activityService.getAbsenceActivityIds(unitId, asDate(blockSettingDTO.getDate()));
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

    public BlockSettingDTO saveBlockSettingForCoverShift(Long unitId, BlockSettingDTO blockSettingDTO) {
        List<BlockSetting> blockSettingList=new ArrayList<>();
        List<BlockSetting> blockSettings=blockSettingMongoRepository.findAllBlockSettingByUnitIdAndDateRange(unitId,blockSettingDTO.getDate(),blockSettingDTO.getEndDate());
        Map<LocalDate,BlockSetting> blockSettingMap=blockSettings.stream().collect(Collectors.toMap(BlockSetting::getDate, Function.identity()));
        LocalDate startDate=blockSettingDTO.getDate();
         while (startDateIsEqualsOrBeforeEndDate(startDate,blockSettingDTO.getEndDate())){
            BlockSetting blockSetting=blockSettingMap.getOrDefault(blockSettingDTO.getDate(),new BlockSetting(unitId,blockSettingDTO.getDate(),null));
            if(blockSettingDTO.isUnblockStaffs()){
                blockSetting.getBlockedStaffForCoverShift().removeAll(blockSettingDTO.getBlockedStaffForCoverShift());
            }else {
                blockSetting.getBlockedStaffForCoverShift().addAll(blockSettingDTO.getBlockedStaffForCoverShift());
            }
            blockSettingList.add(blockSetting);
            startDate=startDate.plusDays(1);
        }
         blockSettingMongoRepository.saveEntities(blockSettingList);
        return blockSettingDTO;
    }

    public List<BlockSettingDTO> getBlockSettings(Long unitId, LocalDate startDate, LocalDate endDate) {
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(blockSettingMongoRepository.findAllBlockSettingByUnitIdAndDateRange(unitId, startDate, endDate), BlockSettingDTO.class);
    }

    public BlockSettingDTO getBlockSetting(Long unitId, LocalDate date) {
        BlockSetting blockSetting = blockSettingMongoRepository.findBlockSettingByUnitIdAndDate(unitId, date);
        return isNotNull(blockSetting) ? ObjectMapperUtils.copyPropertiesByMapper(blockSetting, BlockSettingDTO.class) : null;
    }

    public boolean deleteBlockSetting(Long unitId, LocalDate date) {
        BlockSetting blockSetting = blockSettingMongoRepository.findBlockSettingByUnitIdAndDate(unitId, date);
        if(isNull(blockSetting)){
            exceptionService.dataNotFoundException(ERROR_BLOCK_SETTING_NOT_FOUND);
        }
        blockSetting.setDeleted(true);
        blockSettingMongoRepository.save(blockSetting);
        return true;
    }
}

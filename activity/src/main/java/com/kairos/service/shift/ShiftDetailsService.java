package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * CreatedBy vipulpandey on 17/12/18
 **/
@Service
@Transactional
public class ShiftDetailsService extends MongoBaseService {

    private final Logger logger = LoggerFactory.getLogger(ShiftDetailsService.class);
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;

    public List<ShiftWithActivityDTO> shiftDetailsById(Long unitId, List<BigInteger> shiftIds) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsByIds(shiftIds);
        setReasonCodeInShifts(shiftWithActivityDTOS, unitId);
        return shiftWithActivityDTOS;
    }

    private void setReasonCodeInShifts(List<ShiftWithActivityDTO> shiftWithActivityDTOS, Long unitId) {
        Set<Long> reasonCodeIds = shiftWithActivityDTOS.stream().flatMap(shifts -> shifts.getActivities().stream().filter(shiftActivityDTO -> shiftActivityDTO.getAbsenceReasonCodeId() != null).map(shiftActivityDTO -> shiftActivityDTO.getAbsenceReasonCodeId())).collect(Collectors.toSet());
        reasonCodeIds.addAll(shiftWithActivityDTOS.stream().flatMap(shifts -> shifts.getActivities().stream().filter(shiftActivityDTO -> shiftActivityDTO.getReasonCodeId() != null).map(shiftActivityDTO -> shiftActivityDTO.getReasonCodeId())).collect(Collectors.toSet()));
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("reasonCodeIds", reasonCodeIds.toString()));
        ReasonCodeWrapper reasonCodeWrapper = genericIntegrationService.getUnitInfoAndReasonCodes(unitId, requestParam);
        Map<Long, ReasonCodeDTO> reasonCodeDTOMap = reasonCodeWrapper.getReasonCodes().stream().collect(Collectors.toMap(ReasonCodeDTO::getId, Function.identity()));

        for (ShiftWithActivityDTO shift : shiftWithActivityDTOS) {
            for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
                if (!shiftActivityDTO.isBreakShift()) {
                    shiftActivityDTO.setReasonCode(shiftActivityDTO.getReasonCodeId() != null ? reasonCodeDTOMap.get(shiftActivityDTO.getReasonCodeId()) : reasonCodeDTOMap.get(shiftActivityDTO.getAbsenceReasonCodeId()));
                }
                shiftActivityDTO.setLocation(reasonCodeWrapper.getContactAddressData());

            }
        }
    }
}

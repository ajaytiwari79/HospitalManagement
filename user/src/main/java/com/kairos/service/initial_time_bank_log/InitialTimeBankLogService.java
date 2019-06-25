package com.kairos.service.initial_time_bank_log;

import com.kairos.dto.user.initial_time_bank_log.InitialTimeBankLogDTO;
import com.kairos.persistence.model.user.initial_time_bank_log.InitialTimeBankLog;
import com.kairos.persistence.repository.user.initial_time_bank_log.InitialTimeBankLogRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created By G.P.Ranjan on 25/6/19
 **/
@Service
@Transactional
public class InitialTimeBankLogService {
    @Inject
    private InitialTimeBankLogRepository initialTimeBankLogRepository;
    @Inject
    private ExceptionService exceptionService;

    public InitialTimeBankLogDTO saveTimeBankLog(InitialTimeBankLogDTO timeBankLogDTO) {
        List<InitialTimeBankLog> previousInitialTimeBankLogs=initialTimeBankLogRepository.getInitialTimeBankLogByEmployment(timeBankLogDTO.getEmploymentId());
        if(previousInitialTimeBankLogs.size()>0){
            timeBankLogDTO.setPreviousInitialBalanceInMinutes(previousInitialTimeBankLogs.get(0).getUpdateInitialBalanceInMinutes());
        }else{
            timeBankLogDTO.setPreviousInitialBalanceInMinutes(0L);
        }
        if(timeBankLogDTO.getPreviousInitialBalanceInMinutes()==timeBankLogDTO.getUpdateInitialBalanceInMinutes()){
            exceptionService.actionNotPermittedException("Previous and Update both value are not equals");
        }
        InitialTimeBankLog initialTimeBankLog =new InitialTimeBankLog(timeBankLogDTO.getId(),timeBankLogDTO.getEmploymentId(),timeBankLogDTO.getPreviousInitialBalanceInMinutes(),timeBankLogDTO.getUpdateInitialBalanceInMinutes());
        initialTimeBankLogRepository.save(initialTimeBankLog);
        timeBankLogDTO.setId(initialTimeBankLog.getId());
        return timeBankLogDTO;
    }

    public List<InitialTimeBankLogDTO> getInitialTimeBalanceByEmployment(Long employmentId) {
        List<InitialTimeBankLog> initialTimeBankLogs=initialTimeBankLogRepository.getInitialTimeBankLogByEmployment(employmentId);
        List<InitialTimeBankLogDTO> initialTimeBankLogDTOs=new ArrayList<>();
        initialTimeBankLogs.forEach(initialTimeBankLog->{
            initialTimeBankLogDTOs.add(new InitialTimeBankLogDTO(initialTimeBankLog.getId(),initialTimeBankLog.getEmploymentId(),initialTimeBankLog.getPreviousInitialBalanceInMinutes(),initialTimeBankLog.getUpdateInitialBalanceInMinutes(),initialTimeBankLog.getCreationDate(),initialTimeBankLog.getCreatedBy()));
        });
        return initialTimeBankLogDTOs;
    }

}

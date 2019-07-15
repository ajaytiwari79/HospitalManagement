package com.kairos.service.initial_time_bank_log;

import com.kairos.dto.user.initial_time_bank_log.InitialTimeBankLogDTO;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.user.initial_time_bank_log.InitialTimeBankLog;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.initial_time_bank_log.InitialTimeBankLogRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Inject
    private UserGraphRepository userGraphRepository;

    public boolean saveInitialTimeBankLog(Long employmentId,Long updatedInitialBalanceInMinutes) {
        List<InitialTimeBankLog> previousInitialTimeBankLogs=initialTimeBankLogRepository.getInitialTimeBankLogByEmployment(employmentId);
        Long previousInitialBalanceInMinutes = 0L;
        if(previousInitialTimeBankLogs.size() > 0){
            previousInitialBalanceInMinutes=previousInitialTimeBankLogs.get(0).getUpdatedInitialBalanceInMinutes();

            if(previousInitialBalanceInMinutes == updatedInitialBalanceInMinutes){
                return false;
            }
        }
        InitialTimeBankLog initialTimeBankLog = new InitialTimeBankLog(employmentId,previousInitialBalanceInMinutes,updatedInitialBalanceInMinutes);
        initialTimeBankLogRepository.save(initialTimeBankLog);
        return true;
    }

    public List<InitialTimeBankLogDTO> getInitialTimeBalanceByEmployment(Long employmentId) {
        List<InitialTimeBankLog> initialTimeBankLogs = initialTimeBankLogRepository.getInitialTimeBankLogByEmployment(employmentId);
        List<Long> userIds=initialTimeBankLogs.stream().map(initialTimeBankLog -> initialTimeBankLog.getCreatedBy()).collect(Collectors.toList());
        List<User> users=userGraphRepository.findAllById(userIds);
        Map<Long,String> userFullNameMap=users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user.getFirstName() + " " + user.getLastName()));
        List<InitialTimeBankLogDTO> initialTimeBankLogDTOs = new ArrayList<>();
        initialTimeBankLogs.forEach(initialTimeBankLog->{
            initialTimeBankLogDTOs.add(new InitialTimeBankLogDTO(initialTimeBankLog.getId(),initialTimeBankLog.getEmploymentId(),initialTimeBankLog.getPreviousInitialBalanceInMinutes(),initialTimeBankLog.getUpdatedInitialBalanceInMinutes(),initialTimeBankLog.getCreationDate(),initialTimeBankLog.getCreatedBy(),userFullNameMap.get(initialTimeBankLog.getCreatedBy())));
        });
        return initialTimeBankLogDTOs;
    }

}

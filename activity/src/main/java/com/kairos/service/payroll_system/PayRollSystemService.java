package com.kairos.service.payroll_system;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll_system.PayRollSystemDTO;
import com.kairos.enums.payroll_system.PayRollType;
import com.kairos.persistence.model.payroll_system.PayRollSystem;
import com.kairos.persistence.repository.payroll_system.PayRollSystemRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayRollSystemService {
    Logger logger = LoggerFactory.getLogger(PayRollSystemService.class);

    @Inject
    private PayRollSystemRepository payRollSystemRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ExceptionService exceptionService;

    //=====================================================================

    /**
     * called only at the time of application bootstrap
     */
    public void createDefaultPayRollSystemList() {
        List<PayRollSystem> payRollSystemList = payRollSystemRepository.findAll();
        if (payRollSystemList.isEmpty()) {
            payRollSystemList = new ArrayList<>();
            payRollSystemList.add(new PayRollSystem(100, PayRollType.INTERNAL_PAYROLL, "This payroll is an INTERNAL_PAYROLL"));
            payRollSystemList.add(new PayRollSystem(200, PayRollType.REMOTE_PAYROLL, "This payroll is an REMOTE_PAYROLL"));
            payRollSystemList.add(new PayRollSystem(300, PayRollType.FULLY_OUTSOURCED_PAYROLL, "This payroll is an FULLY_OUTSOURCED_PAYROLL"));
            payRollSystemList.add(new PayRollSystem(400, PayRollType.LOCAL_PAYROLL_ADMINISTRATION, "This payroll is an LOCAL_PAYROLL_ADMINISTRATION"));
            payRollSystemRepository.saveEntities(payRollSystemList);
            logger.info("Default Payroll created on bootstartup");
        }
    }

    //============================================================
    public List<PayRollSystemDTO> getDefaultAvailablePayRolls() {
        List<PayRollSystem> payRollSystemList = payRollSystemRepository.findAll();
        if (payRollSystemList.isEmpty()) {
            exceptionService.dataNotFoundException("message.data.notFound","Defeault Payroll");
        }
        List<PayRollSystemDTO> payRollSystemDTOList=null;
        try {
          payRollSystemDTOList = ObjectMapperUtils.copyPropertiesOfListByMapper(payRollSystemList, PayRollSystemDTO.class);
        } catch (Exception e) {
            logger.error("Exception occured in Default Payroll creation during bootstart");
        }
        return payRollSystemDTOList;
    }


}

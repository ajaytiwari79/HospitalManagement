package com.kairos.activity.service.open_shift;

import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.open_shift.Order;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OrderMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.priority_group.PriorityGroupService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import com.kairos.response.dto.web.open_shift.OrderResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OpenShiftService extends MongoBaseService {

        private static final Logger logger = LoggerFactory.getLogger(PhaseService.class);
        @Inject
        private OpenShiftMongoRepository openShiftMongoRepository;
        @Inject
        private PriorityGroupService priorityGroupService;



        public void createOpenShift(OpenShiftResponseDTO openShiftResponseDTO) {

            OpenShift openShift = new OpenShift();
            ObjectMapperUtils.copyProperties(openShiftResponseDTO,openShift);
            save(openShift);
        }

        public void createOpenShiftFromOrder(List<OpenShiftResponseDTO> openShiftResponseDTOs, BigInteger orderId) {

            List<OpenShift> openShifts = new ArrayList<OpenShift>();
            for(OpenShiftResponseDTO openShiftResponseDTO: openShiftResponseDTOs) {
                openShiftResponseDTO.setOrderId(orderId);
                OpenShift openShift = new OpenShift();
                ObjectMapperUtils.copyProperties(openShiftResponseDTO,openShift);
                openShifts.add(openShift);

            }
            save(openShifts);
        }

        public void updateOpenShift(OpenShiftResponseDTO openShiftResponseDTO,BigInteger openShiftId) {

            OpenShift openShift = openShiftMongoRepository.findOpenShiftByIdAndEnabled(openShiftId);
            if(!Optional.ofNullable(openShift).isPresent()) {
                throw new DataNotFoundByIdException("OpenShift doesn not exist by id"+ openShiftId);
            }
            ObjectMapperUtils.copyProperties(openShiftResponseDTO,openShift);
            save(openShift);
        }

        public void deleteOpenShift(BigInteger openShiftId) {

            OpenShift openShift = openShiftMongoRepository.findOpenShiftByIdAndEnabled(openShiftId);
            if(!Optional.ofNullable(openShift).isPresent()) {
                throw new DataNotFoundByIdException("OpenShuift does not exist by id"+ openShiftId);
            }
            openShift.setDeleted(true);
            save(openShift);
        }


        public List<OpenShift> getOpenshiftsByUnitIdAndOrderId(Long unitId, BigInteger orderId) {

            List<OpenShift> openShifts = openShiftMongoRepository.findOpenShiftsByUnitIdAndOrderId(unitId,orderId);

            return openShifts;
        }

}

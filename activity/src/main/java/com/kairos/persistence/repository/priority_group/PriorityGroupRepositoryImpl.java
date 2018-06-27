package com.kairos.persistence.repository.priority_group;

import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.inject.Inject;
import java.math.BigInteger;

public class PriorityGroupRepositoryImpl implements CustomPriorityGroupRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public OpenShiftResponseDTO getOpenshiftByPriorityGroup() {


        return null;
    }
}

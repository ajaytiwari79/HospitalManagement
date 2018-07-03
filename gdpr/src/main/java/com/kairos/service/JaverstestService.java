package com.kairos.service;


import com.kairos.persistance.model.JaversTest;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.javers.JaversCommonService;
import org.javers.core.Javers;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class JaverstestService extends MongoBaseService {


    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    private JaversCommonService javers;


    public JaversTest createJaversTest(Long countryId,JaversTest javersTest)
    {

        Long l1=new Long(4);
        javersTest.setCountryId(l1);
        javersTest.setOrganizationId(4L);
        List<Long> longs=new ArrayList<>();
        longs.add(4l);
        javersTest.setFd(longs);
        javersTest=save(javersTest);
        javers.saveToJavers(javersTest);
        return javersTest;

    }



}

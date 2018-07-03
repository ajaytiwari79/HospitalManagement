package com.kairos.controller;


import com.kairos.persistance.model.JaversTest;
import com.kairos.service.JaverstestService;
import org.bson.types.ObjectId;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.spring.annotation.JaversAuditable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
public class JaversController {


    @Inject
    private JaverstestService  javerstestService;


    @Inject
    private Javers javers;

    @PostMapping("/{countryId}/javers")
    public JaversTest addJaversEntity(@PathVariable Long countryId, @RequestBody JaversTest javersTest) {

       return javerstestService.createJaversTest(countryId,javersTest);

    }


    @GetMapping("/test")
    public List<CdoSnapshot> getVersionHistory()
    {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(BigInteger.valueOf(38l), JaversTest.class);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());

        return changes;

    }

}

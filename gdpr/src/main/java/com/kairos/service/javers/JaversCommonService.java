package com.kairos.service.javers;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.userContext.UserContext;
import org.javers.core.Javers;
import org.javers.core.commit.Commit;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;

@Service
public class JaversCommonService {


    @Inject
    private Javers javers;


    public <T extends MongoBaseEntity> T saveToJavers(T entity) {

        Assert.notNull(entity, "Entity must not be null!");
        // Get class name for sequence class
         Commit commit = javers.commit(UserContext.getUserDetails().getUserName(), entity);
        return entity;

    }





}

package com.kairos.service;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.repository.user.UserBaseRepository;
import com.kairos.service.exception_handler.ExceptionHandlerService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * UserBaseService
 */
@Service
public class UserBaseService  {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    UserBaseRepository userBaseRepository;



    /**
     * @param entity
     * @return
     */
    public <T extends UserBaseEntity> T save(T entity) {
        DateTime dt = new DateTime();
        DateTime utc = dt.withZoneRetainFields(DateTimeZone.forID("Etc/GMT"));
        Date date = utc.toDate();
        logger.info("Utc Time: " + date);
        Long dateTime = date.getTime();

        if (entity.getId() == null) {
            entity.setCreationDate(dateTime);
        }
        entity.setLastModificationDate(dateTime);
        return userBaseRepository.save(entity);
    }

    public <T extends UserBaseEntity> List<T> save(List<T> entities) {
        DateTime dt = new DateTime();
        DateTime utc = dt.withZoneRetainFields(DateTimeZone.forID("Etc/GMT"));
        Date date = utc.toDate();
        logger.info("Utc Time: " + date);
        Long dateTime = date.getTime();
        entities.forEach(entity -> {
            if (entity.getId() == null) {
                entity.setCreationDate(dateTime);
            }
            entity.setLastModificationDate(dateTime);
        });
        userBaseRepository.saveAll(entities);
        return entities;
    }

    /**
     * @param id
     */
    public void delete(Long id) {
        userBaseRepository.delete(id);
    }


    public void safeDeleteEntity(Long id) {
        userBaseRepository.safeDelete(id);
    }


    /**
     * @param id
     * @return
     */
    public UserBaseEntity findOne(Long id) {
        return userBaseRepository.findOne(id);
    }

}

package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.PayOutPerShift;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

/**
 * @author pradeep
 * @date - 24/7/18
 */
public class PayOutRepositoryImpl implements CustomPayOutRepository {

    public static final String DATE = "date";
    @Inject
    private MongoTemplate mongoTemplate;



    public PayOutPerShift findLastPayoutByEmploymentId(Long employmentId, Date date) {
        Query query = new Query(Criteria.where("employmentId").is(employmentId).and(DATE).lt(date).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC, DATE));
        return mongoTemplate.findOne(query, PayOutPerShift.class);
    }


    public void updatePayOut(Long employmentId, int payOut) {
        Query query = new Query(Criteria.where("employmentId").is(employmentId).and("deleted").is(false));
        Update update = new Update().inc("payoutBeforeThisDate",payOut);
        mongoTemplate.updateMulti(query,update, PayOutPerShift.class);

    }

    @Override
    public List<PayOutPerShift> findAllByEmploymentAndBeforeDate(Set<LocalDate> dateSet, Set<DayOfWeek> dayOfWeekSet, Long employmentId, Date payOutDate){
        Criteria criteria = Criteria.where("employmentId").is(employmentId).and("deleted").is(false);
        if(isCollectionNotEmpty(dayOfWeekSet)) {
            criteria = criteria.and("dayOfWeek").in(dayOfWeekSet);
        }
        if(isCollectionNotEmpty(dateSet)){
            criteria = criteria.and(DATE).in(dateSet).lte(payOutDate);
        }else {
            criteria = criteria.and(DATE).lte(payOutDate);
        }
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, DATE));
        return mongoTemplate.find(query, PayOutPerShift.class);
    }

    public List<PayOutPerShift> findAllByEmploymentsAndDate(Set<LocalDate> dateSet, Set<DayOfWeek> dayOfWeekSet, Collection<Long> employmentIds, Date startDate, Date endDate){
        Criteria criteria = Criteria.where("employmentId").in(employmentIds).and("deleted").is(false);
        if(isCollectionNotEmpty(dayOfWeekSet)) {
            criteria = criteria.and("dayOfWeek").in(dayOfWeekSet);
        }
        if(isCollectionNotEmpty(dateSet)){
            criteria = criteria.and(DATE).in(dateSet).gte(startDate).lte(endDate);
        }else {
            criteria = criteria.and(DATE).gte(startDate).lte(endDate);
        }
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, DATE));
        return mongoTemplate.find(query, PayOutPerShift.class);
    }
}

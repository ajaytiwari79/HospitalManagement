package com.kairos.activity.service.restrcition_freuency;

import com.kairos.activity.persistence.model.restrcition_freuency.RestrictionFrequency;
import com.kairos.activity.persistence.repository.restrcition_freuency.RestrictionFrequencyRepository;
import com.kairos.activity.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by prabjot on 15/9/17.
 */
@Service
public class RestrictionFrequencyService extends MongoBaseService{

    @Inject
    private RestrictionFrequencyRepository restrictionFrequencyRepository;

    public RestrictionFrequency saveRestrictionFrequency(RestrictionFrequency restrictionFrequency){

        return save(restrictionFrequency);
    }

    public RestrictionFrequency getRestrictionFrequency(BigInteger id){
        return restrictionFrequencyRepository.findById(id).get();
    }

    public List<RestrictionFrequency> getRestrictionFrequency(){
        return restrictionFrequencyRepository.findAll();
    }
}

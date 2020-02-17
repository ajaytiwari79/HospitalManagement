package com.kairos.services.restrcition_freuency;

import com.kairos.persistence.model.restrcition_freuency.RestrictionFrequency;
import com.kairos.repositories.restrcition_freuency.RestrictionFrequencyRepository;
import com.kairos.services.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by prabjot on 15/9/17.
 */
@Service
public class RestrictionFrequencyService extends MongoBaseService {

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

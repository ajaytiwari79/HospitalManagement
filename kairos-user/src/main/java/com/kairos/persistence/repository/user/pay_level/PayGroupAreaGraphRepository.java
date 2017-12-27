package com.kairos.persistence.repository.user.pay_level;

import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.pay_level.PayGroupArea;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by prabjot on 21/12/17.
 */
@Repository
public interface PayGroupAreaGraphRepository extends GraphRepository<PayGroupArea> {

}

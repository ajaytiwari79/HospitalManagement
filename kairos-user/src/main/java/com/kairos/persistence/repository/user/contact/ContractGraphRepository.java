package com.kairos.persistence.repository.user.contact;
import com.kairos.persistence.model.user.contract.Contract;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

/**
 * Created by oodles on 23/11/16.
 */
public interface ContractGraphRepository extends GraphRepository<Contract>{

    @Override
    List<Contract> findAll();

}

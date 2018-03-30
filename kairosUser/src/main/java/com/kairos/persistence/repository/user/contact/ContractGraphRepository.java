package com.kairos.persistence.repository.user.contact;
import com.kairos.persistence.model.user.contract.Contract;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

import java.util.List;

/**
 * Created by oodles on 23/11/16.
 */
public interface ContractGraphRepository extends Neo4jBaseRepository<Contract,Long>{

    @Override
    List<Contract> findAll();

}

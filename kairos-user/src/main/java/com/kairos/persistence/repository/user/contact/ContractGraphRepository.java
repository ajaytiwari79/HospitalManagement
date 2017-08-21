package com.kairos.persistence.repository.user.contact;

import java.util.List;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.kairos.persistence.model.user.contract.Contract;

/**
 * Created by oodles on 23/11/16.
 */
public interface ContractGraphRepository extends GraphRepository<Contract>{

    @Override
    List<Contract> findAll();

}

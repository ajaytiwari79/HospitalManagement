package com.kairos.service.contract;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.kairos.persistence.model.user.contract.Contract;
import com.kairos.persistence.repository.user.contact.ContractGraphRepository;
import com.kairos.service.UserBaseService;

/**
 * Created by oodles on 23/11/16.
 */
@Service
public class ContractService  extends UserBaseService{

    @Inject
    ContractGraphRepository contractGraphRepository;

    public Contract addContract(Contract contract){
        return contractGraphRepository.save(contract);
    }


    public Contract getContract(Long id){
        return contractGraphRepository.findOne(id);
    }


    public List<Contract> getAllContract(){
        return contractGraphRepository.findAll();
    }



    public Contract updateContract(Contract contract){
        return contractGraphRepository.save(contract);
    }


    public boolean deleteContract(Long id){
         contractGraphRepository.delete(id);
        return !contractGraphRepository.exists(id);
    }



}

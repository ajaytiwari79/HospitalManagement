package com.kairos.service.contract;
import com.kairos.persistence.model.user.contract.Contract;
import com.kairos.persistence.repository.user.contact.ContractGraphRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by oodles on 23/11/16.
 */
@Service
public class ContractService {

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
         contractGraphRepository.deleteById(id);
        return !contractGraphRepository.existsById(id);
    }



}

package com.kairos.service.data_inventory.data_category_element;


import com.kairos.persistance.repository.master_data.data_category_element.DataElementMognoRepository;
import com.kairos.service.common.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class OrganizationDataElementService extends MongoBaseService {






    @Inject
    private DataElementMognoRepository dataElementMognoRepository;



}

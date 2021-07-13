package com.kairos.persistence.repository.counter;
/*
 *Created By Pavan on 30/4/19
 *
 */

import com.kairos.persistence.model.counter.KPISet;

import java.util.List;

public interface CustomKPISetRepository {

    List<KPISet> findAllByCountryIdAndDeletedFalse(List<Long> orgSubTypeIds,Long countryId);
}

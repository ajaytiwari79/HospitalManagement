package com.kairos.utils;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ComparisonUtils {


    public void checkOrgTypeAndService(Set<Long> ids, List<OrganizationTypeAndServiceBasicDTO> orgTypesAndServices) {

        Set<Long> orgTypeAndServiceIds = new HashSet<>();
        if (ids.size() != orgTypesAndServices.size()) {
            for (OrganizationTypeAndServiceBasicDTO result : orgTypesAndServices) {
                orgTypeAndServiceIds.add(result.getId());
            }
            Set<Long> differences = difference(ids, orgTypeAndServiceIds);
            throw new DataNotFoundByIdException("data for id " + differences.iterator().next() + "not exist");
        }


    }



    public Set<Long> difference(final Set<Long> set1, final Set<Long> set2) {
        final Set<Long> larger = set1.size() > set2.size() ? set1 : set2;
        final Set<Long> smaller = larger.equals(set1) ? set2 : set1;
        return larger.stream().filter(n -> !smaller.contains(n)).collect(Collectors.toSet());
    }
}

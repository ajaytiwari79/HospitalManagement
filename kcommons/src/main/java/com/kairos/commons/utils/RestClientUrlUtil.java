package com.kairos.commons.utils;


import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by vipul on 19/9/17.
 */
public class RestClientUrlUtil {



    public final static String getBaseUrl(boolean hasUnitInUrl, Long id,String userServiceUrl) {
        if(!Optional.ofNullable(id).isPresent()) {
            return userServiceUrl;
        }else {
            if (hasUnitInUrl) {
                return new StringBuilder(userServiceUrl).append("unit/").append(id ).toString();
            } else {
                return new StringBuilder(userServiceUrl).append("country/").append(id).toString();
            }
        }

    }

    public final static String arrayToDelimitedString(Collection c) {
        return StringUtils.collectionToCommaDelimitedString(c);
    }




}

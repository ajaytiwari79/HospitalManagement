package com.kairos.dto.user.country.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by prerna on 30/4/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterDetailDTO implements Serializable {

    private String id;
    private String value;

}

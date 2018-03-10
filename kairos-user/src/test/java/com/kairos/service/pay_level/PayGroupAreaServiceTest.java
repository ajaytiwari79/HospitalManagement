package com.kairos.service.pay_level;

import com.kairos.persistence.model.user.pay_level.MunicipalityPayGroupAreaWrapper;
import com.kairos.persistence.model.user.pay_level.PayGroupArea;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.response.dto.web.pay_level.PayGroupAreaDTO;
import com.kairos.util.DateUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by vipul on 10/3/18.
 */
public class PayGroupAreaServiceTest {
    List<MunicipalityPayGroupAreaWrapper> municipalityPayGroupArea;
    Set<Long> municipalityId;
    PayGroupAreaDTO payGroupAreaDTO;

    @Before
    public void setUp() throws Exception {
        municipalityId = new HashSet<>();
        municipalityId.add(1032L);
        municipalityId.add(1035L);
        municipalityId.add(1024L);
        municipalityPayGroupArea = new ArrayList<>();
        payGroupAreaDTO = new PayGroupAreaDTO("North", "Pay grp 1", municipalityId, DateUtil.getCurrentDate(), null);
        Municipality municipality = new Municipality(1032L);
        Municipality municipality2 = new Municipality(1035L);
        PayGroupArea payGroupArea = new PayGroupArea(1520743512000L, 1520743512000L);
        PayGroupArea payGroupArea2 = new PayGroupArea(1520743512000L, 1520743512000L);

        municipalityPayGroupArea.get(0).setPayGroupArea(payGroupArea);
        municipalityPayGroupArea.get(0).setMunicipality(municipality);


        municipalityPayGroupArea.get(1).setPayGroupArea(payGroupArea2);
        municipalityPayGroupArea.get(1).setMunicipality(municipality2);
    }

    @Test
    public void validatePayGroupArea() throws Exception {

    }

}
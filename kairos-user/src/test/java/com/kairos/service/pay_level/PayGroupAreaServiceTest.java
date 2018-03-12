package com.kairos.service.pay_level;

import com.kairos.persistence.model.user.pay_group_area.MunicipalityPayGroupAreaWrapper;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.response.dto.web.pay_group_area.PayGroupAreaDTO;
import com.kairos.service.pay_group_area.PayGroupAreaService;
import com.kairos.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 10/3/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class PayGroupAreaServiceTest {
    List<MunicipalityPayGroupAreaWrapper> municipalityPayGroupArea;
    Set<Long> municipalityId;
    PayGroupAreaDTO payGroupAreaDTO;
    @InjectMocks
    private PayGroupAreaService payGroupAreaService;

    @Before
    public void setUp() throws Exception {
        municipalityId = new HashSet<>();
        municipalityId.add(1032L);
        municipalityId.add(1035L);
        municipalityId.add(1024L);
        municipalityPayGroupArea = new ArrayList<>(10);
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
        payGroupAreaService.validatePayGroupArea(municipalityPayGroupArea, payGroupAreaDTO);
    }

}
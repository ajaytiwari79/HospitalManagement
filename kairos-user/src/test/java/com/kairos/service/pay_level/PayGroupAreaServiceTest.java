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
    private List<MunicipalityPayGroupAreaWrapper> municipalityPayGroupArea;
    private Set<Long> municipalityId;
    private PayGroupAreaDTO payGroupAreaDTO;
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
        MunicipalityPayGroupAreaWrapper currentObject = new MunicipalityPayGroupAreaWrapper(new Municipality(1032L), new PayGroupArea(1520743512000L, 1520743512000L));
        MunicipalityPayGroupAreaWrapper currentObject1 = new MunicipalityPayGroupAreaWrapper(new Municipality(1035L), new PayGroupArea(1520743512000L, 1520743512000L));
        municipalityPayGroupArea.add(currentObject);
        municipalityPayGroupArea.add(currentObject1);


    }

    @Test
    public void validatePayGroupArea() throws Exception {
        payGroupAreaService.validatePayGroupArea(municipalityPayGroupArea, payGroupAreaDTO);
    }

}
package com.kairos.service.agreement.wta;

import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import org.junit.Test;

/**
 * Created by vipul on 2/1/18.
 */
public class WTAServiceIntegrationTest {
    @Test
    public void createWta() throws Exception {
        WTADTO wtadto = new WTADTO();
        wtadto.setName("Hello");
    }

}
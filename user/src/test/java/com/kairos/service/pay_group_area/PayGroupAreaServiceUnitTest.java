package com.kairos.service.pay_group_area;

import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PayGroupAreaServiceUnitTest {


    @InjectMocks
    private PayGroupAreaService payGroupAreaService;

    @Mock
    PayGroupAreaGraphRepository payGroupAreaGraphRepository;

    @Mock
    private PayGroupArea payGroupArea;

    private  Long payGroupAreaId = 9999L;

    @Before
    public void before(){
//        MockitoAnnotations.initMocks(this);

        //        payGroupArea = new PayGroupArea();
        when(payGroupAreaGraphRepository.findOne(payGroupAreaId)).thenReturn(payGroupArea);
    }


    @Test
    public void deletePayGroupArea(){
        payGroupArea.setDeleted(true);
        when(payGroupAreaGraphRepository.save(payGroupArea)).thenReturn(payGroupArea);
        assert payGroupAreaService.deletePayGroupArea(payGroupAreaId) == true;
        verify(payGroupAreaGraphRepository,times(1)).findOne(payGroupAreaId);

    }



}

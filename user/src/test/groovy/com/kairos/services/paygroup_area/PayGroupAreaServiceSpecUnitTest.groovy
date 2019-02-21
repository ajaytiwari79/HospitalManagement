package com.kairos.services.paygroup_area

import com.kairos.persistence.model.user.pay_group_area.PayGroupArea
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository
import com.kairos.service.pay_group_area.PayGroupAreaService
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

import static org.mockito.Mockito.when

@RunWith(MockitoJUnitRunner.class)
class PayGroupAreaServiceSpecUnitTest {

    @InjectMocks
    private PayGroupAreaService payGroupAreaService;

    @Mock
    PayGroupAreaGraphRepository payGroupAreaGraphRepository;

    @Mock
    private PayGroupArea payGroupArea;

    private  Long payGroupAreaId = 9999L;


    @Before
        void before(){
        MockitoAnnotations.initMocks(this);
//        payGroupAreaGraphRepository = Mock(PayGroupAreaGraphRepository)
    }

    def "test deletion of pay group area service when sub"(){

        given:
        when(payGroupAreaGraphRepository.findOne(payGroupAreaId)).thenReturn(payGroupArea);
        payGroupArea.setDeleted(true)
        when(payGroupAreaGraphRepository.save(payGroupArea)).thenReturn(payGroupArea)


        when:
        boolean isPayGroupAreaDeleted = payGroupAreaService.deletePayGroupArea(payGroupAreaId)


        then:
        isPayGroupAreaDeleted
    }


}

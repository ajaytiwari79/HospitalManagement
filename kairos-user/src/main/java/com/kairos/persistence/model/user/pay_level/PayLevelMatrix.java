package com.kairos.persistence.model.user.pay_level;

import com.kairos.persistence.model.common.UserBaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prabjot on 21/12/17.
 */
public class PayLevelMatrix extends UserBaseEntity{
    private String expertiseLevel;
    private List<PayGroupArea> payGroupAreaList=new ArrayList<>();

    public PayLevelMatrix() {

    }
    public String getExpertiseLevel() {
        return expertiseLevel;
    }
    public void setExpertiseLevel(String expertiseLevel) {
        this.expertiseLevel = expertiseLevel;
    }

    public List<PayGroupArea> getPayGroupAreaList() {
        return payGroupAreaList;
    }

    public void setPayGroupAreaList(List<PayGroupArea> payGroupAreaList) {
        this.payGroupAreaList = payGroupAreaList;
    }
}

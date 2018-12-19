package com.kairos.persistence.repository.payroll;/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.persistence.model.payroll.PayRoll;

import java.math.BigInteger;

public interface CustomPayRollRepository {

    PayRoll findByNameOrCode(String name, int code);

    PayRoll findNameOrCodeExcludingById(BigInteger id, String name, int code);
}

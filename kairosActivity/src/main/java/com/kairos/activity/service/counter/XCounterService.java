package com.kairos.activity.service.counter;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class XCounterService implements CounterService{
    @Inject
    BaseCounterService baseCounterService;
    @Override
    public int getData() {
        return baseCounterService.getData();
    }
}

package com.planner.test;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ApplicationContextDetails implements ApplicationContextAware{
private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext=applicationContext;
    }

    public Map<String,Object> getAllSpringBeans()
    {
        Map<String,Object> stringObjectMap=new HashMap<>();
        for(String s:applicationContext.getBeanDefinitionNames())
        {
            if("solverConfigRepository".equals(s))
                stringObjectMap.put(s,applicationContext.getBean(s));
        }
        return stringObjectMap;
    }
}

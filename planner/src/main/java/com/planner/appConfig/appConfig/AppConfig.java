package com.planner.appConfig.appConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
@Component
public class AppConfig implements IAppConfig{


    @Value("${kairos.auth.Token}")
    private String kairosAuth;

    @Value("${kairos.local.url}")
    private String kairosBaseUrl;

    @Value("${graphhopper.key}")
    private String graphhoperkey;

    @Value("${drool.files.path}")
    private String droolFilePath;

    @Value("${vrp.config.xml}")
    private String vrpXmlFilePath;

    //@Value("${cassandra.keyspace}")
    private String cassandraKeySpace;

    //@Value("${cassandra.contactpoint}")
    private String cassandraContactPoint;

    private List<String> graphhoperKeys = Arrays.asList("4c0e9759-3e65-4ffb-b57a-883f28827ca7","cb87cd5e-a404-4da2-bc2f-db452ebd2c66","7ba37151-7141-4ea4-a89f-28a2ae297a0a");

    public int graphhoperKeyExpireCount = 0;

    @Override
    public String getKairosBaseUrl(){
        return kairosBaseUrl;
    }
    @Override
    public String getKairosAuth() {
        return kairosAuth;
    }
    @Override
    public String getGraphhoperkey(){return graphhoperkey;}

    @Override
    public String getCassandraKeySpace() {
        return cassandraKeySpace;
    }

    @Override
    public String getCassandraContactPoint() {
        return cassandraContactPoint;
    }

    public String getKeyAfterExpires(){
        return graphhoperKeys.get(graphhoperKeyExpireCount);
    }

    public void setGraphhoperKeyExpireCount(int count){
        graphhoperKeyExpireCount = count;
    }

    public String getDroolFilePath() {
        return droolFilePath;
    }

    public String getVrpXmlFilePath() {
        return vrpXmlFilePath;
    }
}

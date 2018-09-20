package com.kairos.service.google_calender;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.country.holiday.CountryHolidayCalender;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryHolidayCalenderGraphRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by oodles on 24/3/17.
 */
@Service
public class GoogleCalenderService {

    @Inject
    private CountryHolidayCalenderGraphRepository countryHolidayGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GoogleCalenderService.class);





    static {
        try {
            AppConstants.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            AppConstants.DATA_STORE_FACTORY = new FileDataStoreFactory(AppConstants.DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }



    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException, ClassNotFoundException {


        // Load client secrets.
//        InputStream targetStream =GoogleCalenderService.class.getResourceAsStream("/client_secret.json");
//        if (targetStream==null){
//            System.out.print("No Stream found");
//        }
//        else {
//            System.out.print(targetStream.toString());
//        }
//        GoogleClientSecrets clientSecrets =   GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(targetStream));
//
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(DATA_STORE_FACTORY)
//                .setAccessType("offline")
//                .build();
//        logger.info("New Auth URL: "+flow.newAuthorizationUrl());
//        String authURI = clientSecrets.getDetails().getAuthUri();
//        logger.info("Auth URI: "+authURI);
//        List<String> rediectURI  = clientSecrets.getDetails().getRedirectUris();
//        logger.info("Redirct URI: "+rediectURI);
//        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
//        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
//        return credential;





        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(AppConstants.JSON_FACTORY,
                new InputStreamReader(GoogleCalenderService.class.getResourceAsStream("/client_secret.json")));
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                AppConstants.HTTP_TRANSPORT, AppConstants.JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(AppConstants.DATA_STORE_FACTORY)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");




    }


    public static List<Event> getEventsFromGoogleCalender(){
        // CountryId is required for later adding support for other countries as well
        Credential credential = null;
        Calendar service = null;
        List<Event> itemsList = new ArrayList<>();

        try {
            credential = authorize();


            // Initialize Calendar service with valid OAuth credentials
            service = new Calendar.Builder(AppConstants.HTTP_TRANSPORT, AppConstants.JSON_FACTORY, credential).setApplicationName(AppConstants.APPLICATION_NAME).build();
            String pageToken = null;
            CountryHolidayCalender holidayCalender = null;
            List<CountryHolidayCalender> calenderList= new ArrayList<>();
            do {
                Events events = service.events().list("en.danish#holiday@group.v.calendar.google.com").setPageToken(pageToken).execute();
                List<Event> items = events.getItems();
                logger.info("No of Events:"+items.size());
                itemsList.addAll(items);

                pageToken = events.getNextPageToken();
            } while (pageToken != null);

            logger.info("Number of holidays found: "+itemsList.size());

        } catch (IOException e) {
            logger.info("Exception occured: "+e.getCause());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return itemsList;
    }
}

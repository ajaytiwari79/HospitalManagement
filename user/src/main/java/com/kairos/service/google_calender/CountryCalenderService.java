package com.kairos.service.google_calender;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryHolidayCalenderGraphRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by oodles on 24/3/17.
 */
@Service
public class CountryCalenderService {

    @Inject
    private CountryHolidayCalenderGraphRepository countryHolidayGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    private  static final org.slf4j.Logger logger = LoggerFactory.getLogger(CountryCalenderService.class);





    static {
        try {
            AppConstants.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            AppConstants.DATA_STORE_FACTORY = new FileDataStoreFactory(AppConstants.DATA_STORE_DIR);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }



    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(AppConstants.JSON_FACTORY,
                new InputStreamReader(CountryCalenderService.class.getResourceAsStream("/client_secret.json")));
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                AppConstants.HTTP_TRANSPORT, AppConstants.JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(AppConstants.DATA_STORE_FACTORY)
                .build();
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
            do {
                Events events = service.events().list("en.danish#holiday@group.v.calendar.google.com").setPageToken(pageToken).execute();
                List<Event> items = events.getItems();
                logger.info("No of Events:{}",items.size());
                itemsList.addAll(items);

                pageToken = events.getNextPageToken();
            } while (pageToken != null);

            logger.info("Number of holidays found: {}",itemsList.size());

        } catch (IOException e) {
            logger.info("Exception occured: {}",e.getCause());
            e.printStackTrace();
        }
        return itemsList;
    }
}

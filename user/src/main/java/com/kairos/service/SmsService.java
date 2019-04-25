package com.kairos.service;

import com.kairos.config.env.EnvConfig;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by prabjot on 29/11/16.
 */
@Service
public class SmsService {

    private final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Inject
    private EnvConfig envConfig;

    public boolean sendSms(String recipient, String message) {

        boolean isSent = true;

        try {
            TwilioRestClient client = new TwilioRestClient(envConfig.getTwillioAccountId(), envConfig.getTwillioAuthToken());

            // Build a filter for the MessageList
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(BODY, message));
            params.add(new BasicNameValuePair(TO, recipient));
            params.add(new BasicNameValuePair(FROM, envConfig.getGetTwillioNumber()));

            MessageFactory messageFactory = client.getAccount().getMessageFactory();
            messageFactory.create(params);
        } catch (TwilioRestException e) {
            isSent = false;
            logger.info("Exception occured while sending otp::" + e.getErrorMessage());
        } catch (Exception e) {
            isSent = false;
            logger.info("Exception occured while sending otp::" + e.getMessage());
        }
        return isSent;


    }
}

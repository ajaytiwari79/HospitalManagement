package com.kairos.activity.service.fls_visitour.webservice_config;

//import com.kairos.config.application.EnvConfig
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.Map;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
public class WebServiceMessageSenderWithAuth extends HttpUrlConnectionMessageSender {

    private Map<String, String> config;
    private String ipAddress;
    private String loggedInUser;

    public WebServiceMessageSenderWithAuth(Map<String, String> config, String ipAddress, String loggedInUser){
        this.config = config;
        this.ipAddress = ipAddress;
        this.loggedInUser = loggedInUser;
    }



    @Override
    protected void prepareConnection(HttpURLConnection connection)
            throws IOException {
        //TODO need to soft code
        String userpassword = config.get("userpassword");
        String encodedAuthorization = Base64.getEncoder().encodeToString(userpassword.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
        connection.setRequestProperty("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6");
        connection.setRequestProperty("IP-Address", ipAddress);
        connection.setRequestProperty("loggedInUser", loggedInUser);
        super.prepareConnection(connection);
    }
}

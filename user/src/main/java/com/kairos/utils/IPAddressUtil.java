package com.kairos.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by oodles on 27/4/17.
 */
public class IPAddressUtil {

    private static final Logger logger = LoggerFactory.getLogger(IPAddressUtil.class);

    public static String getIPAddress(HttpServletRequest httpServletRequest){

        String ipAddress ="";
        try{
            ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
            logger.debug("IP Address using X-FORWARDED-FOR " + ipAddress);

            if (ipAddress == null || ipAddress.equals("")) {
                ipAddress = httpServletRequest.getRemoteAddr();
                logger.debug("IP Address using getRemoteAddr "+ ipAddress);
            }
        }catch (Exception ex){
            logger.error("ex "+ex);
        }

        if(ipAddress == ""  || ipAddress.contains(":") || ipAddress.contains("127.0.")){
            Enumeration<NetworkInterface> networkInterfaces = null;
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            while (networkInterfaces != null && networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.toString().contains(":")) {
                        ipAddress = inetAddress.getHostAddress().toString();
                    }
                }
            }
            logger.debug("IP Address using NetworkInterface "+ ipAddress);
        }
        return ipAddress;
    }
}

package com.kairos.dto.user_context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {
    public static final String CORRELATION_ID = "correlation-id";
    public static final String AUTH_TOKEN     = "Authorization";
    public static final String USER_ID        = "user-id";

    private static final ThreadLocal<String> correlationId=  new InheritableThreadLocal<>();
    private static final ThreadLocal<String> authToken= new InheritableThreadLocal<>();
    private static final ThreadLocal<String> userId =new InheritableThreadLocal<>();
    private static final ThreadLocal<Long> orgId =new InheritableThreadLocal<>();
    private static final ThreadLocal<Long> unitId =new InheritableThreadLocal<>();
    private static final ThreadLocal<String> tabId =new InheritableThreadLocal<>();
    private static final ThreadLocal<CurrentUserDetails> userDetails =new InheritableThreadLocal<>();
    private static final ThreadLocal<Long> countryId = new InheritableThreadLocal<>();


    public static String getCorrelationId() { return correlationId.get(); }
    public static void setCorrelationId(String cid) {correlationId.set(cid);}

    public static String getAuthToken() { return authToken.get(); }
    public static void setAuthToken(String aToken) {authToken.set(aToken);}

    public static String getUserId() { return userId.get(); }
    public static void setUserId(String aUser) {userId.set(aUser);}

    public static Long getOrgId() { return orgId.get(); }
    public static void setOrgId(Long aOrg) {orgId.set(aOrg);}

    public static void setUnitId(Long unitid) {unitId.set(unitid);}
    public static Long getUnitId() {return unitId.get();}

    public static void setCountryId(Long countryid) {countryId.set(countryid);}
    public static Long getCountryId() {return countryId.get();}

    public static void setUserDetails(CurrentUserDetails details) {userDetails.set(details);}
    public static CurrentUserDetails getUserDetails() {return userDetails.get();}

    public static void setTabId(String aTabId){tabId.set(aTabId);}
    public static String getTabId() {return tabId.get();}



}
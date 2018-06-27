package com.kairos.user.client;

/**
 * Created by prabjot on 3/5/17.
 */
public class ClientTemporaryAddress extends ContactAddress {

    /**
     * @autor prabjot
     * static factory to get instance of contact address, this is mainly used for client address,
     * whenever new client address will be created,default access to location also will be created
     * @return
     */
    public static ClientTemporaryAddress getInstance(){
        ClientTemporaryAddress contactAddress = new ClientTemporaryAddress();
        contactAddress.setAccessToLocation(new AccessToLocation());
        return contactAddress;
    }

}

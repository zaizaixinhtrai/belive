package com.appster.manager;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.util.Set;

/**
 * Created by thanhbc on 6/9/17.
 */

public class BeLiveXMPPTCPConnection extends XMPPTCPConnection {

    public BeLiveXMPPTCPConnection(XMPPTCPConnectionConfiguration config) {
        super(config);
    }

    public Set<ConnectionListener> getConnectionListeners(){
        return connectionListeners;
    }
}

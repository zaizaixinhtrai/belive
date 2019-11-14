package com.appster.models;

/**
 * Created by Ngoc on 3/14/2017.
 */

public class NetworkUploadSpeedModel {

    private String serverName;
    private long uptime;
    private long bytesIn;
    private long bytesOut;
    private long bytesInRate;
    private long bytesOutRate;
    private long totalConnections;
    private ConnectionCountBean connectionCount;
    private String applicationInstance;
    private String name;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public long getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(long bytesIn) {
        this.bytesIn = bytesIn;
    }

    public long getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(long bytesOut) {
        this.bytesOut = bytesOut;
    }

    public long getBytesInRate() {
        return bytesInRate;
    }

    public void setBytesInRate(long bytesInRate) {
        this.bytesInRate = bytesInRate;
    }

    public long getBytesOutRate() {
        return bytesOutRate;
    }

    public void setBytesOutRate(long bytesOutRate) {
        this.bytesOutRate = bytesOutRate;
    }

    public long getTotalConnections() {
        return totalConnections;
    }

    public void setTotalConnections(long totalConnections) {
        this.totalConnections = totalConnections;
    }

    public ConnectionCountBean getConnectionCount() {
        return connectionCount;
    }

    public void setConnectionCount(ConnectionCountBean connectionCount) {
        this.connectionCount = connectionCount;
    }

    public String getApplicationInstance() {
        return applicationInstance;
    }

    public void setApplicationInstance(String applicationInstance) {
        this.applicationInstance = applicationInstance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class ConnectionCountBean {
        /**
         * RTMP : 0
         * MPEGDASH : 0
         * CUPERTINO : 0
         * SANJOSE : 0
         * SMOOTH : 0
         * RTP : 0
         */

        private int RTMP;
        private int MPEGDASH;
        private int CUPERTINO;
        private int SANJOSE;
        private int SMOOTH;
        private int RTP;

        public int getRTMP() {
            return RTMP;
        }

        public void setRTMP(int RTMP) {
            this.RTMP = RTMP;
        }

        public int getMPEGDASH() {
            return MPEGDASH;
        }

        public void setMPEGDASH(int MPEGDASH) {
            this.MPEGDASH = MPEGDASH;
        }

        public int getCUPERTINO() {
            return CUPERTINO;
        }

        public void setCUPERTINO(int CUPERTINO) {
            this.CUPERTINO = CUPERTINO;
        }

        public int getSANJOSE() {
            return SANJOSE;
        }

        public void setSANJOSE(int SANJOSE) {
            this.SANJOSE = SANJOSE;
        }

        public int getSMOOTH() {
            return SMOOTH;
        }

        public void setSMOOTH(int SMOOTH) {
            this.SMOOTH = SMOOTH;
        }

        public int getRTP() {
            return RTP;
        }

        public void setRTP(int RTP) {
            this.RTP = RTP;
        }
    }
}

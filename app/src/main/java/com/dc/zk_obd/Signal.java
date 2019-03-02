package com.dc.zk_obd;

public class Signal {
    private String dbname;
    private String messagename;
    private int canid;
    private byte[] cancmd= new byte[3];
    private String signalname;
    private int startbyte;
    private int startbit;
    private int length;
    private double factor=1;
    private double offset=0;
    private byte[] canvalue= new byte[10];
    private double signalvalue;

    public byte[] getCanvalue() {
        return canvalue;
    }

    public void setCanvalue(byte[] canvalue) {
        this.canvalue = canvalue;
    }

    public void setCancmd(byte[] cancmd) {
        this.cancmd = cancmd;
    }

    public byte[] getCancmd() {
        return cancmd;
    }

    public double getSignalvalue() {
        long num = 0;
        for (int ix = 3; ix < 11; ++ix) {
            num <<= 8;
            num |= (canvalue[ix] & 0xff);
        }
        num<<=((startbyte-1)*8+startbit);
        num>>=(64-length);
        signalvalue=(double)num*factor+offset;
        return signalvalue;
    }
    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getMessagename() {
        return messagename;
    }

    public void setMessagename(String messagename) {
        this.messagename = messagename;
    }

    public int getCanid() {
        return canid;
    }

    public void setCanid(int canid) {
        this.canid = canid;
    }

    public String getSignalname() {
        return signalname;
    }

    public void setSignalname(String signalname) {
        this.signalname = signalname;
    }

    public int getStartbyte() {
        return startbyte;
    }

    public void setStartbyte(int startbyte) {
        this.startbyte = startbyte;
    }

    public int getStartbit() {
        return startbit;
    }

    public void setStartbit(int startbit) {
        this.startbit = startbit;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

}

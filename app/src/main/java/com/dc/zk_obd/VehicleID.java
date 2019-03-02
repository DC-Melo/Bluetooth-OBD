package com.dc.zk_obd;

public class VehicleID {

    private byte[] canTx= new byte[3];
    private byte[] canRx= new byte[44];
    private String VIN;

    public byte[] getCanTx() {
        return canTx;
    }

    public void setCanTx(byte[] canTx) {
        this.canTx = canTx;
    }

    public byte[] getCanRx() {
        return canRx;
    }

    public void setCanRx(byte[] canRx) {
        this.canRx = canRx;
        for(int i=0;i<4;i++){

        }
    }

    public String getVIN() {
        return VIN;
    }

}


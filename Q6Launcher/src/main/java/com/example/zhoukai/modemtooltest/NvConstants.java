package com.example.zhoukai.modemtooltest;

/**
 * Created by zhoukai on 17-1-3.
 * request id of nv items
 * support the access of following nv items
 */

public class NvConstants {
 
    //the request id of reading imei
    public static final int REQUEST_GET_IMEI = 1;

    //the request id of writing imei
    public static final int REQUEST_SET_IMEI = 2;

    //the request id of reading wifi mac address
    public static final int REQUEST_GET_MAC_ADDRESS = 3;

    //the request id of writing wifi mac address
    public static final int REQUEST_SET_MAC_ADDRESS = 4;

    //the request id of reading QSN , customers can use it to customize their own sn
    public static final int REQUEST_GET_QSN = 5;

    //the request id of writing QSN
    public static final int REQUEST_SET_QSN = 6;

    //the request id of reading SN , the value is corresponding with sn presented on SC20 module
    public static final int REQUEST_GET_SN = 7;

    //the request id of writing SN
    public static final int REQUEST_SET_SN = 8;

    //the request id of reading MEID
    public static final int REQUEST_GET_MEID = 9;

    //the request id of writing MEID
    public static final int REQUEST_SET_MEID = 10;
}

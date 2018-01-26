package com.example.zhoukai.modemtooltest;



public class ModemToolTest  {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
   
 

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is used to get the value of nv items listed in {@link NvConstants}.
     */
    public static native String getItem(int nvid);

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is used to set the value of nv items listed in {@link NvConstants}.
     */
    public static native int setItem(int nvid, String value);
}
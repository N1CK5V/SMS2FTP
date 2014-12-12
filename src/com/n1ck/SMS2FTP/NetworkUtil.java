package com.n1ck.SMS2FTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


//Класс для проверки наличия подключения к интернету
public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

        public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {

            status = "1";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {

            status = "2";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {

            status = "0";
        }
        return status;
    }
}
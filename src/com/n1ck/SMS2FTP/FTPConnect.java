package com.n1ck.SMS2FTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTPClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class FTPConnect {

    //Инициализируем переменные
    String MyPREFERENCES = "MyPrefs" ;
    String hostName="";
    String ftpUser="";
    String ftpPassword="";

    //Подключаемся к FTP
    public boolean connectToFTP(Context ctx, String fileToUpload){

        SharedPreferences sf = ctx.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sf.contains("ftphost"))
        {
            hostName=sf.getString("ftphost", "");

        }

        if (sf.contains("username"))
        {

            ftpUser=sf.getString("username", "");

        }


        if (sf.contains("password"))
        {
            ftpPassword=sf.getString("password", "");

        }


        FTPClient ftpClient = new FTPClient();
        boolean ftpSuccess=false;


        try {
            ftpClient.connect(InetAddress.getByName(hostName));
            ftpClient.login(ftpUser, ftpPassword);
            File dir = new File(Environment.getExternalStorageDirectory(), "/log/" + fileToUpload);


            FileInputStream srcFileStream = new FileInputStream(dir);
            ftpSuccess = ftpClient.storeFile("/log-" + System.currentTimeMillis() + ".txt",
                    srcFileStream);

            if (ftpSuccess){
                dir.delete();
            }
            srcFileStream.close();
        } catch (SocketException e) {
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }
        return ftpSuccess;
    }



}
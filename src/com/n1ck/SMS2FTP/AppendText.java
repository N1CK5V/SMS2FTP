package com.n1ck.SMS2FTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.format.DateFormat;

public class AppendText {


    //Добавляем СМС информацию к файлу
    public boolean appendStringToFile(final String appendContents, String filename) {
        boolean result = false;
        try {

            File file = Environment.getExternalStorageDirectory();
            File file1 = new File(file,filename);
            if (file != null && file.canWrite()) {
                file1.createNewFile();
                Writer out = new BufferedWriter(new FileWriter(file1, true), 1024);
                out.write(appendContents + "\n" + "\n" + "\n");
                out.close();
                result = true;
            }

        } catch (IOException e) {

        }
        return result;
    }

    //Конвертируем дату из milliiseconds в нормальный вид
    public String convertDate(long dateInMilliseconds) {
        return DateFormat.format("dd/MM/yyyy hh:mm:ss", dateInMilliseconds).toString();
    }


    //Получаем имена из телефонной книги
    public String getContactName(Context context, String phoneNo) {
        String contactName = "";
        try{
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNo));
            String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };
            String sortOrder = ContactsContract.PhoneLookup.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            ContentResolver cr = context.getContentResolver();
            if(cr != null){
                Cursor resultCur = cr.query(uri, projection, null, null, sortOrder);
                if(resultCur != null){
                    while (resultCur.moveToNext()) {

                        contactName = resultCur.getString(resultCur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));

                        break;
                    }
                    resultCur.close();
                }
            }
        }
        catch(Exception e){
        }

        return contactName;
    }
}

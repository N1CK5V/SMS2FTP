package com.n1ck.SMS2FTP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    EditText host, user, pwd;
    Button ok;
    String MyPREFERENCES = "MyPrefs" ;
    String ftphost="ftphost";
    String username="username";
    String password="password";
    SharedPreferences sf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        user=(EditText)findViewById(R.id.user);
        pwd=(EditText)findViewById(R.id.pwd);
        host=(EditText)findViewById(R.id.ftpHost);
        ok=(Button)findViewById(R.id.ok);

        sf= getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        //Проверяем заполнены ли поля
        if (sf.contains(ftphost))
        {
            host.setText(sf.getString(ftphost, ""));

        }

        if (sf.contains(username))
        {
            user.setText(sf.getString(username, ""));

        }


        if (sf.contains(password))
        {
            pwd.setText(sf.getString(password, ""));

        }


        //Запускаем по нажатию кнопки
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //Сохраняем настройки
                Editor editor = sf.edit();

                editor.putString(ftphost, host.getText().toString());
                editor.putString(username, user.getText().toString());
                editor.putString(password, pwd.getText().toString());
                editor.commit();

                startService(new Intent(getApplicationContext(), Tasks.class));
                finish();

            }
        });

    }


}
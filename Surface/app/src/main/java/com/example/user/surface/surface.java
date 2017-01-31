package com.example.user.surface;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.LinkedList;
import java.util.List;

public class surface extends AppCompatActivity {
    public Button adlbutton;
    public Button tugbutton;
    public Button recordbutton;
    public Button walkbutton;
    static int mainmode=0;
   public static String username="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surface);
        username=getUsername();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //button宣告
        adlbutton = (Button) findViewById(R.id.Adl);
        tugbutton = (Button) findViewById(R.id.tug);
        recordbutton = (Button) findViewById(R.id.record);
        walkbutton = (Button) findViewById(R.id.walk);
        //BADL button
        adlbutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(surface.this, ADLmenu.class);
                startActivity(intent);

            }

        });
        //IADL button

        //TUG button
        tugbutton = (Button) findViewById(R.id.tug);
        tugbutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainmode = 1;

                Intent intent = new Intent();
                intent.setClass(surface.this, TestActivity.class);
                startActivity(intent);
                Log.v("lll", "bb");
            }

        });
        //WALK button
        walkbutton = (Button) findViewById(R.id.walk);
        walkbutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainmode = 2;
                Intent intent = new Intent();
                intent.setClass(surface.this, TestActivity.class);
                startActivity(intent);
                Log.v("lll", "bb");
            }

        });
        //HISTORY button
        recordbutton = (Button) findViewById(R.id.record);
        recordbutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainmode = 0;
                Intent intent = new Intent();
                intent.setClass(surface.this, CalendarActivity.class);
                startActivity(intent);
                Log.v("lll", "bb");


            }

        });
    }
    //=============================================================================================
    static String uriAPI = "http://192.168.11.4:17027/httpPostTest.php";
    protected static final int REFRESH_DATA = 0x00000001;
    public  String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
     //   return "allen";
    }


    }



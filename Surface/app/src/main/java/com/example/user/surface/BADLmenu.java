package com.example.user.surface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BADLmenu extends AppCompatActivity {
    public Button qusetionbutton;
    public Button btn_getup,btn_upstair;
    public Button medicinebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badlmenu);

        //question button
        qusetionbutton = (Button) findViewById(R.id.question_badl);
        qusetionbutton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(BADLmenu.this, badl.class);
                startActivity(intent);

            }

        });
        //phone button
        btn_getup = (Button) findViewById(R.id.badl_getup);
        btn_getup.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                surface.mainmode = 3;

                Intent intent = new Intent();
                intent.setClass(BADLmenu.this, TestActivity.class);
                startActivity(intent);

            }

        });
        btn_upstair = (Button) findViewById(R.id.badl_upstair);
        btn_upstair.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                surface.mainmode = 4;

                Intent intent = new Intent();
                intent.setClass(BADLmenu.this, TestActivity.class);
                startActivity(intent);

            }

        });

    }
}

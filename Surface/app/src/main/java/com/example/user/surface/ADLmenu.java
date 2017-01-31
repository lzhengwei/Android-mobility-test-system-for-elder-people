package com.example.user.surface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ADLmenu extends AppCompatActivity {
    public Button qusetionbutton;
    public Button btn_iadl,btn_badl;
    public Button medicinebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adlmenu);

        //question button
        btn_badl = (Button) findViewById(R.id.buttonBADL);
        btn_badl.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ADLmenu.this, BADLmenu.class);
                startActivity(intent);

            }

        });
        //phone button
        btn_iadl = (Button) findViewById(R.id.buttonIADL);
        btn_iadl.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                surface.mainmode = 3;

                Intent intent = new Intent();
                intent.setClass(ADLmenu.this, IADLmenu.class);
                startActivity(intent);

            }

        });

    }
}

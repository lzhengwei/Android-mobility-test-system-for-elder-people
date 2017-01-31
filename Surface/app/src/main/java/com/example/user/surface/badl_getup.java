package com.example.user.surface;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class badl_getup extends AppCompatActivity implements SensorEventListener {
    //=========sensor==================================
    private SensorManager sensorManager,compassManager;
    private Sensor accelerometer;

    private Button btstart;
    private TextView maintext;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float lastX, lastY, lastZ,mdelta;

    private SoundPool sound;
    int startsound,upsound,walksound;
    //==============================================
    private boolean up=false;
    private boolean motiveState = false;   //是否处于运动状态
    private boolean processState = false;   //标记当前是否已经在计步

    //==============================================
    private float sensorsec=0,sensorpoint=0,sec,startuptime;
    private int sensorsecond=0,sensorcountsec=0;

    private float[] save=new float[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badl_getup);
        btstart=(Button)findViewById(R.id.button);
        sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        walksound=sound.load(this,R.raw.walksound,1);
        maintext=(TextView)findViewById(R.id.textView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            maintext.setText("sensor get ");

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, 100000);

        } else {
            maintext.setText("no sensor");
            // fai! we dont have an accelerometer!
        }
        btstart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (processState == true) {

                    btstart.setText("開始");
                    processState = false;
                } else {
                    sec=sensorsec;
                    btstart.setText("停止");
                    processState=true;
                }
                deltaXMax=0;
                deltaYMax=0;
                deltaZMax=0;

            }
        });
    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, 100000);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        if(processState)
        {
            sensorcountsec++;
            sensorpoint=(float)sensorcountsec/10;
            sensorsec=sensorsecond+sensorpoint;
            // secondpoint=getTimessecond();
            if(sensorcountsec>=9)
            { sensorsecond= (int)(sensorsec+1);sensorcountsec=-1;}
            maintext.setText("Time : "+sensorsec+" ");

            if(deltaZ<=0.4)
                deltaZ=0;
            save[4] = save[3];
            save[3] = save[2];
            save[2] = save[1];
            save[1] = save[0];
            save[0] =deltaZ;
            if( !up && (sensorsec-sec>5))
            {
                up=true;
                Log.v("first", "time " + sensorsec);

                sound.autoPause();
                sound.play(walksound, 1, 1, 0, 0, 1);
                //  sound.autoPause();
                //  sound.play(walksound, 1, 1, 0, 0, 1);
            }
            else if (up)
            {
                if(deltaZ!=0 && save[1]==0 && save[2]==0 && !motiveState)
                { motiveState=true;

                    startuptime=sensorsec;
                    //sound.play(upsound, 1, 1, 0, 0, 1);

                }
                else if (motiveState && save[1]==0 && save[2]==0 &&save[3]==0)
                {
                    processState=false;
                    startuptime=sensorsec-startuptime;
                    maintext.setText(startuptime+"");
                    sound.autoPause();
                    sound.play(walksound, 1, 1, 0, 0, 1);
                }
            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

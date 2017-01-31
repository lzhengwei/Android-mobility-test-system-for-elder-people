
package com.example.user.surface;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class badl_upstair extends AppCompatActivity implements SensorEventListener {
    //=========sensor==================================
    private SensorManager sensorManager,compassManager;
    private Sensor accelerometer;
    private String uriAPI="http://192.168.1.227/test/BADL.php";
    protected static final int REFRESH_DATA = 0x00000001;
    private Button btstart,btresult;
    private TextView maintext;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float lastX, lastY, lastZ;

    private SoundPool sound;
    int startsound,upsound,walksound;
    //==============================================
    private boolean up=false;
    private boolean motiveState = false;   //???????????????????
    private boolean processState = false;   //???????????????????

    //==============================================
    private float sensorsec=0,sensorpoint=0,sec,startuptime,uptime=0;
    private int sensorsecond=0,sensorcountsec=0;

    private float[] save=new float[5];
    double average_mvalue,mvalue,mdelta;
    private float[] record=new float[5];
    private double lstValue = 0,oriValue;  //????????
    double highvalue=0;
    double difference=0;
    //=========================== need result =====================================
    private double[] viewdata=new double[1000];
    private int dataindex=0;
    private String data="";
    //============================================
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // ????????????????????
                case REFRESH_DATA:
                    String result = null;
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if (result != null)
                        // ?????????????????????
                        // Toast.makeText(TUG_MainActivity.this, result, Toast.LENGTH_LONG).show();
                        break;
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badl_upstair);
        btresult=(Button)findViewById(R.id.button3);
        btstart=(Button)findViewById(R.id.button2);
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
            //maintext.setText("Time : "+sensorsec+" ");

          /*  if(deltaZ<=0.4)
                deltaZ=0;
            save[4] = save[3];
            save[3] = save[2];
            save[2] = save[1];
            save[1] = save[0];
            save[0] =deltaZ;*/

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
                mdelta=magnitude(deltaX,deltaY,deltaZ);
                save[4] = save[3];
                save[3] = save[2];
                save[2] = save[1];
                save[1] = save[0];
                save[0] =lastY;
                average_mvalue = (save[4] + save[3] +save[0] + save[2] + save[1] ) /5;  //y???
                if(mdelta<=0.4)
                    mdelta=0;

                record[2]=record[1];
                record[1]=record[0];
                record[0]=(float)mdelta;
                if(mdelta!=0 && record[1]==0 && record[2]==0 && !motiveState)
                { motiveState=true;
                    oriValue=average_mvalue;
                    lstValue=average_mvalue;
                    uptime=sensorsec;
                    //sound.play(upsound, 1, 1, 0, 0, 1);
                    Log.v("upget",">1  "+"\nvalue : "+lstValue+"\ndelta : "+mdelta);
                    //  Log.v("upget",">1  "+"\ntime : "+sec);
                }
                else if (motiveState) {
                    //  up=true;
                    /*if(lastX<-8)
                    {
                        motiveState = false;
                        up=true;
                        save=new float[10];
                        uptime=sec-uptime;
                      //  sound.play(ding, 1, 1, 0, 0, 1);*/

                    data+=average_mvalue+"\n";

                    if (lstValue < average_mvalue) {
                        lstValue = average_mvalue;
                        Log.v("up", " > " + lstValue);
                        Log.v("high", " > " + highvalue);
                        highvalue=lstValue;
                        if (difference<Math.abs(deltaY))
                        difference=Math.abs(deltaY);
                    }
                    else if (lstValue>average_mvalue&&average_mvalue>oriValue)
                    {

                        lstValue= average_mvalue;
                        Log.v("down", " > " + lstValue);
                        if (difference<Math.abs(deltaY))
                            difference=Math.abs(deltaY);
                    }
                    else if (average_mvalue < oriValue && lstValue>average_mvalue && (oriValue-average_mvalue)>0.2 &&(sensorsec-uptime)>1) {
                        lstValue = -9999;
                        motiveState = false;
                        up=false;
                        save=new float[10];
                        if (difference<Math.abs(deltaY))
                            difference=Math.abs(deltaY);
                        uptime=sensorsec-uptime;
                        maintext.setText("uptime :"+uptime);
                        maintext.setText(maintext.getText()+"\ndifference : "+Math.abs(deltaY));
                        processState=false;
                        //  sound.play(ding, 1, 1, 0, 0, 1);*/
                                   /* try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                        sound.play(walksound, 1, 1, 0, 0, 1);
                        Thread t=new Thread(new sendPostRunnable());
                        t.start();
                    }
                }
            }


        }
    }

    //================================================================================================
    private String sendPostDataToInternet()
    {
      /* ????HTTP Post???? */
        HttpPost httpRequest = new HttpPost(uriAPI);
      /*
       * Post??????????????????NameValuePair[]?????????
       */
        List<NameValuePair> params = new ArrayList<NameValuePair>();
      /*  List<HashMap<String, Float>> params = new ArrayList<HashMap<String, Float>>();
        HashMap<String, Float> map = new HashMap<String, Float>();*/
        params.add(new BasicNameValuePair("uptime",String.valueOf(uptime) ));
        params.add(new BasicNameValuePair("difference",String.valueOf(difference) ));

        try
        {
          /* ??????HTTP request */
            httpRequest.setEntity(new UrlEncodedFormEntity( params, HTTP.UTF_8));
          /* ??????HTTP response */
            HttpResponse httpResponse = new DefaultHttpClient()
                    .execute(httpRequest);
          /* ????????????200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
             /* ?????????????? */
                String strResult = EntityUtils.toString(httpResponse
                        .getEntity());
                // ??????????????
                return strResult;

            }
        } catch (Exception e)

        {

            e.printStackTrace();

        }

        return null;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public double magnitude(float x, float y, float z) {
        double magnitude = 0;
        magnitude = Math.sqrt(x * x + y * y + z * z);
        return magnitude;
    }
    class sendPostRunnable implements Runnable
    {

        @Override
        public void run()
        {
            String result = sendPostDataToInternet();
            mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
        }

    }
}

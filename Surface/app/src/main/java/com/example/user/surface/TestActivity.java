package com.example.user.surface;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/**
 * Created by Jay on 2016/9/13.
 */
public class TestActivity extends AppCompatActivity implements SensorEventListener {

    private Timer timer = new Timer(true);

    final Context context = this;
    private Button btn_work,btn_starttest;
    private TextView answertext;
    private RelativeLayout testchart;
    private GraphicalView testview;
    private Spinner signallist;
    //============ Answer ===========================================
    private  String answer="";
    //============== readfile =======================================
    private String viewfilename;
    private int fileid=-1,ratio=5;
    private double datamax=0,datamin=100,showdatamax=0,showdatamin=100;
    //============= view ==========================================
    private double[] viewdata=new double[1000], viewdata2=new double[1000],dx;
    private int viewdata_index=0,peak_index=0,peak_index2=0;
    private double[] mspdatam1=new double[2000];
    private double[] mspdatamview1=new double[1000];
    private double[] mspdatam2=new double[2000];
    private double[] mspdatamview2=new double[1000];

    //============ tiptool =============================================
    private PopupWindow popup = null;
    private View layout;
    private View viewParent;
    //========= main ====================================
    int Tugmode=0,fileinmode=0,mainmode= surface.mainmode,showmode=0,Btmode=0;
    //========== walk ==========================================
    private boolean up1=false,isturn=false,iswalked1=false,timeup=false;
    int walkpoint[]=new int[10];
    int mspwalkpoint[] =new int[30];
    int mspwalkpoint2[] =new int[30];
    int walkdataindex=0,Walkpoint,Btdatasize1=0,Btdatasize2=0,walkcross=120;
    String txtdata="";
    //==============================sensor=================================
    private SensorManager sensorManager,compassManager;
    private Sensor accelerometer,magntic;
    private float[] record=new float[5];

    private float lastX=0, lastY=0, lastZ=0;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float gravity[];
    // Magnetic rotational data
    private float magnetic[]; //for magnetic rotational data
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float[] values = new float[3];
    private float azimuth;

    private float init_orentation,orentationvalue,lst_orentation;
    //==============sensor time======================================
    private float sensorsec=0,sensorpoint=0;
    private int sensorsecond=0,sensorcountsec=0;
    //====================time================================================
    private float uptime=0,alltime=0,walktime=0,sec=0,turnaroundtime=0,backtime=0;
    private float secondpoint;
    private float second;
    private float[] timespit=new float[5];
    //==================Gait==============================================
    private int step = 0,datanumber=0,firstpeak;
    private double lstValue = 0,oriValue,walkfreq,ccnum=0;  //上次的值
    private double curValue = 0,range=2;  //当前值
    private boolean motiveState = false;   //是否处于运动状态
    private boolean processState = false;   //标记当前是否已经在计步
    private Queue averqueue=new LinkedList();
    //=================== BADL =======================================
    double highvalue=0,difference=0;
    //===========average data search===================================
    double[] peak=new double[3];
    double[] save=new double[5],averagedata=new double[10];
    double valley;
    double[] time=new double[100];
    int searchmode=1,stepcount=0;
    double average_mvalue,mvalue,mdelta;
    //========== sound ==========================================
    public MediaPlayer mediaPlayer;
    private SoundPool sound;
    int startsound,upsound,walksound,endsound,turnaroundsound,walkbacksound,ding,downsound,startnoseatsound;
    //============= draw ========================================
    int gridindex[]=new int[20];
    //================= Bluetooth ====================================
    private static final UUID BLUETOOTH_SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ConnectedThread connect1,connect2;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket,nnSocket;
    BluetoothDevice mmDevice,nnDevice;
    OutputStream mmOutputStream,nnOutputStream;
    private Handler tryconnect_handler=new Handler(),myTimer = new Handler();
    long downTime = SystemClock.uptimeMillis();
    long eventTime = SystemClock.uptimeMillis() + 100;
    MotionEvent motionEvent = MotionEvent.obtain(
            downTime,
            eventTime,
            MotionEvent.ACTION_DOWN,
           0.0f,
            0.0f,
            0
    );

    String btreceivedata1="",btreceivedata2="";
    //========================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        this.findViewById(android.R.id.content).getRootView().dispatchGenericMotionEvent(motionEvent);

//=================== main ==========================================
        testchart = (RelativeLayout) findViewById(R.id.test);   //三軸
        answertext=(TextView) findViewById(R.id.textView2);
        btn_starttest=(Button)findViewById(R.id.button7);
        signallist=(Spinner)findViewById(R.id.spinner5);
        btn_work = (Button) findViewById(R.id.button8);
        //   testview=(GraphicalView)findViewById(R.id.view2);
        //======================sensor======================================
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compassManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        boolean haveaccsensor=true,havemagsensor=true;
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            Log.v("sensor", "sensor get ");
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // sensorManager.registerListener(TestActivity.this, accelerometer, 100000);

        } else {
            haveaccsensor=false;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // success! we have an accelerometer
            magntic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            // compassManager.registerListener(compass, compassManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),100000);
        }
        else
            havemagsensor=false;
        if(!haveaccsensor || !havemagsensor)
        {
            String showsensorwrong="";
            if(!haveaccsensor && !havemagsensor)
                showsensorwrong="無\n三軸加速規感測器\n方向感測器";
            else if(!haveaccsensor)
                showsensorwrong="無\n三軸加速規感測器";
            else if(!havemagsensor)
                showsensorwrong="無\n方向感測器";

            ShowAlertMessage(showsensorwrong);
        }
        //=================  sound ========================================
        mediaPlayer=new MediaPlayer();
        mediaPlayer=MediaPlayer.create(TestActivity.this,R.raw.endvoice);

        sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        startsound=sound.load(this,R.raw.readyvoice,1);
        upsound=sound.load(this,R.raw.upandgo,1);
        walksound=sound.load(this,R.raw.walkvoice,1);
        turnaroundsound=sound.load(this,R.raw.turnaroundandback,1);
        walkbacksound=sound.load(this,R.raw.walkbackvoice,1);
        endsound=sound.load(this,R.raw.endvoice,1);
        ding=sound.load(this,R.raw.walksound,1);
        downsound=sound.load(this,R.raw.downvoice,1);
        startnoseatsound=sound.load(this,R.raw.readynoseatdown,1);
        //============ read file ===================================
        Bundle bundle= this.getIntent().getExtras();
        Log.v("mainmode",mainmode+"");

        if(mainmode==0) {
            boolean nodata=bundle.getBoolean("nodata");
            viewfilename=bundle.getString("filename");
            Log.v("filename", viewfilename);
            Log.v("fileid", fileid + "");
            if(nodata)
            {
                fileid=bundle.getInt("fileid");
                writetxtdataorigin();
            }
            setTitle(viewfilename + "結果");
            btn_starttest.setVisibility(View.INVISIBLE);
            //    signallist.setVisibility(View.VISIBLE);

            Log.v("fileindmode", fileinmode + "");
            showresult();




        }
        else
        {

            btn_work.setVisibility(View.INVISIBLE);
            switch(mainmode)
            {
                case 1:
                    setTitle("起立行走測驗");
                    break;
                case 2:
                    setTitle("10公尺走路測驗");
                    break;
                case 3:
                    setTitle("撿起物品測驗");
                    break;
                case 4:
                    setTitle("爬樓梯測驗");
                    break;
            }
        }
        Log.v("mainmode", mainmode + "");
        final int delay=100000;
        //====================== button ==========================================================================
        btn_starttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  int sensorrate=100000;
                switch(mainmode)
                {

                    case 1:
                        /*if(which==2)
                        {
                            Btmode=1;
                            txtdata+="B\n";
                            Bluetoothopen();


                        }*/
                        Bluetoothopen();

                        sound.play(startnoseatsound, 1, 1, 0, 0, 1);
                        sensorManager.registerListener(TestActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                        sensorManager.registerListener(TestActivity.this, magntic,  SensorManager.SENSOR_DELAY_UI);

                        searchmode = 1;
                        sec = sensorsec;
                       // myTimer.postDelayed(timercount,100);
                        timer.schedule(new MyTimerTask(), 100, 100);

                        break;
                    case 2:
                       /* if(which==2)
                        {
                            Btmode=1;
                            txtdata+="B\n";
                            Bluetoothopen();

                        }*/
                        AlertDialog.Builder editDialog = new AlertDialog.Builder(TestActivity.this);
                        editDialog.setTitle("輸入跨步長");

                        final EditText editText = new EditText(TestActivity.this);
                        editText.setText("");
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setTextSize(30);
                        editText.setHint("約120cm~150cm");
                        editDialog.setView(editText);
                        editDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {
                                Log.v("type0", editText.getText().toString());
                                try {
                                    walkcross = Integer.parseInt(editText.getText().toString());
                                }
                                catch(Exception x)
                                {ShowAlertMessage(x.toString());}
                                Bluetoothopen();

                                sound.play(startnoseatsound, 1, 1, 0, 0, 1);
                                sensorManager.registerListener(TestActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);

                                //   sensorManager.registerListener(TestActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                                searchmode=1;
                                sec = sensorsec;

                                myTimer.postDelayed(timercount, 100);
                            }
                        });
                        editDialog.show();
                        break;
                    case 3:
                        sensorManager.registerListener(TestActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                        sec = sensorsec;
                        myTimer.postDelayed(timercount, 100);
                        break;
                    case 4:
                        sensorManager.registerListener(TestActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                        sec = sensorsec;
                        myTimer.postDelayed(timercount, 100);
                        break;
                }
                btn_starttest.setVisibility(View.INVISIBLE);
                String[] startmode={"手機模式","","感測器模式"};


            }
        });
        btn_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showresult();

            }
        });


        signallist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 2:
                            Btmode = 0;
                            showchart(1);
                        break;
                    case 1:
                        if(fileinmode==1)
                        {
                            Btmode = 1;
                            showmode=1;
                            showchart(1);
                        }
                        else
                        {
                            Btmode = 0;
                            showchart(1);
                        }
                        break;
                    case 0:
                            Btmode = 1;
                            showmode=2;

                        showchart(1);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //   signallist.bringToFront();

    }
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this,magntic);
        Log.v("log", "destroy");
        if(connect1!=null)
            connect1.cancel();
        if(connect2!=null)
            connect2.cancel();
        myTimer.removeCallbacks(timercount);
        Thread.currentThread().interrupt();
        timer.cancel();
        // compassManager.unregisterListener(compass);

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
        switch(id)
        {
            case R.id.action_transform:
                /*Intent intent = new Intent();
                intent.setClass(TestActivity.this, filelistActivity.class);
                startActivity(intent);*/
                Log.v("threadgo", "");

                break;
           /* case R.id.action_deleteall:
                try {
                    File mSDFile = Environment.getExternalStorageDirectory();
                    //讀取文件檔路徑
                    for(int i=0;i<MainActivity.filelist.size();i++) {
                        String deletefilename=MainActivity.filelist.get(i);
                        File deletefile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + deletefilename + ".txt");
                        deletefile.delete();

                        // Toast.makeText(TestActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();
                    }
                       MainActivity.filelist.clear();
                    Toast.makeText(TestActivity.this, "已刪除所有紀錄", Toast.LENGTH_SHORT).show();

                }
                catch (Exception x)
                {
                    Log.v("delete exception",x.toString());
                }
                break;*/
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = event.values.clone();



                float[] value = event.values;
                deltaX = Math.abs(lastX - value[0]);
                deltaY = Math.abs(lastY - value[1]);
                deltaZ = Math.abs(lastZ - value[2]);
                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];
                mvalue=magnitude2(value[0], value[1]);
                mdelta= magnitude(deltaX, deltaY, deltaZ);

                switch(mainmode)
                {
                    case 1:
                        switch(Tugmode) {
                            case 0:
                                //Log.v("time",sensorsec+"");

                                if(sensorsec-sec>=12)
                                {
                                    btwrite('m',"a");
                                    btwrite('n', "a");
                                    //Log.v("time", "over" + sensorsec + "");
                                    sound.play(ding, 1, 1, 0, 0, 1);
                                    sound.play(upsound, 1, 1, 0, 0, 1);
                                    Tugmode=1;
                                }
                                break;
                            case 1:
                                //Log.v("up","yes");
                                showchart(0);
                                save[4] = save[3];
                                save[3] = save[2];
                                save[2] = save[1];
                                save[1] = save[0];
                                save[0] = Math.abs(lastY);
                                average_mvalue = (save[4] + save[3] + save[0] + save[2] + save[1]) / 5;

                                //=========================================================

                                //=========================================================
                                if (mdelta <= 0.4)
                                    mdelta = 0;
                                //  updata += lastY + " ";
                                record[2] = record[1];
                                record[1] = record[0];
                                record[0] = (float) mdelta;
                                if (mdelta != 0 && record[1] == 0 && record[2] == 0 && !motiveState) {
                                    motiveState = true;
                                    oriValue = average_mvalue;
                                    uptime = sensorsec;
                                    alltime=sensorsec;
                                    //sound.play(upsound, 1, 1, 0, 0, 1);
                                    Log.v("upget", ">1  " + "\nvalue : " + oriValue + "\ndelta : " + mdelta);
                                    //  Log.v("upget",">1  "+"\ntime : "+sec);
                                } else if (motiveState) {
                                    //  up=true;
                                    //================================================
                                    viewdata[walkdataindex]=average_mvalue;
                                    viewdata_index++;
                                    walkdataindex++;
                                    txtdata=txtdata+average_mvalue+"\n";
                                    //===============================================
                                    // lstValue = average_mvalue;
                                    // Log.v("upget","lstvalue : " + lstValue );

                                    if (average_mvalue>=6) {
                                        motiveState = false;
                                        up1 = true;
                                        save = new double[10];
                                        uptime = sensorsec - uptime;
                                        walktime=sensorsec;
                                        Log.v("walktime",walktime+"");
                                        //  sound.play(ding, 1, 1, 0, 0, 1);
                               /* if (lstValue < average_mvalue) {
                                    lstValue = average_mvalue;
                                    Log.v("down", " > " + lstValue);
                                } else if (lstValue > average_mvalue && Math.abs(average_mvalue - oriValue) >2 ) {
                                    lstValue = -9999;
                                    motiveState = false;
                                    up1=true;
                                    save=new double[10];
                                    uptime=sec-uptime;
                                    sound.play(ding, 1, 1, 0, 0, 1);*/
                                   /* try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                                        //sound.play(walksound, 1, 1, 0, 0, 1);
                                        //searchmode=2;
                                        gridindex[1]=walkdataindex;
                                        txtdata+="E"+walkdataindex+"\n";
                                        Log.v("E",walkdataindex+"");
                                        Tugmode = 2;
                                        btreceivedata1+="l";
                                        btreceivedata2+="l";
                                    }
                                }

                                break;
                            case 2:
                                showchart(0);
                                save[4]=save[3];
                                save[3]=save[2];
                                save[2] = save[1];
                                save[1] = save[0];
                                save[0] = mvalue;
                                if(averqueue.size()<5)
                                    averqueue.add(mvalue);
                                else
                                {
                                    averqueue.add(mvalue);
                                    average_mvalue=average_edit(averqueue,5);
                                    averqueue.poll();
                                }
                                //average_mvalue = (save[0] + save[2] + save[1]+save[3]+save[4]) / 5;
                                averagedata[4] = averagedata[3];
                                averagedata[3] = averagedata[2];
                                averagedata[2] = averagedata[1];
                                averagedata[1] = averagedata[0];
                                averagedata[0] = average_mvalue;
                                //  viewdata += average_mvalue + " ";
                                //---------------------------------------------------------
                                // walkdata[walkdataindex]=average_mvalue;
                                viewdata[walkdataindex]=average_mvalue;
                                viewdata_index++;
                                walkdataindex++;
                                txtdata=txtdata+average_mvalue+"\n";
                                //-----------------------------------------------------
                                switch (searchmode) {
                                    case 1:
                                        peak[0] = averagedata[2];
                                        searchmode=2;
                                       /* if (averagedata[2] > averagedata[1] && averagedata[2] > averagedata[0] && averagedata[2] > averagedata[3] && averagedata[2] > averagedata[4] && peak[0] == 0) {
                                            peak[0] = averagedata[2];
                                            searchmode = 2;

                                        }*/
                                        break;
                                    case 2:
                                        if (averagedata[2] < averagedata[1] && averagedata[2] < averagedata[0] && averagedata[2] < averagedata[3] && averagedata[2] < averagedata[4]) {
                                            if (Math.abs(peak[0] - averagedata[2]) > 0.5) {
                                                valley = averagedata[2];
                                                Walkpoint=walkdataindex-3;
                                                searchmode = 3;
                                            } else {
                                                //    Log.v("valley back 1", "valley : " +  save[1] + " peak[0] : " + peak[0]);
                                                peak[0] = 0;
                                                searchmode = 1;
                                            }
                                        }
                                        break;
                                    case 3:

                                        if (averagedata[2] > averagedata[1] && averagedata[2] > averagedata[0] && averagedata[2] > averagedata[3] && averagedata[2] > averagedata[4]) {
                                            if (Math.abs(averagedata[2] - valley) > 0.7) {

                                                //Log.v("valley get", "valley : " + valley + " peak[0] : " + peak[0] + " peak[1] : " +save[1]);
                                                peak[0] = averagedata[2];
                                                time[stepcount] = sensorsec;
                                                stepcount++;
                                                Log.v("step time", "time " + stepcount + " = " + time[stepcount]);

                               /*   sound.autoPause();
                                   sound.play(walksound, 1, 1, 0, 0, 1);*/
                                                //  peak[1] = 0;
                                                if (stepcount >= 4) {
                                                   /* if (orentationvalue > 180)
                                                        init_orentation = orentationvalue;
                                                    else
                                                        init_orentation = orentationvalue + 180;*/
                                                    init_orentation=orentationvalue;
                                                    Log.v("ori init", "ori :" + orentationvalue + "init : " + init_orentation);
                                                    averqueue.clear();
                                                   // sound.play(ding, 1, 1, 0, 0, 1);
                                                    //walktime = (float) ((time[1] - time[0]) + (time[2] - time[1]) + (time[3] - time[2]) + (time[4] - time[3])) / 4;
                                                    walktime=sensorsec-walktime;
                                                    Log.v("walktime",walktime+" "+sensorsec);

                                                    timespit[1] = sensorsec;
                                                    sound.play(turnaroundsound, 1, 1, 0, 0, 1);

                                                    isturn = true;
                                                    iswalked1 = true;
                                                    txtdata+="E"+walkdataindex+"\n";
                                                    gridindex[2]=walkdataindex;
                                                    Log.v("E",walkdataindex+"");

                                                    Tugmode = 3;

                                                    btreceivedata1+="l";
                                                    btreceivedata2+="l";

                                                } else
                                                    searchmode = 2;
                                            } else {
                                                peak[0] = 0;
                                                searchmode = 1;
                                            }

                                        }
                                        break;
                                }
                                break;
                            case 3:
                                showchart(0);
                                save[2] = save[1];
                                save[1] = save[0];
                                save[0] = mvalue;
                                if(averqueue.size()<5)
                                    averqueue.add(mvalue);
                                else
                                {
                                    averqueue.add(mvalue);
                                    average_mvalue=average_edit(averqueue,5);
                                    averqueue.poll();
                                }
                               // average_mvalue = (save[0] + save[2] + save[1]) / 3;
                                //=========================================================
                                viewdata[walkdataindex]=average_mvalue;
                                viewdata_index++;
                                walkdataindex++;
                                txtdata=txtdata+average_mvalue+"\n";
                                //=========================================================
                                if (Math.abs(orentationvalue - init_orentation) > 150 && Math.abs(orentationvalue - init_orentation) < 210 && isturn) {
                                    isturn = false;
                                    turnaroundtime = sensorsec - timespit[1];
                                    timespit[2]=sensorsec;
                                   // sound.play(ding, 1, 1, 0, 0, 1);
                                    Log.v("ori","ori"+orentationvalue+"init"+init_orentation);
                                    Log.v("Time", " sec : " + sec + " walktime : " + walktime
                                            + "step 1 : " + time[0] + "step 2 : " + time[1] + "step 3 : " + time[2] + "step 4 : " + time[3] + "step 5 : " + time[4] +
                                            " turnaround time :  " + turnaroundtime);
                           /* try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                                    //   sound.play(walkbacksound, 1, 1, 0, 0, 1);
                                    txtdata+="E"+walkdataindex+"\n";
                                    gridindex[3]=walkdataindex;
                                    Tugmode = 4;

                                    btreceivedata1+="l";
                                    btreceivedata2+="l";
                                    Log.v("E",walkdataindex+"");
                                }
                                break;
                            case 4:
                                showchart(0);

                                save[2] = save[1];
                                save[1] = save[0];
                                save[0] = mvalue;
                                average_mvalue = (save[0] + save[2] + save[1]) / 3;
                                //=========================================================
                                viewdata[walkdataindex]=average_mvalue;
                                viewdata_index++;
                                walkdataindex++;
                                txtdata=txtdata+average_mvalue+"\n";
                                Log.v("last Y",lastY+"");

                                //=========================================================
                                if (Math.abs(lastY) <2  && mdelta<1) {

                                    record[4] = record[3];
                                    record[3] = record[2];
                                    record[2] = record[1];
                                    record[1] = record[0];
                                    record[0] = Math.abs(lastY);
                                    if (record[1]<3 && record[0] <3 && record[2]<3) {
                                        Log.v("down Y", record[1] + " " + record[2]);
                                        sound.play(ding, 1, 1, 0, 0, 1);
                                        alltime=sensorsec-alltime-(float)0.9;
                                        walktime=walktime-(float)0.2;
                                        turnaroundtime=turnaroundtime-(float)0.2;
                                        backtime = sensorsec - timespit[2]-(float)0.5;
                                        Log.v("Time", " sec : " + sec + " walktime : " + walktime +
                                                "step 1 : " + time[0] + "step 2 : " + time[2] + "step 3 : " + time[3] + "step 4 : " + time[4] + "step 5 : " + time[5] +
                                                " turnaround time :  " + turnaroundtime
                                                + " back time :  " + backtime);

                                      /*  if (getIPAddress() != null) {
                                            Thread t = new Thread(new sendPostRunnable());
                                            t.start();
                                        } else {
                                            Toast.makeText(TUG_MainActivity.this, "目前無連接網路，無法上傳資料", Toast.LENGTH_LONG).show();
                                        }*/
                                        String testanswer;
                                        if(alltime<=15)
                                            testanswer="行動能力良好";
                                        else if(alltime<=20)
                                            testanswer="行動能力遲緩";
                                        else
                                            testanswer="高跌倒風險";

                                        String time="t"+testanswer+"\nt總時間 : "+String.format("%.2f", alltime)+" sec"+
                                                "\nt轉彎時間 : "+String.format("%.2f", turnaroundtime)+" sec"+
                                         "\nt起立時間 : "+String.format("%.2f", uptime)+" sec"+
                                        "\nt行走時間 : "+String.format("%.2f",walktime)+" sec"+
                                         "\nt返回座位時間 : "+String.format("%.2f", backtime);
                                        txtdata+=time+"\n";
                                        for(int i=0;i<5;i++)
                                        {
                                            txtdata+="p"+walkpoint[i]+"\n";
                                            Log.v("walkpoint",walkpoint[i]+"");
                                        }
                                        txtdata+="E"+walkdataindex+"\n";
                                        gridindex[4]=walkdataindex;
                                        Log.v("E", walkdataindex + "");
                                        if(Btmode==0)
                                            writetxtdata();
                                        walkdataindex=0;
                                        //writetxtdata_andGO();
                                        showchart(0);
                                        Tugmode = -1;
                                        btwrite('m',"o");
                                        btwrite('n', "o");

                                        initial_variables();

                                    }
                                }
                                break;
                        }
                        break;
                    case 2: // ten meter test
                        Log.v("time",sensorsec+"");

                        if(sensorsec-sec<=13 && !timeup)
                        {
                            if(sensorsec-sec>=12) {
                                btreceivedata1="";
                                btreceivedata2="";
                                btwrite('m',"a");
                                btwrite('n', "a");
                                Log.v("time", "over" + sensorsec + "");

                                sound.play(ding, 1, 1, 0, 0, 1);
                                sound.play(walksound, 1, 1, 0, 0, 1);
                                save = new double[10];
                                averagedata = new double[10];
                                timeup = true;
                            }
                            mvalue=0;
                        }

                        if(averqueue.size()<5)
                            averqueue.add(mvalue);
                        else
                        {
                            averqueue.add(mvalue);
                            average_mvalue=average_edit(averqueue,5);
                            averqueue.poll();
                        }
                       // average_mvalue = (save[0] + save[2] + save[1]+save[3]+save[4]) / 5;
                        averagedata[4] = averagedata[3];
                        averagedata[3] = averagedata[2];
                        averagedata[2] = averagedata[1];
                        averagedata[1] = averagedata[0];
                        averagedata[0] =average_mvalue;
                        if(timeup )
                        {
                            //  walkdata[walkdataindex]=average_mvalue;
                            walkdataindex++;
                            txtdata=txtdata+average_mvalue+"\n";
                            /*txtdata=txtdata+"x"+lastX+"\n";
                            txtdata=txtdata+"y"+lastY+"\n";
                            txtdata=txtdata+"z"+lastZ+"\n";
                            txtdata=txtdata+"m"+mvalue+"\n";*/
                        }
                        switch (searchmode) {
                            case 1:
                               // if (averagedata[2] > averagedata[1] && averagedata[2] > averagedata[0] &&  averagedata[2] > averagedata[3] && averagedata[2] > averagedata[4] && peak[0] == 0) {
                                    peak[0] = averagedata[2];
                                    searchmode = 2;

                              //  }
                                break;
                            case 2:
                                if (averagedata[2] < averagedata[1] && averagedata[2] < averagedata[0] &&  averagedata[2] < averagedata[3] && averagedata[2] < averagedata[4]) {
                                    if (Math.abs(peak[0] - averagedata[2]) > 0.5) {
                                        valley = averagedata[2];
                                        Log.v("step valley index",walkdataindex+"");
                                        Walkpoint=walkdataindex-3;
                                        second=sensorsec;
                                        searchmode = 3;
                                    } else {
                                        //    Log.v("valley back 1", "valley : " +  save[1] + " peak[0] : " + peak[0]);
                                        peak[0] = 0;
                                        searchmode = 1;
                                    }
                                    Log.v("step valley ",walkdataindex+"");

                                }
                                break;
                            case 3:
                                if (averagedata[2] > averagedata[1] && averagedata[2] > averagedata[0] &&  averagedata[2] > averagedata[3] && averagedata[2] > averagedata[4]) {
                                    if (Math.abs(averagedata[2] - valley) > 0.7) {
                                        //sound.play(ding, 1, 1, 0, 0, 1);
                                        //Log.v("valley get", "valley : " + valley + " peak[0] : " + peak[0] + " peak[1] : " +save[1]);
                                        peak[0] = averagedata[2];
                                        time[stepcount]=second;
                                        //  maintext.setText(maintext.getText()+"\n time "+stepcount+" is "+ time[stepcount]);
                                        walkpoint[stepcount]=Walkpoint;
                                        stepcount++;
                               /*   sound.autoPause();
                                   sound.play(walksound, 1, 1, 0, 0, 1);*/
                                        //  peak[1] = 0;
                                        if(stepcount>=10)
                                        {

                                            sound.play(ding, 1, 1, 0, 0, 1);

                                            walktime=(float)((time[4]-time[2])+(time[6]-time[4])+(time[8]-time[6]))/3;
                                            // maintext.setText(maintext.getText() + "\nwalk time is " + walktime);
                                             walkfreq=4/((time[8]-time[0]));
                                            for(int i=0;i<stepcount;i++)
                                            {
                                                txtdata+="p"+walkpoint[i]+"\n";
                                                Log.v("walkpoint",walkpoint[i]+"");
                                            }
                                            txtdata+="l"+walkcross+"\ns"+String.format("%.2f", walkfreq*walkcross)+"\n"+"";
                                            walkdataindex=0;
                                            if(Btmode==0)
                                                writetxtdata();
                                            btwrite('m',"o");
                                            btwrite('n',"o");
                                            //    Log.v("txt",txtdata);
                                            // writetxtdata_andGO();
                                            searchmode=1;

                                            initial_variables();


                                        }
                                        else
                                            searchmode = 2;
                                    }
                                    else
                                    {
                                        peak[0]=0;
                                        searchmode = 1;
                                    }

                                }
                                break;
                        }
                        break;
                    case 3:  //getup test

                        if(sensorsec-sec<=13 && !timeup)
                        {
                            if(sensorsec-sec>=12) {
                                sound.play(ding, 1, 1, 0, 0, 1);
                                save = new double[10];
                                averagedata = new double[10];
                                timeup = true;
                            }
                            mvalue=0;
                        }
                        else
                        {
                            if(mdelta<=0.4)
                                mdelta=0;
                            save[4] = save[3];
                            save[3] = save[2];
                            save[2] = save[1];
                            save[1] = save[0];
                            save[0] =mdelta;
                            txtdata+=mdelta+"\n";
                            //==========================================================

                            if(mdelta!=0 && save[1]==0 && save[2]==0 && !motiveState)
                            { motiveState=true;

                                alltime=sensorsec;
                                //sound.play(upsound, 1, 1, 0, 0, 1);

                            }
                            else if (motiveState && save[1]==0 && save[2]==0 &&save[3]==0)
                            {


                                alltime=sensorsec-alltime;
                                txtdata+="Gt"+alltime+"\n";
                                Log.v("alltime",alltime+"");
                                writetxtdata();

                                initial_variables();
                                sound.autoPause();
                                sound.play(ding, 1, 1, 0, 0, 1);
                            }
                        }
                        break;
                    case 4: // upstair test
                        if (mdelta <= 0.4)
                            mdelta = 0;
                        //  updata += lastY + " ";
                        record[2] = record[1];
                        record[1] = record[0];
                        record[0] = (float) mdelta;
                        save[4] = save[3];
                        save[3] = save[2];
                        save[2] = save[1];
                        save[1] = save[0];
                        save[0] =lastY;
                        average_mvalue = (save[4] + save[3] +save[0] + save[2] + save[1] ) /5;
                        txtdata+=average_mvalue+"\n";
                        //==========================================================
                        if(sensorsec-sec<=13 && !timeup)
                        {
                            if(sensorsec-sec>=12) {
                                sound.play(ding, 1, 1, 0, 0, 1);
                                save = new double[10];
                                averagedata = new double[10];
                                timeup = true;
                            }
                            mvalue=0;
                        }
                        else
                        {
                        if(mdelta!=0 && record[1]==0 && record[2]==0 && !motiveState)
                        {
                            motiveState=true;
                            oriValue=average_mvalue;
                            lstValue=average_mvalue;
                            alltime=sensorsec;
                            //sound.play(upsound, 1, 1, 0, 0, 1);

                        }
                        else if(motiveState) {
                            txtdata+=average_mvalue+"\n";

                            if (lstValue < average_mvalue) {
                                lstValue = average_mvalue;
                                Log.v("up", " > " + lstValue);
                                Log.v("high", " > " + highvalue);
                                highvalue = lstValue;
                                if (difference < Math.abs(deltaY))
                                    difference = Math.abs(deltaY);
                            } else if (lstValue > average_mvalue && average_mvalue > oriValue) {

                                lstValue = average_mvalue;
                                Log.v("down", " > " + lstValue);
                                if (difference < Math.abs(deltaY))
                                    difference = Math.abs(deltaY);
                            } else if (average_mvalue < oriValue && lstValue > average_mvalue && (oriValue - average_mvalue) > 0.2 && (sensorsec - alltime) > 1) {
                                lstValue = -9999;
                                motiveState = false;
                                // save=new double[10];
                                if (difference < Math.abs(deltaY))
                                    difference = Math.abs(deltaY);
                                alltime = sensorsec - alltime;
                                txtdata+="U"+alltime+"\n";
                                writetxtdata();
                                sound.play(ding, 1, 1, 0, 0, 1);
                                initial_variables();

                            }
                        }
                        }

                        break;
                }
                break;
        }

        if (mags != null && accels != null ) {
            gravity = new float[9];
            magnetic = new float[9];
            SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
            float[] outGravity = new float[9];
            SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X, SensorManager.AXIS_Z, outGravity);
            SensorManager.getOrientation(outGravity, values);
            //   azimuth = values[0] * 57.2957795f;
            azimuth=(float)Math.toDegrees(values[0] +360)%360;
            if(azimuth<0)
                azimuth+=360;


            if( Math.abs(lst_orentation-azimuth)<=30)
                orentationvalue = azimuth;
           // Log.v("degree",azimuth+"");

            lst_orentation=azimuth;

            //Log.v("ori",orentationvalue+"");
            // mvalue=(float)Math.sqrt(accels[0]*accels[0]*+accels[1]*accels[1]+accels[2]*accels[2]);
            // maintext.setText(azimuth+"");
            mags = null;
            accels = null;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public Runnable timercount=new Runnable() {
        @Override
        public void run() {
            sensorcountsec++;
            sensorpoint=(float)sensorcountsec/10;
            sensorsec=sensorsecond+sensorpoint;
            Log.v("timer",sensorsec+"");

            // secondpoint=getTimessecond();
            if(sensorcountsec>=9)
            { sensorsecond= (int)(sensorsec+1);sensorcountsec=-1;
            }
            if(mainmode!=0)
                myTimer.postDelayed(timercount,100);
        }
    };
    public class MyTimerTask extends TimerTask
    {
        public void run()
        {
            sensorcountsec++;
            sensorpoint=(float)sensorcountsec/10;
            sensorsec=sensorsecond+sensorpoint;
            Log.v("timer",sensorsec+"");

            // secondpoint=getTimessecond();
            if(sensorcountsec>=9)
            {
                sensorsecond= (int)(sensorsec+1);sensorcountsec=-1;
            }

        }
    };
    public void showchart(int mode)
    {
        int btdatamax= Btdatasize1>Btdatasize2?Btdatasize1:Btdatasize2;
        testchart.removeAllViews();
        List<double[]> lx = new ArrayList<double[]>(); // 點的x坐標
        List<double[]> ly = new ArrayList<double[]>(); // 點的y坐標

        String[] titles = new String[]{"右腳","左腳",""}; // 定義折線的名稱
        int[] colors = new int[]{Color.RED,Color.BLUE,Color.GREEN};// 折線的顏色
        PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE, PointStyle.CIRCLE, PointStyle.TRIANGLE}; // 折線點的形狀
        XYMultipleSeriesRenderer renderer=null;
        XYMultipleSeriesDataset dataset = null;
        // 數值X,Y坐標值輸入
        switch(mode)
        {
            case 0:
                dx = new double[viewdata_index];
                for (int i = 0; i < viewdata_index; i++) {
                    dx[i] = i;
                }
                lx.clear();
                ly.clear();
                lx.add(dx);
                ly.add(viewdata);

                dataset = buildDatset(1, titles, lx, ly); // 儲存座標值
                renderer = buildRenderer(1, colors, styles, true);
                setChartSettings(renderer, "折線圖展示", "Gait Cycle ( % )", "RSS ( m/sec^2 )", 0, viewdata_index, 0, 20, Color.BLACK);// 定義折線圖
                break;
            default:
                if(Btmode==0) {
                    dx = new double[viewdata_index];
                    for (int i = 0; i < viewdata_index; i++) {
                        dx[i] = i;
                    }
                    lx.clear();
                    ly.clear();
                    Log.v("length viewdatax_index", viewdata_index + "");

                    lx.add(dx);
                    ly.add(viewdata);
                    if (viewdata_index > 300) {
                        lx.add(dx);
                        ly.add(viewdata2);
                        if (fileid == 0) {
                            lx.add(new double[]{92, 250});
                            ly.add(new double[]{5, 5});
                        }
                    } else if(fileinmode!=3){
                        for (int i = 0; i < 5; i++) {
                            lx.add(new double[]{walkpoint[i]});
                            ly.add(new double[]{viewdata[walkpoint[i]]});
                        }
                    }
                }
                else
                {



                }


                switch(fileid)
                {
                    case 1:
                        dataset = buildDatset(1, titles, lx, ly); // 儲存座標值
                        renderer = buildRenderer(1,colors, styles, true);
                        setChartSettings(renderer, "折線圖展示", "Time ( s )", "RSS ( m/sec^2 )", 0, viewdata_index, datamin, datamax, Color.BLACK);// 定義折
                        break;
                    case 0:
                        dataset = buildDatset(3,titles, lx, ly); // 儲存座標值
                        renderer = buildRenderer(3,colors, styles, true);
                        setChartSettings(renderer, "折線圖展示", "\nGait Cycle ( % )", "RSS ( m/sec^2 )", 0, viewdata_index, datamin, datamax, Color.BLACK);
                        break;
                    default:
                        if(Btmode==0 ) {
                            switch(fileinmode)
                            {
                                case 3:
                                    dataset = buildDatset(1, titles, lx, ly); // 儲存座標值
                                    renderer = buildRenderer(1, colors, styles, true);
                                    setChartSettings(renderer, "折線圖展示", "Gait Cycle ( % )", "RSS ( m/sec^2 )", 0, viewdata_index, datamin, datamax, Color.BLACK);// 定義折線圖

                                    break;

                                default:
                                    dataset = buildDatset(2, titles, lx, ly); // 儲存座標值
                                    renderer = buildRenderer(2, colors, styles, true);
                                    setChartSettings(renderer, "折線圖展示", "Gait Cycle ( % )", "RSS ( m/sec^2 )", 0, viewdata_index, datamin, datamax, Color.BLACK);// 定義折線圖
                                    break;
                            }
                        }
                        else
                        {
                            switch(fileinmode)
                            {
                                case 2:
                                    if(Btdatasize1>0) {
                                        dx = new double[Btdatasize1];
                                        for (int i = 0; i < Btdatasize1; i++) {
                                            dx[i] = i;
                                        }
                                        lx.clear();
                                        ly.clear();
                                        lx.add(dx);
                                        ly.add(mspdatamview1);
                                    }
                                    if(Btdatasize2>0) {
                                        dx = new double[Btdatasize2];
                                        for (int i = 0; i < Btdatasize2; i++) {
                                            dx[i] = i;
                                        }
                                        lx.add(dx);
                                        ly.add(mspdatamview2);
                                    }
                                    Log.v("Btdatasize1 2", Btdatasize1 + " "+Btdatasize2 + " "+lx.size());
                                    if(lx.size()>0) {
                                        dataset = buildDatset(2, titles, lx, ly); // 儲存座標值
                                        renderer = buildRenderer(2, colors, styles, true);
                                        setChartSettings(renderer, "折線圖展示", "Gait Cycle ( % )", "RSS ( m/sec^2 )", 0, btdatamax, datamin, datamax, Color.BLACK);// 定義折線圖
                                    }
                                    else
                                    {
                                        dataset = buildDatset(0, titles, lx, ly); // 儲存座標值
                                        renderer = buildRenderer(0, colors, styles, true);
                                        Toast.makeText(TestActivity.this, "無此感測器訊號資料", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 1:
                                    if(Btdatasize1>0) {
                                        dx = new double[Btdatasize1];
                                        for (int i = 0; i < Btdatasize1; i++) {
                                            dx[i] = i;
                                        }
                                        lx.clear();
                                        ly.clear();
                                        lx.add(dx);
                                        ly.add(mspdatam1);
                                    }
                                    if(Btdatasize2>0) {
                                        dx = new double[Btdatasize2];
                                        for (int i = 0; i < Btdatasize2; i++) {
                                            dx[i] = i;
                                        }
                                        lx.add(dx);
                                        ly.add(mspdatam2);
                                    }
                                    Log.v("Btdatasize1 2", Btdatasize1 + " "+Btdatasize2 + " "+lx.size());
                                    if(lx.size()>0 && Btdatasize1>0 && Btdatasize2>0) {
                                        switch(showmode)
                                        {
                                            case 1:
                                                titles = new String[]{"左腳"};
                                                colors = new int[]{Color.BLUE};
                                                lx.remove(1);
                                                ly.remove(1);
                                                break;
                                            case 2:
                                                titles = new String[]{"右腳"};
                                                colors = new int[]{Color.RED};
                                                lx.remove(0);
                                                ly.remove(0);
                                                break;
                                        }
                                        dataset = buildDatset(lx.size(), titles, lx, ly); // 儲存座標值
                                        renderer = buildRenderer(lx.size(), colors, styles, true);
                                        setChartSettings(renderer, "折線圖展示", "Gait Cycle ( % )", "RSS ( m/sec^2 )", 0, btdatamax, datamin, datamax, Color.BLACK);// 定義折線圖
                                    }
                                    else
                                    {
                                        dataset = buildDatset(0, titles, lx, ly); // 儲存座標值
                                        renderer = buildRenderer(0, colors, styles, true);
                                        Toast.makeText(TestActivity.this, "無此感測器訊號資料", Toast.LENGTH_SHORT).show();

                                    }
                                    break;
                                default:
                                    dataset = buildDatset(0, titles, lx, ly); // 儲存座標值
                                    renderer = buildRenderer(0, colors, styles, true);
                                    break;
                            }

                        }
                        break;
                }
                break;
        }


        testview=ChartFactory.getLineChartView(context, dataset, renderer);
        // init_tooltip();
        View chart = ChartFactory.getLineChartView(context, dataset, renderer);
        testchart.removeAllViews();
        testchart.addView(testview);
    }
    // 定義折線圖名稱
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {

        renderer.setXTitle(xTitle); // X軸名稱
        renderer.setLabelsTextSize(18);
        renderer.setAxisTitleTextSize(24);
        renderer.setYTitle(yTitle); // Y軸名稱
        renderer.setXAxisMin(xMin); // X軸顯示最小值
        renderer.setXAxisMax(xMax); // X軸顯示最大值
        renderer.setXLabelsColor(Color.BLACK); // X軸線顏色
        renderer.setXLabels(0);
        renderer.setLegendTextSize(24);

        renderer.setYAxisMin(yMin); // Y軸顯示最小值
        renderer.setYAxisMax(yMax); // Y軸顯示最大值
        renderer.setAxesColor(axesColor); // 設定坐標軸顏色
        renderer.setYLabelsColor(0, Color.BLACK); // Y軸線顏色
        renderer.setPointSize(1);

        renderer.setLabelsColor(Color.BLACK); // 設定標籤顏色
        renderer.setMarginsColor(Color.WHITE); // 設定背景顏色
        renderer.setShowGrid(true); // 設定格線
        renderer.setShowGridX(false);

        renderer.setGridColor(Color.BLACK);
        renderer.setShowCustomTextGrid(true);

        renderer.setClickEnabled(false);

        //  renderer.setSelectableBuffer(10);
        renderer.setZoomButtonsVisible(true);//设置可以缩放
        //   renderer.setInScroll(true);
        renderer.setPanEnabled(true, true);
        switch( fileid )
        {
            case 1:
                renderer.addTextLabel(92,"100%");
                renderer.addTextLabel(250,"200%");
                renderer.addTextLabel(380,"300%");
                renderer.setShowLegend(true);
                break;
            case 2:
                renderer.setShowLegend(false);
                renderer.addTextLabel(1," 開始                     ");
                renderer.addTextLabel(23," 起立時間    ");
                renderer.addTextLabel(490,"行走時間                                                                 ");
                renderer.addTextLabel(540,"  轉彎時間");
                renderer.addTextLabel(950,"返回座位時間                    ");
                break;
            default:
                switch (fileinmode)
                {
                    case 0:
                        renderer.setShowLegend(true);
                        renderer.addTextLabel(1, " 開始 ");
                        switch(Tugmode)
                        {
                            case 2:
                                renderer.addTextLabel(gridindex[1], "起立時間           ");
                                break;
                            case 3:
                                renderer.addTextLabel(gridindex[1], "起立時間           ");
                                renderer.addTextLabel(gridindex[2], "行走時間                             ");
                                break;
                            case 4:
                                renderer.addTextLabel(gridindex[1], "起立時間           ");
                                renderer.addTextLabel(gridindex[2], "行走時間                             ");
                                renderer.addTextLabel(gridindex[3], "  轉彎時間                     ");
                                break;
                        }
                        break;
                    case 1:
                        if(Btmode==0) {
                            renderer.setShowLegend(true);
                            renderer.addTextLabel(1, " 開始 ");
                            renderer.addTextLabel(gridindex[0], "起立時間           ");
                            renderer.addTextLabel(gridindex[1], "行走時間                             ");
                            renderer.addTextLabel(gridindex[2], "  轉彎時間                     ");
                            renderer.addTextLabel(gridindex[3], "返回座位時間                ");
                        }
                        else
                        {
                            renderer.setShowLegend(true);
                            renderer.addTextLabel(1, " 開始 ");
                            renderer.addTextLabel(mspwalkpoint[0], "起立時間                      ");
                            renderer.addTextLabel(mspwalkpoint[1], "行走時間                                                          ");
                            renderer.addTextLabel(mspwalkpoint[2], "  轉彎時間                                  ");
                            renderer.addTextLabel(Btdatasize1, "返回座位時間          ");
                        }
                        break;
                    case 2:
                        String[] per2=new String[]{"L0","L1","L2","L3","L4","L5","L6","L7","L8","L9"};

                        // String[] per=new String[]{"0","1","2","3","4","5","6","7","8","9"};
                        String[] per=new String[]{"100%","200%","300%","400%","500%","1","2","3","4","5","1","2","3","4","5"};
                        renderer.setShowLegend(false);
                        renderer.addTextLabel(1, " 開始 ");
                        if(Btmode==0) {
                            for(int i=0;i<10;i++)
                            {
                                // if(i%2==0)
                                renderer.addTextLabel(walkpoint[i], per[i]);
                            }
                        }
                        else
                        {
                            Log.v("peak_index", peak_index + "");
                            renderer.setShowLegend(true);
                            for(int i=0;i<=9;i++)
                            {
                                if(i%2!=0 &&mspwalkpoint[i]!=0)
                                {
                                    //renderer.addTextLabel(mspwalkpoint2[i], per2[i]);Log.v("grid",per[i]+" point "+walkpoint[i]);
                                    renderer.addTextLabel(mspwalkpoint[i]-firstpeak, per[i/2]);
                                }
                            }
                        }

                        break;
                }
                break;

        }


    }

    // 定義折線圖的格式
    private XYMultipleSeriesRenderer buildRenderer(int length,int[] colors, PointStyle[] styles, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        // int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();



            r.setColor(colors[i]);

            r.setPointStyle(styles[0]);
            r.setFillPoints(fill);
            renderer.addSeriesRenderer(r); //將座標變成線加入圖中顯示
        }

        return renderer;
    }

    // 資料處理
    private XYMultipleSeriesDataset buildDatset(int length,String[] titles, List<double[]> xValues, List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        //  int length = titles.length; // 折線數量
        for (int i = 0; i < length; i++) {
            // XYseries對象,用於提供繪製的點集合的資料
            XYSeries series = new XYSeries(titles[i]); // 依據每條線的名稱新增
            double[] xV = xValues.get(i); // 獲取第i條線的資料
            double[] yV = yValues.get(i);
            int seriesLength = xV.length; // 有幾個點
            //  Log.v("length seriesLength",seriesLength+"");
            for (int k = 0; k < seriesLength; k++) // 每條線裡有幾個點
            {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
    public void init_tooltip()
    {
        //================== vie tool ===================================
        final Context contextTemp = context;
        final int toolTipWidth = 74;
        final int toolTipHeight = 51;



        //  toolTipHeight = dip2px(contextTemp, toolTipHeight);
        // toolTipWidth = DensityUtil.dip2px(contextTemp, toolTipWidth);

        LayoutInflater inflater = LayoutInflater.from(contextTemp);
       // layout = inflater.inflate(R.layout.chart_tooltext, null);
        // 获取toolTip中两个TextView,这两个TextView的值会不断变化
        //final TextView distanceTotal = (TextView)layout.findViewById(R.id.distance_total);
       // final TextView calorieTotal = (TextView)layout. findViewById(R.id.calorie_total);
        // testchart.setClickable(true);
        // testview.setClickable(true);
        testview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                MotionEvent motionEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN, event.getX(), event.getY(),
                        event.getMetaState());
                testview.onTouchEvent(motionEvent);
                testview.setHorizontalScrollBarEnabled(true);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SeriesSelection seriesSelection = testview.getCurrentSeriesAndPoint();
                        double[] xy = testview.toRealPoint(0);
                        if (seriesSelection != null) {
                           // distanceTotal.setText(dx[seriesSelection.getPointIndex()] + "");
                            //   calorieTotal.setText((int) viewdatax[seriesSelection.getPointIndex()] + "");

                            double[] clickPoint = testview.toRealPoint(0);

                            double xValue = seriesSelection.getXValue();// 基准点的x坐标
                            double yValue = seriesSelection.getValue();// 基准点的y坐标

                            double xPosition = event.getRawX() - event.getX() + dx[1] + ((event.getX() - dx[1]) * xValue / clickPoint[0]);
                            double yPosition = event.getRawY() - event.getY() + dx[0]
                                    + ((event.getY() - dx[0]) * (datamax - yValue) / (datamax - clickPoint[1]));
                            int xOffset = (int) (xPosition - toolTipWidth / 2);
                            // 减去7个dip是为了让poupup和点之间的距离高一点。
                            int yOffset = (int) (yPosition - toolTipHeight);

                            if (popup != null) {
                                popup.dismiss();
                            }
                            popup = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            popup.showAtLocation(testview, Gravity.NO_GRAVITY, xOffset, yOffset);
                            Toast.makeText(
                                    TestActivity.this,
                                    "Chart element in series index " + seriesSelection.getSeriesIndex()
                                            + " data point index " + seriesSelection.getPointIndex() + " was clicked"
                                            + " closest point value X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue()
                                            + " clicked point value X=" + (float) xy[0] + ", Y=" + (float) xy[1], Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(TestActivity.this, "click!!!", Toast.LENGTH_SHORT).show();
                            if (popup != null) {
                                popup.dismiss();
                            }
                            //  testview.setBottom(0);
                          /*  testview.setTop(20);
                            testview.setLeft(20);
                            testview.setRight(0);*/
                            //testview.repaint();
                        }

                        break;
                    case MotionEvent.ACTION_SCROLL:
                        Toast.makeText(TestActivity.this, "scroll", Toast.LENGTH_SHORT).show();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Toast.makeText(TestActivity.this, "move", Toast.LENGTH_SHORT).show();
                        Log.v("action", "move");
                        break;
                }

                // SeriesSelection seriesSelection = ((RelativeLayout) v);

                //当点击的位置是对应某一个点时，开始获取该点处的数据，并且弹出PopupWindow.
                  /* if (seriesSelection != null) {
                        distanceTotal.setText(seriesSelection.getValue() + "");
                        calorieTotal.setText((int) carolies[seriesSelection.getPointIndex()] + "");
                        Log.i("data", seriesSelection.getValue() + " " + carolies[seriesSelection.getPointIndex()]);
                        //以下代码是为了计算tooltip弹出的位置。
                        // 实际点击处的x,y坐标
                        double[] clickPoint = chartView.toRealPoint(0);

                        double xValue = seriesSelection.getXValue();// 基准点的x坐标
                        double yValue = seriesSelection.getValue();// 基准点的y坐标

                        double xPosition = event.getRawX() - event.getX() + margin[1] + ((event.getX() - margin[1]) * xValue / clickPoint[0]);
                        double yPosition = event.getRawY() - event.getY() + margin[0]
                                + ((event.getY() - margin[0]) * (renderer.getYAxisMax() - yValue) / (renderer.getYAxisMax() - clickPoint[1]));
                        int xOffset = (int) (xPosition - toolTipWidth / 2);
                        // 减去7个dip是为了让poupup和点之间的距离高一点。
                        int yOffset = (int) (yPosition - toolTipHeight - DensityUtil.dip2px(contextTemp, 7));
                        initPopupWindow(contextTemp);

                        popup.showAtLocation(viewParent, Gravity.NO_GRAVITY, xOffset, yOffset);

                    } else { //当点击的位置不是图表上折点的位置时，如果上一次点击弹出的popup还存在，就把它dismiss掉。
                        if (popup != null) {
                            popup.dismiss();
                        }
                    }*/


                return true;
            }
        });
    }

    private void writetxtdata() {
        try {
            File mSDFile = Environment.getExternalStorageDirectory();

            //Log.v("filewriter ", txtdata);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH_mm");
            Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
            String filename;
            filename = formatter.format(curDate);
            Log.v("mainmode",mainmode+"");
            Thread t;
            switch (mainmode) {
                case 1:
                    filename = "起立行走測驗"+filename ;//+ filename;
                    t=new Thread(new sendPostRunnable(1));
                    t.start();
                    break;
                case 2:
                    filename = "10公尺走路測驗" + filename;
                    t=new Thread(new sendPostRunnable(2));
                    t.start();
                    break;
                case 3:
                    filename = "BADL撿物品測驗" + filename;
                    t=new Thread(new sendPostRunnable(3));
                    t.start();
                case 4:
                    filename = "BADL爬樓梯測驗" + filename;
                    t=new Thread(new sendPostRunnable(4));
                    t.start();
                    break;
            }
            viewfilename=filename;

            File mFile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/");
            Log.v("Sdfile",mSDFile.getParent() + "/" + mSDFile.getName());
            //若沒有檔案儲存路徑時則建立此檔案路徑
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
            Log.v("filewriter ", "1");

            FileWriter mfilewriter;
            mfilewriter = new FileWriter(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/"+filename  + ".txt",false);
            Log.v("filewriter ", "2");

            mfilewriter.write(txtdata);
            Log.v("filewriter ", "3");

            mfilewriter.close();

            //MainActivity.filelist.add(filename);
            Log.v("filewriter ", "4");

            Log.v("filename", filename);
            showresult();
        }
        catch (Exception e)
        {
            Log.v("filewriter error",e.toString());
        }

    }
    //=========================================================================================================
    private String[] uriAPI =new String[]{"","http://192.168.1.227/test/tug1.php","http://192.168.1.227/test/tenwalk.php"} ;

    private String senddata(int mode)
    {
        HttpPost httpRequest = new HttpPost(uriAPI[mode]);
      /*
       * Post運作傳送變數必須用NameValuePair[]陣列儲存
       */
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        switch(mode)
        {
            case 1:
                Log.v("senddata", String.valueOf(alltime) + "_" + turnaroundtime + "_" + backtime);
                params.add(new BasicNameValuePair("username", surface.username));
                params.add(new BasicNameValuePair("sec",String.valueOf(alltime) ));
                params.add(new BasicNameValuePair("uptime",String.valueOf(uptime) ));
                params.add(new BasicNameValuePair("walktime",String.valueOf(walktime) ));
                params.add(new BasicNameValuePair("turnaroundtime",String.valueOf(turnaroundtime) ));
                params.add(new BasicNameValuePair("walkbacktime",String.valueOf(walkbacksound) ));
                params.add(new BasicNameValuePair("walk",String.valueOf(txtdata) ));
                Log.v("txtdata",txtdata);
                //params.add(new BasicNameValuePair("walkdata",String.valueOf(backtime) ));
                break;
            case 2:
                params.add(new BasicNameValuePair("username",surface.username));
                params.add(new BasicNameValuePair("walkfreq",String.valueOf(walkfreq*walkcross) ));
                params.add(new BasicNameValuePair("walkcross",String.valueOf(walkcross) ));
                params.add(new BasicNameValuePair("ccnum",String.valueOf(ccnum) ));
                params.add(new BasicNameValuePair("walk",String.valueOf(txtdata) ));
                break;
            case 3:
                break;
            case 4:
                break;

        }
        try
        {
          /* 發出HTTP request */
            httpRequest.setEntity(new UrlEncodedFormEntity( params, HTTP.UTF_8));
          /* 取得HTTP response */
            HttpResponse httpResponse = new DefaultHttpClient()
                    .execute(httpRequest);
          /* 若狀態碼為200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
             /* 取出回應字串 */
                String strResult = EntityUtils.toString(httpResponse
                        .getEntity());
                // 回傳回應字串
                return strResult;

            }
        } catch (Exception e)

        {
            Log.v("senddataerror",e.toString());
            e.printStackTrace();

        }
        return null;
    }
    class sendPostRunnable implements Runnable
    {
        int mode=1;
        public sendPostRunnable(int inmode)
        {
            mode=inmode;

        }
        @Override
        public void run()
        {
            Log.v("sendpost","");
            String result = senddata(mode);
            mHandler.obtainMessage(surface.REFRESH_DATA, result).sendToTarget();
        }

    }
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 顯示網路上抓取的資料
                case surface.REFRESH_DATA:
                    String result = null;
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if (result != null) {
                        // 印出網路回傳的文字
                        Log.v("sendresult", result);
                      //  Toast.makeText(TestActivity.this, result, Toast.LENGTH_LONG).show();
                    }
                        break;
            }
        }
    };
    //========================================================================================================================
    private void readdata(String filename)
    {

        try
        {
            //取得SD卡儲存路徑
            File mSDFile = Environment.getExternalStorageDirectory();
            //讀取文件檔路徑
            File file = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + filename + ".txt");
            if(file.exists()) {
                Log.v("file","exist");

                FileReader mFileReader = new FileReader(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + filename + ".txt");


                BufferedReader mBufferedReader = new BufferedReader(mFileReader);
                String mReadText = "";
                String mTextLine = mBufferedReader.readLine();


                //一行一行取出文字字串裝入String裡，直到沒有下一行文字停止跳出

                //============================================================
                // r , t , w , l , s , p , x
                int index = 0,mspindex1=0,mspindex2=0,pointindex=0;
                double max=0;
                viewdata_index=0;
                peak_index=0;peak_index2=0;
              //  Queue peakqueue = new LinkedList(),peakqueu2 = new LinkedList();

                while (mTextLine != null) {
                    //  Log.v("readline",mTextLine);
                    switch (mTextLine.charAt(0)) {

                        case 'G':
                            answer += "拿起花費時間" + mTextLine.substring(2) + "\n";
                            break;
                        case 'U':
                            answer += "花費時間" + mTextLine.substring(1) + "\n";
                            break;
                        case 'C':
                            if(max< Double.valueOf(mTextLine.substring(1)))
                                max= Double.valueOf(mTextLine.substring(1));
                            break;
                        case 't':
                            answer += mTextLine.substring(1) + "\n";
                            break;
                        case 'w':
                            answer += "\n步頻" + mTextLine.substring(1);
                            break;
                        case 'l':
                            answer += "跨步長 " + mTextLine.substring(1) + " cm";
                            break;
                        case 's':
                            answer += "\n走路速度 " + mTextLine.substring(1) + " cm/sec";
                            break;
                        case 'p':
                            Log.v("walkpoint", mTextLine.substring(1));
                            walkpoint[pointindex] = Integer.valueOf(mTextLine.substring(1));
                            pointindex++;
                            break;
                        case 'h':
                            firstpeak= Integer.valueOf(mTextLine.substring(1));
                            break;
                        case 'L':
                            mspwalkpoint[peak_index]=Integer.valueOf(mTextLine.substring(1));
                            Log.v("TUG point ",peak_index+" "+ mspwalkpoint[peak_index]);
                            peak_index++;
                            break;
                        case 'W':
                            mspwalkpoint[peak_index] = Integer.valueOf(mTextLine.substring(1));
                            Log.v("peak_index1",peak_index+" "+mspwalkpoint[peak_index]);

                         /* if(peak_index>=4) {
                                mspwalkpoint[peak_index] = mspwalkpoint[peak_index] - mspwalkpoint[3];
                               // Log.v("peak_index1",peak_index+" "+mspwalkpoint[peak_index]+" "+mspwalkpoint[3]);
                            }*/
                            peak_index++;
                            break;
                        case 'H':
                            mspwalkpoint2[peak_index2] = Integer.valueOf(mTextLine.substring(1));
                            peak_index2++;
                            Log.v("peak_index2",peak_index2+"");
                            break;
                        case 'E':
                            gridindex[index] = Integer.valueOf(mTextLine.substring(1));
                            index++;
                            Log.v("gridindex", gridindex[index - 1] + mTextLine.substring(1));

                            break;
                        case 'B':
                            Btmode = 1;
                            break;
                        case 'm':
                            // mspdatam[mspindex]=Double.valueOf(mTextLine.substring(1));
                          /*  if(q.size()<=30)
                                q.add(Double.valueOf(mTextLine.substring(1)));
                            mspdatam[index]=average(q);
                            if(q.size()>30)
                                q.remove();
                            Log.v("Queue",q.size()+"");*/
                           //if (peak_index >= 4 && peak_index<=7)
                            {
                                mspdatam1[mspindex1] = Double.valueOf(mTextLine.substring(1));
                         /*   if(fileinmode==2) {
                                if(mspindex1>30)
                                    peakqueue.add(mspdatam1[mspindex1]);
                                if (peakqueue.size() >= 21 && mspindex1 > 30) {
                                    boolean ispeak = true;
                                    ArrayList arraylist = new ArrayList(peakqueue);
                                    for (int s = 1; s <= 10; s++) {
                                        if ((double) arraylist.get(10 + s) < (double) arraylist.get(10) || (double) arraylist.get(10 - s) < (double) arraylist.get(10)) {
                                            ispeak = false;
                                            break;
                                        }

                                    }
                                    peakqueue.poll();
                                    if (ispeak && Math.abs((double) arraylist.get(10) - (double) arraylist.get(0)) > 0.2) {
                                        mspwalkpoint[peakindex] = mspindex1 - 11;
                                        peakindex++;
                                        Log.v("ispeak1", arraylist.get(10) + " index is " + mspindex1);
                                    }
                                }
                            }*/
                            mspindex1++;
                    }
                            break;
                        case 'n':
                            // mspdatam[mspindex]=Double.valueOf(mTextLine.substring(1));
                          /*  if(q.size()<=30)
                                q.add(Double.valueOf(mTextLine.substring(1)));
                            mspdatam[index]=average(q);
                            if(q.size()>30)
                                q.remove();
                            Log.v("Queue",q.size()+"");*/
                            //mspindex2size++;
                           // if (mspindex2size > mspwalkpoint[3] && mspindex2size<= mspwalkpoint[3]+mspindex1)
                            {
                                mspdatam2[mspindex2] = Double.valueOf(mTextLine.substring(1));
                                mspwalkpoint2[peak_index2]-= mspwalkpoint2[3];
                          /*  if(fileinmode==2) {
                                if (mspindex2 > 30)
                                    peakqueu2.add(mspdatam2[mspindex2]);
                                if (peakqueu2.size() >= 21) {
                                    boolean ispeak = true;
                                    ArrayList arraylist = new ArrayList(peakqueu2);
                                    for (int s = 1; s <= 10; s++) {
                                        if ((double) arraylist.get(10 + s) < (double) arraylist.get(10) || (double) arraylist.get(10 - s) < (double) arraylist.get(10)) {
                                            ispeak = false;
                                            break;
                                        }

                                    }
                                    peakqueu2.poll();
                                    if (ispeak && Math.abs((double) arraylist.get(10) - (double) arraylist.get(0)) > 0.3) {
                                        mspwalkpoint2[peakindex1] = mspindex2 - 11;
                                        peakindex1++;
                                        Log.v("ispeak2", arraylist.get(10) + " index is " + mspindex2);
                                    }
                                }

                            }*/
                                mspindex2++;
                            }
                            break;

                        default:
                            viewdata[viewdata_index] = Double.valueOf(mTextLine);
                            if (viewdata[viewdata_index] > datamax)
                                datamax = viewdata[viewdata_index];
                            else if (viewdata[viewdata_index] < datamin)
                                datamin = viewdata[viewdata_index];
                            viewdata_index++;
                            break;
                    }
                    //   mReadText += mTextLine+"\n";
                    mTextLine = mBufferedReader.readLine();
                }
                Btdatasize1=mspindex1-1;
                Btdatasize2=mspindex2-1;
                Log.v("Btdatasize1",Btdatasize1+"");
                //syncmsp();
                int Max=Btdatasize1<Btdatasize2?Btdatasize2:Btdatasize1;
                ratio=Max/viewdata_index;
                Log.v("fileindmode read",fileinmode+"");
                if(fileinmode==2) {
                   mspviewtake();
                }
                if(max>0.7)
                answer += "\n步態對稱性係數 : " + max+"\n\n高度相關";
                else if(max>0.4)
                    answer += "\n步態對稱性係數 : " + max+"\n\n中度相關";
                else
                    answer += "\n步態對稱性係數 : " + max+"\n\n低度相關";

                //  Log.v("mspindex",mspindex1+" "+mspindex2);
                // Log.v("viewdata_index",viewdata_index+" ratio : "+ratio);
            }

        }
        catch(Exception e)
        {
            Log.v("readdata error",e.toString());

        }
    }
    public void initial_variables() {
//=========boolean=============================

        timer.cancel();
        up1 = false;
        iswalked1 = false;
        isturn = false;
        motiveState = false;
        timeup = false;
        processState = false;

        //====value======================================
        init_orentation = 0;
        lstValue = 0;
        sensorsec = 0;
        stepcount = 0;
        peak[0] = 0;
//=========time===========================
       /* uptime=0;
        alltime=0;
        backtime=0;
        turnaroundtime=0;
        walktime=0;*/
        sec = 0;
        save = new double[10];
        record = new float[10];
        averagedata = new double[10];
        walkpoint = new int[10];
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magntic);
        searchmode = 1;

        try {
            if (btreceivedata1.length() > 100)
                decode('m');
            if (btreceivedata2.length() > 100)
                decode('n');
        } catch (Exception x) {
            Log.v("decodeerror", x.toString());
        }
        try {

            Log.v("receive data1", btreceivedata1);
            Log.v("receive data2", btreceivedata2);
            btreceivedata1 = "";
            btreceivedata2 = "";
            int shiftvalue = 0;
            if (mainmode == 2) {
                CCnorm(1, 3,1,3);
                CCnorm(2, 4,2,4);
                CCnorm(3, 5,3,5);
                CCnorm(4, 6,3,5);
                CCnorm(5, 7,5,7);
                CCnorm(6, 8,5,7);
                CCnorm(7, 9,6,8);
                for (int i = 1; i <= 9; i += 2) {
                    shiftvalue += Math.abs(mspwalkpoint2[i] - mspwalkpoint[i]);
                    Log.v("mspwalk point 1 ", mspwalkpoint[i] + "");
                    Log.v("mspwalk point 2 ", mspwalkpoint2[i] + "");
                    Log.v("mspwalk point dec ", mspwalkpoint2[i] - mspwalkpoint[i] + "");
                }
                shiftvalue = shiftvalue / 5;
                Log.v("shift value", shiftvalue + "");
                syncmsp(shiftvalue);

            }
        } catch (Exception x) {
            Log.v("init error", x.fillInStackTrace().toString());
        }
        if (Btmode == 1) {

            writetxtdata();
        }

        mainmode = 0;

        try {
            if (connect1 != null) {
                connect1.cancel();
                connect1.flag = false;
            }
            if (connect2 != null) {
                connect2.cancel();
                connect2.flag = false;
            }
            //  sound.release();
            // sound.play(endsound, 1, 1, 0, 0, 1);

            mediaPlayer.stop();
            mediaPlayer.prepare();
            mediaPlayer.start();
            btn_work.setVisibility(View.VISIBLE);

    }
        catch (Exception x)
        {Log.v("init error",x.fillInStackTrace().toString());}

    }
    public double magnitude(float x, float y, float z) {
        double magnitude = 0;
        magnitude = Math.sqrt(x * x + y * y + z * z);
        return magnitude;
    }
    public double magnitude2(float x, float y) {
        double magnitude = 0;
        magnitude = Math.sqrt(x * x + y * y );
        return magnitude;
    }
    private void writetxtdataorigin() {
        try {
            File mSDFile = Environment.getExternalStorageDirectory();


            File mFile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP");
            //若沒有檔案儲存路徑時則建立此檔案路徑
            if (!mFile.exists()) {
                mFile.mkdirs();
            }





            FileWriter mfilewriter;
            mfilewriter = new FileWriter(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + viewfilename + ".txt");
          /*  if(fileid==0)
                 mfilewriter.write(oridata3stepright);
            else if(fileid==1)
                mfilewriter.write(oridatatTUG);*/
            mfilewriter.close();
            // MainActivity.filelist.add(filename);
            //Log.v("filename", filename);
            //viewfilename=filename;
        }
        catch (Exception e)
        {
            Log.v("filewriter error",e.toString());
        }

    }
    public void ShowAlertMessage(String message)
    {
        new AlertDialog.Builder(TestActivity.this)
                .setTitle("程式執行發生錯誤")
                .setMessage(message)
                        //.setMessage("選擇一裝置進行配對")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
    private void syncmsp(int shiftvalue)
    {
        //  int shift=mspwalkpoint2[0]-mspwalkpoint[0];

        for(int i=0;i<Btdatasize2-shiftvalue;i++)
        {
            mspdatam2[i]=mspdatam2[i+shiftvalue];
        }
        Btdatasize2=Btdatasize2-shiftvalue;
    }
    private void mspviewtake()
    {
        int index=0,shiftvalue=Math.abs(mspwalkpoint[0]-mspwalkpoint2[0]),lastvalley=0;
        if(mspwalkpoint[9]!=0)
            lastvalley=mspwalkpoint[9];
        else if(mspwalkpoint[7]!=0)
            lastvalley=mspwalkpoint[7];
        else if(mspwalkpoint[5]!=0)
            lastvalley=mspwalkpoint[5];

        Log.v("mspviewtake",mspwalkpoint[9]+" "+firstpeak);
        if(lastvalley!=0 && firstpeak!=0) {
            Btdatasize1 =lastvalley- firstpeak;

            for (int i = firstpeak; i <= lastvalley; i++) {
                mspdatamview1[index] = mspdatam1[i];
                mspdatamview2[index] = mspdatam2[i + shiftvalue];
                index++;
            }
        }
    }
    //====================================== BlueTooth =================================================
    private void Bluetoothopen()
    {
        List<String> BTpair;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled())
        {
            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }
        BTpair = new ArrayList<>();

        if (!mBluetoothAdapter.isEnabled()) {

            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            new AlertDialog.Builder(TestActivity.this)
                    .setTitle("藍芽尚未開啟")
                    .setMessage("Open bluetooth")
                    .setPositiveButton("開啟藍芽", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult( enableIntent,1);
                            mmDevice=mBluetoothAdapter.getRemoteDevice("20:15:09:01:89:93");
                            nnDevice=mBluetoothAdapter.getRemoteDevice("20:16:04:11:03:13");
                            try {
                                socketConnect();
                            }
                            catch (Exception e)
                            {Log.v("FindBT",e.toString());}

                        }
                    })
                    .show();
// Otherwise, setup the chat session
        }else {
            //  btview.setText("Bluttooth 已開啟");
            mmDevice=mBluetoothAdapter.getRemoteDevice("20:15:09:01:89:93");
            nnDevice=mBluetoothAdapter.getRemoteDevice("20:16:04:11:03:13");
            try {
                socketConnect();
            }
            catch (Exception e)
            {Log.v("FindBT",e.toString());}
        }


    }
    private void socketConnect()
    {
        String s = "d";
        boolean btconnectgood1=true,btconnectgood2=true;
        try {
            Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            mmSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
            mmSocket.connect();
            Log.v("OpenBT", "mmsocket");
            mmOutputStream = mmSocket.getOutputStream();
            connect1 = new ConnectedThread(mmSocket);
            connect1.start();
            s = "d";
            mmOutputStream.write(s.getBytes());
        }
        catch (Exception x)
        {
            btconnectgood1=false;

        }
        try {
            Method mm = nnDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            nnSocket = (BluetoothSocket) mm.invoke(nnDevice, 1);
            nnSocket.connect();
            Log.v("OpenBT", "nnsocket");
            nnOutputStream = nnSocket.getOutputStream();
            connect2 = new ConnectedThread(nnSocket);
            connect2.start();

            s = "x";
            nnOutputStream.write(s.getBytes());
        }
        catch (Exception x)
        {
            btconnectgood2=false;
        }
        if(!btconnectgood1 || !btconnectgood2)
        {
            if(!btconnectgood1 && !btconnectgood2)
                Toast.makeText(TestActivity.this, "無穿戴式裝置模式", Toast.LENGTH_SHORT).show();
            else if(!btconnectgood1)
                Toast.makeText(TestActivity.this, "無穿戴式裝置模式1", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(TestActivity.this, "無穿戴式裝置模式2", Toast.LENGTH_SHORT).show();


        }
        else
        {
            Btmode=1;
            txtdata+="B\n";
        }


    }
    public Runnable openBT=new Runnable() {
        @Override
        public void run() {
            Log.v("OpenBT", "socketcreat");
            if (mmDevice != null) {
                try {

                    // mmSocket = mmDevice.createRfcommSocketToServiceRecord(BLUETOOTH_SPP_UUID);
                    Log.v("OpenBT", "socketcontect");
                    Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                    mmSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
                    mmSocket.connect();

                    if (mmSocket.isConnected()) {
                        Log.v("Bluetooth","成功連接 ");
                        //lksend_Handler.postDelayed(begin_send_lk, 5000);

                    }
                    else
                        Log.v("Bluetooth", "連接失敗 ");
                    Log.v("Bluetooth", "與 " + mmDevice.getName() + " 成功連接");

                    mmOutputStream = mmSocket.getOutputStream();
                    // beginListenForData(); //開始傾聽藍芽裝置的資料

                }
                catch(Exception e)
                {Log.v("OpenBT",e.toString());}
            }
            else
                tryconnect_handler.postDelayed(openBT,1000);
        }

    };
    public class ConnectedThread extends Thread {
        private final BluetoothSocket ttSocket;
        private final InputStream mmInStream;
        private Handler connecthandler;
        byte[] readBuffer;
        private boolean flag=true;
        public ConnectedThread(BluetoothSocket socket) {
            Log.d("connect", "create ConnectedThread");
            ttSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

            } catch (IOException e) {
                Log.e("connect", "temp sockets not created", e);
            }

            mmInStream = tmpIn;


        }
        int bytes =0;
        public void run() {
            Log.i("connect", "BEGIN mConnectedThread");


            // Keep listening to the InputStream while connected
            while (flag) {
                try {
                    // Read from the InputStream
                    if(ttSocket.isConnected())
                        bytes= mmInStream.available();
                    //  byte[] buffer = new byte[bytes];
                    //  Log.i(TAG, "mmInStream.available()"+mmInStream.available() );
                    readBuffer = new byte[bytes];
                    if (bytes > 0) {
                        bytes = mmInStream.read(readBuffer);
                        final String str =  new String(readBuffer);;

                        if(ttSocket.getRemoteDevice()==mmSocket.getRemoteDevice())
                        { btreceivedata1=btreceivedata1+str;
                            //Log.v("data","1"+str+" device"+ttSocket.getRemoteDevice());
                        }
                        else
                        { btreceivedata2=btreceivedata2+str;
                            //Log.v("data","2"+str+" device"+ttSocket.getRemoteDevice());
                        }
                        //final  String str= new String(readBuffer, 0, readBuffer.length, "ASCII")+"\r";
                       /* if(alldata[stringindex].length()>1020) {
                            alldata[stringindex] = alldata[stringindex] + "\n";
                            Log.v("srtingindex",stringindex+"");
                        }*/

                        /*runOnUiThread(new Runnable() {
                            public void run() {

                                //maintext.setText(str+"___"+readBuffer.toString());
                                // Log.v("receivedd",str+"___"+readBuffer.toString());
                            }

                        });*/




                        // Send the obtained bytes to the UI Activity
                        //  }
                    }
                } catch (IOException e) {
                    Log.e("connect", "disconnected", e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //  overstep();
                            //btview.setText("at connectthread Disconnect!!");
                        }
                    });
                    break;
                }
            }
        }
        public void cancel() {
            try {
                ttSocket.close();

            } catch (IOException e) {
            }
        }
    }
    private void btwrite(char device,String data)
    {
        try {
            switch(device)
            {
                case 'm':
                    mmOutputStream.write(data.getBytes());
                    break;
                case 'n':
                    nnOutputStream.write(data.getBytes());
                    break;
                default:

                    break;
            }
        }
        catch (Exception x)
        {
            Log.v("OutputStream",x.toString());
        }
    }
    public void decode(char device)
    {
        boolean start=false;
        String xx="",yy="",zz="",receivedata="";
        int index=0,number=0,length=0,peakindex=0,mspindex=0;
        boolean firststepisget=false;
        double denumber=0,x,y,z;
        double[] save=new double[30];
        Queue q = new LinkedList(),peakqueue=new LinkedList(),peakqueu2 = new LinkedList();


        switch(device)
        {
            case 'm':
                length=btreceivedata1.length();
                receivedata=btreceivedata1;
                break;
            case 'n':
                length=btreceivedata2.length();
                receivedata=btreceivedata2;
                break;

        }
        // for(int x=0;x<=stringindex;x++)
        Log.v("String length alldata",length+"");

        {
            for(int i=0;length-i>=50&&length>12;)
            {
                if(!start)
                {
                    if(receivedata.charAt(i)=='a')
                        start=true;
                    i++;

                }
                else if( length-i>=50)
                {
                    for(int d=0;d<4&&receivedata.charAt(i)!='o';d++)
                    {
                        if(receivedata.charAt(i)=='l'){
                            mspwalkpoint[mspindex]=index;
                            txtdata+="L"+ (index+16)+"\n";
                            Log.v("mspsplit",index+"");
                            mspindex++;
                            d--;
                        }
                        else {
                            xx += receivedata.charAt(i);
                        }
                        i = i + 1;

                    }
                    number=Integer.parseInt( xx, 16 );
                    if (number > 32768){
                        number = number-32768;
                        number = 32767 - number + 1;
                        number = number * -1;
                    }
                    denumber=(number*9.8)/1280.0;
                    x=(float)denumber;

                    for(int d=0;d<4&&receivedata.charAt(i)!='o';d++)
                    {
                        if(receivedata.charAt(i)=='l'){
                            mspwalkpoint[mspindex]=index;
                            txtdata+="L"+ (index+16)+"\n";
                            Log.v("mspsplit",index+"");
                            mspindex++;
                            d--;
                        }
                        else {
                            yy += receivedata.charAt(i);
                        }
                        i = i + 1;
                    }
                    number=Integer.parseInt( yy, 16 );
                    if (number > 32768){
                        number = number-32768;
                        number = 32767 - number + 1;
                        number = number * -1;
                    }
                    denumber=(number*9.8)/1280.0;
                    y=(float)denumber;

                    for(int d=0;d<4&&receivedata.charAt(i)!='o';d++)
                    {
                        if(receivedata.charAt(i)=='l'){
                            mspwalkpoint[mspindex]=index;
                            txtdata+="L"+ (index+16)+"\n";
                            Log.v("mspsplit",index+"");
                            mspindex++;
                            d--;
                        }
                        else {
                            zz += receivedata.charAt(i);
                        }
                        i = i + 1;

                    }
                    number=Integer.parseInt( zz, 16 );
                    if (number > 32768){
                        number = number-32768;
                        number = 32767 - number + 1;
                        number = number * -1;
                    }
                    denumber=(number*9.8)/1280.0;
                    z=(float)denumber;

                    //mspdatam[index]=(float)magnitude((float) x, (float)y,(float)z);
                /*    if(q.size()<=30)
                    q.add(magnitude((float) x, (float)y,(float)z));
                    mspdatam[index]=average(q);
                    if(q.size()>30)
                    q.remove();*/

                    //  Log.v("receivedata "+device,(float)magnitude((float) x, (float)y,(float)z)+"");
                    if(q.size()<15) {
                        switch(device)
                        {
                            case 'm':
                                mspdatam1[index]=(float)magnitude((float) x, (float)y,(float)z);
                                txtdata+="m"+mspdatam1[index]+"\n";
                                q.add(mspdatam1[index]);
                                break;
                            case 'n':
                                mspdatam2[index]=(float)magnitude((float) x, (float)y,(float)z);
                                txtdata+="n"+mspdatam2[index]+"\n";
                                q.add(mspdatam2[index]);
                                break;

                        }
                    }
                    else {
                        q.add(magnitude((float) x, (float) y, (float) z));
                        switch(device)
                        {
                            case 'm':
                                mspdatam1[index]=average_edit(q, 15);
                                txtdata+="m"+mspdatam1[index]+"\n";
                                // peakqueue.add(mspdatam1[index]);
                                if(mainmode==2) {
                                    if (index >30)
                                        peakqueue.add(mspdatam1[index]);
                                    if (peakqueue.size() >= 21 && index > 30) {
                                        boolean isvalley = true;
                                        ArrayList arraylist = new ArrayList(peakqueue);
                                        if(!firststepisget) {
                                            for (int s = 1; s <= 10; s++) {
                                                if ((double) arraylist.get(10 + s) > (double) arraylist.get(10) || (double) arraylist.get(10 - s) > (double) arraylist.get(10)) {
                                                    firststepisget = true;
                                                    break;
                                                }
                                            }
                                            if(firststepisget)
                                                txtdata+="h"+ (index - 11)+"\n";
                                        }
                                        else {
                                            for (int s = 1; s <= 10; s++) {
                                                if ((double) arraylist.get(10 + s) < (double) arraylist.get(10) || (double) arraylist.get(10 - s) < (double) arraylist.get(10)) {
                                                    isvalley = false;
                                                    break;
                                                }
                                            }
                                            if (isvalley && Math.abs(mspdatam1[index-20]  - (double) arraylist.get(10)) >0.5) {
                                                mspwalkpoint[peakindex] = index - 11;
                                                txtdata+="W"+ mspwalkpoint[peakindex]+"\n";
                                                peakindex++;
                                                Log.v("ispeak1", arraylist.get(10) + " index is " + index);
                                            }
                                        }
                                        peakqueue.poll();

                                    }
                                }
                                break;
                            case 'n':
                                mspdatam2[index]=average_edit(q,15);
                                txtdata+="n"+mspdatam2[index]+"\n";
                                //peakqueue.add(mspdatam1[index]);
                                if(mainmode==2) {
                                    if (index > 30)
                                        peakqueue.add(mspdatam2[index]);
                                    if (peakqueue.size() >= 21 && index > 30) {
                                       // Log.v("ispeak2", " index is " + index);

                                        boolean ispeak = true;
                                        ArrayList arraylist = new ArrayList(peakqueue);
                                        if(!firststepisget) {
                                            for (int s = 1; s <= 10; s++) {
                                                if ((double) arraylist.get(10 + s) > (double) arraylist.get(10) || (double) arraylist.get(10 - s) > (double) arraylist.get(10)) {
                                                    firststepisget = true;
                                                    break;
                                                }
                                            }
                                        }
                                        else {
                                            for (int s = 1; s <= 10; s++) {
                                                if ((double) arraylist.get(10 + s) < (double) arraylist.get(10) || (double) arraylist.get(10 - s) < (double) arraylist.get(10)) {
                                                    ispeak = false;
                                                    break;
                                                }
                                            }
                                            if (ispeak && Math.abs(mspdatam2[index-20] - (double) arraylist.get(10)) > 0.5) {
                                                mspwalkpoint2[peakindex] = index - 11;
                                                txtdata+="H"+ mspwalkpoint2[peakindex]+"\n";

                                                peakindex++;
                                                Log.v("ispeak2", arraylist.get(10) + " index is " + index);
                                            }
                                        }
                                        peakqueue.poll();

                                    }
                                }
                                break;

                        }
                        q.poll();
                        //Log.v("queue "+device,average(q)+"");

                    }

                    //x=average(q);

                    index++;
                    xx="";yy="";zz="";

                }
            }

        }
        Log.v("decode index", index + "");
        // Log.v("txtdata",txtdata);
        q.clear();

        //initial_variables();

    }
    //=================================================================================================
    private double CCnorm(int m1,int m2,int n1,int n2)
    {
        //int Uflong=(walkpoint[6]-walkpoint[2])*5,Aflong=(walkpoint[7]-walkpoint[3])*ratio,ccsize;\
        //  Log.v("ccsize", "mspwalkpoint[6]"+ mspwalkpoint[4]+" "+mspwalkpoint[2]+ "mspwalkpoint2[6]"+ mspwalkpoint2[4]+" "+mspwalkpoint2[2]);

        int Uflong=(mspwalkpoint[m2]-mspwalkpoint[m1]),Aflong=(mspwalkpoint2[n2]-mspwalkpoint2[n1]),ccsize;
        ccsize=Uflong>Aflong?Uflong:Aflong;
        //ccsize=Uflong;
        Log.v("ccsize","ccsize"+ccsize+" walkpoint2 : "+mspwalkpoint[2]+" walkpoint1"+mspwalkpoint[1]+" walkpoint22 : "+mspwalkpoint2[2]+" walkpoint21"+mspwalkpoint2[1]);
        double reans=0,averUF=0,averAF=0;
        double[] aUF=new double[ccsize+1],aAF=new double[ccsize+1],cc=new double[ccsize+1];
        for(int i=0;i<ccsize;i++)
        {
            aAF[i+1]=mspdatam2[mspwalkpoint2[n1]+i];
            averAF+= aAF[i+1];
            aUF[i+1]=mspdatam1[mspwalkpoint[m1]+i];
            averUF+=aUF[i+1];
        }
        averAF=averAF/ccsize;
        averUF=averUF/ccsize;
        Log.v("average","a "+averAF+" u "+averUF);
        double Max=0,sum=0,a=0,u=0;
        for(int k=1;k<=ccsize;k++) {
            sum+=(aAF[k]-averAF)*(aUF[k]-averUF);
            a+=(aAF[k]-averAF)*(aAF[k]-averAF);
            u+=(aUF[k]-averUF)*(aUF[k]-averUF);
        }
        double AcUF=0,AcAF=0,sumU=0;
       /* for(int n=1;n<=ccsize;n++)
        {
            if (n - 0 <= 0 || n - 0 > ccsize)
            { //Log.v("n-k",n-k+"");
                aAF[n] = 0;
                aUF[n]=0;
                a=0;}
            else
            {a=aAF[n - 0];u=aAF[n];}
            sum += aAF[n] * aAF[n];
            sumU+=aUF[n]*aUF[n];
        }*/
        //AcUF=sumU;AcAF=sum;
        reans=sum/(Math.sqrt(a*u));
        txtdata+="C"+String.format("%.6f", reans)+"\n";
        answer+="\n步態對稱性係數"+String.format("%.6f", reans)+"\n";
        if(ccnum<reans)
        ccnum=reans;
        Log.v("CC", reans + "    Max " + sum + "   AcUF" + u + "    AcAF" + a);
       /* double Max=0,sum=0,a=0,u=0;
        for(int k=0;k<ccsize;k++) {

            for (int n = 1; n <= ccsize; n++) {

                if (n - k <= 0 || n - k > ccsize)
                { //Log.v("n-k",n-k+"");
               // aAF[n] = 0;
                  //  aUF[n] = 0;
                    ;
                  //  a=0;
                }
                else
                {a=aAF[n - k];u=aUF[n];}
                sum += a * u;
                a=0;u=0;
            }
            cc[k]=sum;
            if (cc[k] > Max)
                Max = cc[k];
            sum=0;

        }
        double AcUF=0,AcAF=0,sumU=0;
        for(int n=1;n<=ccsize;n++)
        {
            if (n - 0 <= 0 || n - 0 > ccsize)
            { //Log.v("n-k",n-k+"");
                 aAF[n] = 0;
                aUF[n]=0;
                a=0;}
            else
            {a=aAF[n - 0];u=aAF[n];}
            sum += aAF[n] * aAF[n];
            sumU+=aUF[n]*aUF[n];
        }
        AcUF=sumU;AcAF=sum;
        answer=Max/(Math.sqrt(AcUF*AcAF));
        Log.v("CC", answer + "    Max " + Max + "   AcUF" + AcUF + "    AcAF" + AcAF);*/

        return reans;
    }
    public double average(Queue que)
    {
        double ave=0;
        ArrayList arraylist = new ArrayList(que);

        double[] d=new double[30];

        for(int x=0;x<15;x++)
        {
            // Log.v("average",arraylist.get(x)+"");
            ave+=(double)arraylist.get(x);
        }
        //Log.v("queue","ave sum"+ave/100);

        //Log.v("average",ave/30+"");
        return ave/15.0;
    }
    public double average_edit(Queue que,int all)
    {
        double ave=0;
        ArrayList arraylist = new ArrayList(que);

       // double[] d=new double[all];

        for(int x=0;x<all;x++)
        {
            // Log.v("average",arraylist.get(x)+"");
            ave+=(double)arraylist.get(x);
        }
        //Log.v("queue","ave sum"+ave/100);

        //Log.v("average",ave/30+"");
        return ave/all;
    }
    private void showresult()
    {
        answer = "";
        switch (viewfilename.charAt(0))
        {
            case '1':
                fileinmode = 2;
                break;
            case '起':
                fileinmode = 1;
                break;
            case 'B':
                fileinmode = 3;
                break;
        }
              /*  if (viewfilename.charAt(0) == '1') {
                    fileinmode = 2;
                } else
                    fileinmode = 1;
                Log.v("fileinmode", fileinmode + "");*/
        readdata(viewfilename);

        //  Log.v("CCnorm", CCnorm()+"") ;
        // Log.v("walkpoint",walkpoint[0]+"");
        answertext.setText("測驗結果\n" + answer);
        showchart(1);
        if(fileinmode<3) {
            String[] showlistvalue = new String[]{"感測器訊號", "手機訊號"};
            switch (fileinmode)
            {
                case 1:
                    showlistvalue = new String[]{"感測器訊號(右腳)","感測器訊號(左腳)","手機訊號"};
                    break;
                case 3:
                    break;
            }
            ArrayAdapter adapter = new ArrayAdapter(TestActivity.this, android.R.layout.simple_list_item_1, showlistvalue);
            signallist.setAdapter(adapter);
            signallist.setVisibility(View.VISIBLE);
        }
        else
            signallist.setVisibility(View.INVISIBLE);
    }
}
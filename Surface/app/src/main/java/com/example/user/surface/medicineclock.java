package com.example.user.surface;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class medicineclock extends AppCompatActivity {
    private TextView timeview,dateview;
    private Button btrelax,btread,btcount;
    private TextView editText ;
    private ListView mainlistview;
    private TimePickerDialog.OnTimeSetListener timepicker;
    private Myadapter myadapter;
    private Timenofi test1;
    //==============================================================
    Calendar mCalendar = new GregorianCalendar();
    Time mTime = new Time("GMT+8");

    private SoundPool sound;
    int[] timeup=new int[7],totalsecond=new int[20];
    int musicsound,countdownindex=0;
    private String[] sounditem={"music1","music2","music3","music4","music5"};

    Boolean countdown=false;
    int month,day,hour,minute,second,
            countdown_minute,countdown_second,countdowntime,
            totalsecond0;
    String shour,sminute,ssecond;

    private int numofadapter=0;
    private String[] time_adapter=new String[10];
    private ArrayAdapter<String> listAdapter;

    private Handler myTimer = new Handler(),threadtimer= new Handler();
    //private EditText timeview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicineclock);
        timeview=(TextView)findViewById(R.id.textView);
        dateview=(TextView)findViewById(R.id.textview1);

        btcount=(Button)findViewById(R.id.buttoncount);
        editText= new TextView(medicineclock.this);
        mainlistview=(ListView)findViewById(R.id.listView);
        myadapter=new Myadapter(getApplicationContext());
        LoadPreferences();
        mainlistview.setAdapter(myadapter);
        //====================================================
        mTime.setToNow();
        month=mTime.month+1;
        day=mTime.monthDay;
        dateview.setText(month + " / " + day);

        myTimer.postDelayed(Timer_function, 1000);
        sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        timeup[0]=sound.load(this,R.raw.upandgo,1);
        musicsound=timeup[0];


        for(int i=0;i<20;i++)
            totalsecond[i]=-1;
       /* listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,sounditem);
        listView.setAdapter(listAdapter);*/
        //=================================================

        btcount.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });
        mainlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("itemclick", "click");
             final  int index =position;
                new AlertDialog.Builder(medicineclock.this)
                        .setTitle("計時選項")
                        .setMessage("計時器目前設定時間 \n" + myadapter.getItemname(index))
                        .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myadapter.removeItem(index);
                                totalsecond[index]=-1;
                                countdownindex--;
                                myadapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("開始計時", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                    countdown = true;

                                    totalsecond[index]=counttotalsecond(index);
                                    countdownindex++;
                                Log.v("time", totalsecond[index] + " ");

                                /* else {
                                    AlertDialog.Builder editDialog = new AlertDialog.Builder(MainActivity.this);
                                    editText = new TextView(MainActivity.this);
                                    editText.setTextSize(20);
                                    editDialog.setTitle("已有其他倒數");
                                    editDialog.setView(editText);
                                    editDialog.setPositiveButton("取消其他倒數", new DialogInterface.OnClickListener() {
                                        // do something when the button is clicked
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            countdown = false;
                                        }
                                    });
                                    editDialog.show();
                                }*/


                            }
                        })
                        .show();

            }
        });

        timepicker= new TimePickerDialog.OnTimeSetListener() {
            int clickindex=0;
            @Override
            public void onTimeSet(TimePicker view,final int hour,final int timeminute) {

                if(clickindex%2==0) {

                    AlertDialog.Builder editDialog = new AlertDialog.Builder(medicineclock.this);
                    editDialog.setTitle("輸入暱稱");
                    final EditText editText = new EditText(medicineclock.this);
                    editText.setText("");
                    editText.setTextSize(30);
                    editText.setHint("藥品名稱");
                    editDialog.setView(editText);
                    editDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        // do something when the button is clicked
                        public void onClick(DialogInterface arg0, int arg1) {
                            myadapter.addItem(editText.getText().toString(),hour + " : " + timeminute);
                            myadapter.notifyDataSetChanged();
                        }
                    });
                    editDialog.show();

                  //  Log.v("timepicker", hour + " : " + timeminute);
                }
                clickindex++;
                // Log.v("adapter",myadapter.getCount()+"  ");
              //  myadapter.notifyDataSetChanged();
            }
        };

    }
    @Override
    protected void onPause(){
        super.onPause();

       SavePreferences();

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        SavePreferences();


        Log.v("onDestroy", "Destroy");

    }

    private int counttotalsecond(int index)
    {
        countdown_minute = 0;
        countdown_second = 0;
        int returnsecond=0;

        String time = myadapter.getItemname(index);
        int edhour, edminute, mid;
        mid = time.indexOf(":");
        edhour = Integer.parseInt(time.substring(0, mid - 1));
        edminute = Integer.parseInt(time.substring(mid + 2, time.length()));
        if (edhour <= hour && edminute < minute) {
            edhour += 23;
        } else if (edhour < hour)
            edhour += 23;


        if (edminute < minute)
            edminute += 60;
        countdowntime = (edhour - hour) * 60 + (edminute - minute - 1);
        returnsecond= 60 * countdowntime + 59 - second;

        if (edhour == hour && edminute == minute)
        {
            countdowntime=0;
            returnsecond = 60 * countdowntime + 59 - second;
        }
        return returnsecond;
    }
    public Runnable Timer_function=new Runnable() {
        @Override
        public void run() {
            mTime.setToNow();
            month=mTime.month+1;
            day=mTime.monthDay;
            hour=mTime.hour+8;
            minute=mTime.minute;
            second=mTime.second;

            for(int i=0;i<20;i++)
            {
               // Log.v("countdown",totalsecond[i]+"");
                if(totalsecond[i]!=-1) {
                    totalsecond[i]--;
                    editText.setText((totalsecond[i] / 60 / 60) + " : " + ((totalsecond[i] / 60) % 60) + " : " + (totalsecond[i] % 60));

                    if (((minute - countdown_minute) == countdowntime || (minute + 60 - countdown_minute) == countdowntime) && countdown_second == second) {
                        countdown = false;
                        // sound.play(musicsound, 1, 1, 0, -1, 1);
                        //callnotify();
                        Log.v("countdown", "time up1");
                        new AlertDialog.Builder(medicineclock.this)
                                .setTitle("時間到")
                                .setMessage("按下確定已停止鬧鐘")
                                .setPositiveButton("停止", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sound.autoPause();

                                    }
                                })
                                .show();

                    }
                    if ( totalsecond[i] == 0) {
                        totalsecond[i]=counttotalsecond(i);
                        Log.v("countdown time up2",totalsecond[i]+"");
                        countdown = false;
                        sound.play(musicsound, 1, 1, 0, 0, 1);
                        callnotify(i);
                    }
                }
            }
            shour=""+hour;sminute=""+minute;ssecond=""+second;
            if(hour<10)
                shour="0"+shour;
            if(minute<10)
                sminute="0"+sminute;
            if(second<10)
                ssecond="0"+ssecond;
                timeview.setText(shour+": "+sminute+" : "+ssecond);
            myTimer.postDelayed(Timer_function,1000);
        }
        };
    private class Timenofi extends Thread{
        boolean flag=true;
        public int tsecond=0,countsecond;
        public Timenofi(int time) {
            countsecond=time;
            Log.v("set time","second"+countsecond);

        }
        public void run() {
            if(flag)
            {
              //  Log.v("time1","second"+tsecond);

                threadtimer.postDelayed(threadtimer_function,1000);

            }
        }
        public void cancel(){
            flag=false;
        }
        }
    public Runnable threadtimer_function=new Runnable() {
        @Override
        public void run() {
            test1.tsecond++;
            if( test1.tsecond>=test1.countsecond)
            {

              //  callnotify();
                Log.v("time up","second"+test1.tsecond);
                test1. flag=false;
                test1.cancel();

            }
            else
                threadtimer.postDelayed(threadtimer_function,1000);

            //    Log.v("time runnable","second"+ test1.tsecond);

        }
    };
    private void callnotify(int index)
    {
        final int notifyID = 1; // 通知的識別號碼ma
        final int requestCode = notifyID; // PendingIntent的Request Code
        final Intent intentc = new Intent(getApplicationContext(), medicineclock.class); // 開啟另一個Activity的Intent
        final int flags = PendingIntent.FLAG_UPDATE_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext()); // 建立TaskStackBuilder
        stackBuilder.addParentStack(medicineclock.class); // 加入目前要啟動的Activity，這個方法會將這個Activity的所有上層的Activity(Parents)都加到堆疊中
        stackBuilder.addNextIntent(intentc); // 加入啟動Activity的Intent
        final PendingIntent pendingIntent = stackBuilder.getPendingIntent(requestCode, flags); // 取得PendingIntent
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("銀向健康").setContentText(myadapter.getmedicinename(index)+"已到吃藥時間").setAutoCancel(true).setContentIntent(pendingIntent).build();

        notificationManager.notify(notifyID, notification); // 發送通知
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected Dialog onCreateDialog(int id)
    {
        Calendar c=Calendar.getInstance();

        switch (id){
            case 1:
                int hour =c.getTime().getHours();
                int minute=c.getTime().getMinutes();
                return new TimePickerDialog(this ,timepicker,hour,minute,true);
            default:
                return null;
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new AlertDialog.Builder(medicineclock.this)
                    .setTitle("選擇鈴聲")
                    .setSingleChoiceItems(sounditem, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            musicsound = timeup[which];
                            sound.play(musicsound, 1, 1, 0, 0, 1);
                        }
                    })
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            musicsound = timeup[0];
                            sound.play(musicsound, 1, 1, 0, 0, 1);
                        }
                    })
                    .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        }

        return super.onOptionsItemSelected(item);
    }
    protected void SavePreferences() {
        // TODO Auto-generated method stub
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = data.edit();
        editor.clear();
        for (int i = 0; i < myadapter.getCount(); ++i){
            // This assumes you only have the list items in the SharedPreferences.
            editor.putString(String.valueOf(i), myadapter.getItemname(i));
            editor.putString("m"+String.valueOf(i), myadapter.getmedicinename(i));

        }
        editor.commit();


    }
    protected void LoadPreferences()
    {
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);

        for (int i = 0;; ++i){
            final String str = data.getString(String.valueOf(i), "");
            final String mstr = data.getString("m"+String.valueOf(i), "");
           if (!str.equals("")){
                myadapter.addItem(mstr,str);
               countdownindex++;
            }

            if(str.equals("")&&mstr.equals(""))
                break;
        }
    }
}

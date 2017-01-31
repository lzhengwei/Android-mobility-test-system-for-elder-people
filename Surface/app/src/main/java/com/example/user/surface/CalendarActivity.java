package com.example.user.surface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhengwei on 2016/9/26.
 */
public class CalendarActivity extends AppCompatActivity {
    private CalendarView maincalendar;
    private TextView showfalltime;
    private ListView showTextlist ;

    private  int Myear,Mmonth,Mday,falltimeofmonth=0;
    String showlogtext="";
    private CaldroidFragment caldroidFragment;
    static List<String> fallday= new ArrayList<>();
    //private String[] showlistvalue;
    private ArrayList<String> showlistvalue = new ArrayList<String>();
    private String savename="";
    private         Date day;
    private boolean nodata=true;
    File mSDFile ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_fall);
        setTitle("測驗紀錄");
        //   LoadPreferences();
        listFallday();
        mSDFile = Environment.getExternalStorageDirectory();
        //========== cadroid =========================================\
        showTextlist = new ListView(CalendarActivity.this);
        caldroidFragment=new CaldroidFragment();
        //=================== cadroid listener =======================================
        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(final Date date, View view) {
                final DateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH_mm",Locale.US);
                String trydate="2016年11月10日17_56";

                Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間


                savename=formatter.format(date);
                Log.v("date",savename);
                try {
                    final DateFormat tryfordayformat = new SimpleDateFormat("yyyy年MM月dd日",Locale.US);

                    Log.v("trydate",tryfordayformat.parse(trydate)+"");

                    final DateFormat fordayformat = new SimpleDateFormat("EEE MMM dd yyyy",Locale.US);
                    day = tryfordayformat.parse(savename);

                    Log.v("date", day+"");

                    showlistvalue.clear();
                    readsamedaylog(savename.substring(0,10));
                    Mmonth= date.getMonth()+1;
                    Mday=date.getDate();
                 /*   if(Fileexits(savename))
                        readdata(savename);*/
                    final TextView showText = new TextView(CalendarActivity.this);
                    AlertDialog.Builder editDialog = new AlertDialog.Builder(CalendarActivity.this);

                    if(showlistvalue.size()==0) {
                        showlogtext = "無測驗紀錄";
                        showText.setTextSize(20);
                        showText.setText(showlogtext);
                        editDialog.setView(showText);
                        Log.v("logtext", "showtext");                    }
                    else{
                        //Log.v("listvalue log ",showlistvalue[0]+showlistvalue[1]);
                        if(showTextlist.getParent()!=null)
                            ((ViewGroup)showTextlist.getParent()).removeView(showTextlist);
                        Log.v("logtext", "showlist");
                        ArrayAdapter adapter = new ArrayAdapter(CalendarActivity.this, R.layout.loglistitem, showlistvalue);
                        showTextlist.setAdapter(adapter);
                        editDialog.setView(showTextlist);

                    }

                    editDialog.setTitle(Mmonth + "月" + Mday + "日" + "測驗紀錄");
                    // String[] showlistvalue=new String[]{showlogtext};

                    editDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    editDialog.show();
                /*new AlertDialog.Builder(CalendarActivity.this)
                        .setTitle(Mmonth+"月"+Mday+"日"+"跌倒紀錄")
                                //.setMessage("選擇一裝置進行配對")
                        .setMessage(showlogtext)
                        .setPositiveButton("新增", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(CalendarActivity.this, falldatainActivity.class);
                                Bundle bundle = new Bundle();
                               bundle.putString("date",savename);

                                intent.putExtras(bundle);
                                startActivityForResult(intent,0);
                            }
                        })
                        .setNegativeButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(fallday.contains(savename))
                                    fallday.remove(fallday.indexOf(savename));
                                caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.WHITE), date);
                                caldroidFragment.refreshView();
                                File mSDFile = Environment.getExternalStorageDirectory();
                                //讀取文件檔路徑
                                File deletefile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/FallLog/" + savename + ".txt");
                                deletefile.delete();

                                if(!deletefile.exists())
                                Toast.makeText(CalendarActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(CalendarActivity.this, "資料刪除失敗，請重新操作", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .show();*/
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //StringBuilder可以將字串連續的加入
                showlogtext="";

                // caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.RED),date);
                // caldroidFragment.refreshView();
            }
            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {

            }
            @Override
            public void onCaldroidViewCreated() {
                Date da;
                final DateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH_mm", Locale.US);

                for(int i=0;i<fallday.size();i++)
                {
                    try {
                        Log.v("da",fallday.get(i));

                        da=formatter.parse(fallday.get(i));
                        caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.argb(255,153,255,153)),formatter.parse(fallday.get(i)) );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //
                }
                caldroidFragment.refreshView();
            }

        };

        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        // args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
        args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
        caldroidFragment.setArguments(args);
        caldroidFragment.setCaldroidListener(listener);
        maincalendar=(CalendarView)findViewById(R.id.calendarView);
        maincalendar.removeAllViews();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendarView, caldroidFragment);
        t.commit();



        //============================================================

        maincalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub
                Myear = year;
                Mmonth = month;
                Mday = dayOfMonth;
                readdata(Mmonth + "" + Mday + "");
                if(showlogtext=="")
                    showlogtext="無測驗紀錄";
                new AlertDialog.Builder(CalendarActivity.this)
                        .setTitle(Mmonth+"月"+Mday+"日"+"測驗紀錄")
                                //.setMessage("選擇一裝置進行配對")
                        .setMessage(showlogtext)
                        .setPositiveButton("新增", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                //intent.setClass(CalendarActivity.this, falldatainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("date", Mmonth + "" + Mday + "");

                                intent.putExtras(bundle);

                                startActivityForResult(intent, 0);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                //StringBuilder可以將字串連續的加入
                showlogtext="";
            }

        });
        showTextlist.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int clickposition=position;
                if(showlistvalue.get(clickposition).charAt(4)=='問')
                    pointviewADL(showlistvalue.get(clickposition));
                else
                pointview(showlistvalue.get(clickposition));
                new AlertDialog.Builder(CalendarActivity.this)
                        .setTitle(Mmonth + "月" + Mday + "日" + "測驗紀錄")
                                //.setMessage("選擇一裝置進行配對")
                        .setMessage(showlistvalue.get(position))
                        .setNegativeButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletefile(showlistvalue.get(clickposition));
                                showlistvalue.remove(clickposition);
                                ArrayAdapter adapter = new ArrayAdapter(CalendarActivity.this, android.R.layout.simple_list_item_1, showlistvalue);
                                showTextlist.setAdapter(adapter);
                                // rewrite();
                            }
                        })
                        .show();
            }
        });
    }
    protected void onDestroy() {
        super.onDestroy();
        Log.v("log", "destroy");
        fallday.clear();
        //  SavncePreferees();
        // compassManager.unregisterListener(compass);

    }
    private void readdata(String filename)
    {
        //showlistvalue=new String[10];
        int listindex=0;
        try
        {
            //取得SD卡儲存路徑
            //讀取文件檔路徑
            File file = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/FallLog/" + filename + ".txt");
            if(file.exists()) {
                Log.v("file", "exist");

                FileReader mFileReader = new FileReader(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/FallLog/" + filename + ".txt");


                BufferedReader mBufferedReader = new BufferedReader(mFileReader);
                String mReadText = "";
                String mTextLine = mBufferedReader.readLine();

                //一行一行取出文字字串裝入String裡，直到沒有下一行文字停止跳出
                int index = 0;
                showlogtext="";
                while (mTextLine != null) {
                    //  Log.v("readline",mTextLine);
                    switch (index)
                    {
                        case 0:
                            showlogtext+=mTextLine+"\n";
                            break;
                        case 1:

                            showlogtext+=mTextLine+"\n";
                            break;
                        case 2:
                            showlogtext+=mTextLine+"\n";
                            break;
                        case 3:
                            showlogtext+=mTextLine+"\n";
                            break;
                    }
                    if(index>=3)
                    {
                        showlistvalue.add(showlogtext);
                        Log.v("showlistvalue "+listindex,showlogtext);
                        showlogtext="";
                        listindex++;
                        index=0;
                        //showlogtext+="\n====================\n";
                    }
                    else
                        index++;

                    //   mReadText += mTextLine+"\n";
                    mTextLine = mBufferedReader.readLine();
                }
            }

        }
        catch(Exception e)
        {
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.v("result", data.getExtras().getBoolean("isin") + ""+requestCode+"") ;

        if(resultCode == RESULT_OK && requestCode==0){

            Log.v("result", data.getExtras().getBoolean("isin") + "") ;
            if( data.getExtras().getBoolean("isin")) {
                caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.RED), day);
                caldroidFragment.refreshView();
            }


        }
    }
    private void listFallday()
    {
        File mSDFile = Environment.getExternalStorageDirectory();
        File file = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/");
        Log.v("SDfile",mSDFile.getParent() + "/" + mSDFile.getName());
        File[] files = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {

                return name.toLowerCase().endsWith(".txt");
            }
        });

        // Log.v("files length",files.length+"");
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                switch (files[i].getName().charAt(0))
                {
                    case '1':
                        fallday.add(files[i].getName().substring(8, files[i].getName().length() - 4));
                        break;
                    case '起':
                        fallday.add(files[i].getName().substring(6, files[i].getName().length() - 4));
                        break;
                    case 'B':
                        if(files[i].getName().charAt(4)=='問')
                            fallday.add(files[i].getName().substring(6, files[i].getName().length() - 4));
                        else
                        fallday.add(files[i].getName().substring(9, files[i].getName().length() - 4));
                        break;
                    case 'I':
                        fallday.add(files[i].getName().substring(6, files[i].getName().length() - 4));
                        break;
                }


                //Log.v("files",files[i].getName().substring(0,files[i].getName().length()-4));
            }
            nodata=false;

        }
        else
        {
            fallday.add("10公尺走路測驗_" +"2016年09月29日12:05:26");
            fallday.add("起立行走測驗_" + "2016年09月29日12:07:32");
        }

    }
    private boolean Fileexits(String name)
    {
        File exitfile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + name + ".txt");
        if(exitfile.exists())
            return true;
        else
            return false;

    }
    private void deletefile(String filename)
    {
        File mSDFile = Environment.getExternalStorageDirectory();
        //讀取文件檔路徑
        File deletefile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + filename+".txt");
        deletefile.delete();
        if(!deletefile.exists())
        {
            Toast.makeText(CalendarActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();

        }
    }
    private void readsamedaylog(String m)
    {

        final String match=m;
        File mSDFile = Environment.getExternalStorageDirectory();
        File file = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/");
        File[] files = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().contains(match);

                // return name.toLowerCase().matches(match);
            }
        });

//        Log.v("files length", files.length + " " + match);
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                showlistvalue.add(files[i].getName().substring(0, files[i].getName().length() - 4));
                Log.v("files", files[i].getName().substring(0, files[i].getName().length() - 4));
            }

        }
    }
    public  void pointview(String filename)
    {
        Log.v("filename",filename);
        Intent intent = new Intent();
        intent.setClass(CalendarActivity.this, TestActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putInt("fileid", fallday.indexOf(filename));
        bundle.putBoolean("nodata",nodata);
        intent.putExtras(bundle);

        startActivity(intent);


    }
    public  void pointviewADL(String filename)
    {

        Log.v("filename",filename);
        Intent intent = new Intent();
        switch (filename.charAt(0))
        {
            case 'B':
                intent.setClass(CalendarActivity.this, badl.class);
                break;
            case 'I':
                intent.setClass(CalendarActivity.this, iadl.class);
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putInt("fileid", fallday.indexOf(filename));
        bundle.putBoolean("isPoint",true);
        intent.putExtras(bundle);

        startActivity(intent);


    }
    private void rewrite(){
        try {
            File mSDFile = Environment.getExternalStorageDirectory();


            File mFile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/FallLog");
            //若沒有檔案儲存路徑時則建立此檔案路徑
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
            //   SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
            // Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
            String relog="";
            for(int i=0;i<showlistvalue.size();i++)
            {
                relog+=showlistvalue.get(i);
            }
            FileWriter mfilewriter;
            mfilewriter = new FileWriter(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/FallLog/" + savename + ".txt");
            mfilewriter.write(relog);
            mfilewriter.close();
            Thread.sleep(1000);
            Toast.makeText(CalendarActivity.this, "儲存成功", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            Log.v("filewriter error",e.toString());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filedelete, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_transform:
                try {
                    Intent intent = new Intent();
                    intent.setClass(CalendarActivity.this, filelistActivity.class);
                    startActivity(intent);

                } catch (Exception x) {
                    Log.v("delete exception", x.toString());
                }
                break;


        }
        return super.onOptionsItemSelected(item);

    }
    protected void SavncePreferees() {
        // TODO Auto-generated method stub
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = data.edit();
        editor.clear();
        // This assumes you only have the list items in the SharedPreferences.
        Log.v("falldaysize", fallday.size() + "");
        for(int i=0;i<fallday.size();i++)
        {  editor.putString("fallday" + i, fallday.get(i));
            Log.v("save",fallday.get(i));}
        editor.commit();



    }
    protected void LoadPreferences()
    {
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);

        fallday.clear();
        for(int i=0;;i++) {
            final String str = data.getString("fallday"+i, "");
            Log.v("log", str);
            if (!str.equals("")) {
                fallday.add(str);
                // Log.v("file long",exitfilenumber+"");
            } else
                break;
        }
        Log.v("falldaysize", fallday.size()+"");

        //lse  Emp



    }
}

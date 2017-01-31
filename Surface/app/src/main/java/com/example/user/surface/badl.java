package com.example.user.surface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class badl extends AppCompatActivity {
    private Spinner phonespinner;
    private Spinner cookspinner;
    private Spinner washspinner;
    private Spinner medicinespinner;
    private Spinner moneyspinner;
    private Spinner shoppingspinner;
    private Spinner homespinner;
    private Spinner outdoorspinner;
    public String eatstr;
    public String bathestr;
    public String healthstr;
    public String dressingstr;
    public String Stoolstr;
    public String pissstr;
    public String Toiletsstr,txtdata="",viewfilename;

    public Button caculate;
    public TextView score;
    public int s=0,lost=0;

    private ArrayAdapter<String> listAdapter;
    public int eatscore=0;
    public int bathescore=0;
    public int healthscore=0;
    public int dressingscore=0;
    public int Stoolscore=0;
    public int pissscroe=0;
    public int Toiletsscore=0,totalscore=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badl_surface);
        final String[] listeat =new String[] {"--------------請選擇--------------","自主獨立","不須幫忙","需要協助"};
        final String[] listbathe={"--------------請選擇--------------","不須幫忙","需要協助"};
        final String[] listhealth={"--------------請選擇--------------","不須幫忙","需要協助"};
        final String[] listdressing={"--------------請選擇--------------","自主獨立","需要協助但自己可以做一半","完全依賴"};
        final String[] listStool={"--------------請選擇--------------","能完全自我控制","偶而失控","失禁或需灌腸劑"};
        final String[] listpiss={"--------------請選擇--------------","能完全自我控制","需要協助但某些可自己做","完全依賴"};
        final String[] listToilets={"--------------請選擇--------------","自主獨立","偶而失控","失禁或需灌腸劑"};
        phonespinner=(Spinner)findViewById(R.id.phone0); //指向畫面上id為changetype1的Spinner物件
        cookspinner=(Spinner)findViewById(R.id.cook);
        washspinner = (Spinner) findViewById(R.id.wash); //指向畫面上id為changetype1的Spinner物件
        medicinespinner = (Spinner) findViewById(R.id.medicine); //指向畫面上id為changetype1的Spinner物件
        moneyspinner = (Spinner) findViewById(R.id.money); //指向畫面上id為changetype1的Spinner物件
        shoppingspinner = (Spinner) findViewById(R.id.shopping); //指向畫面上id為changetype1的Spinner物件
        homespinner = (Spinner) findViewById(R.id.home); //指向畫面上id為changetype1的Spinner物件
        score=(TextView)findViewById(R.id.score);
        caculate=(Button)findViewById(R.id.caculate);

        //========================= Bundle =============================================
        if( this.getIntent().hasExtra("filename")) {
            Bundle bundle = this.getIntent().getExtras();
            viewfilename = bundle.getString("filename");
            readdata(viewfilename);
            caculate.setVisibility(View.INVISIBLE);

        }
        else {
            //吃飯
            listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, listeat);

            //將adapter 添加到spinner中
            phonespinner.setAdapter(listAdapter);
            phonespinner.setSelection(0, false);

            //設定項目被選取之後的動作
            phonespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    phonespinner.setSelection( position);
                    CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.checktext);
                    chkItem.setChecked(true);
                       Toast.makeText(badl.this, "你選的是"+phonespinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    // Log.v("test", phonespinner.getSelectedItem().toString()+listeat[1]);
                    //   Log.v("test","哈");
                }

                @Override
                public void onNothingSelected(AdapterView arg0) {

                    Toast.makeText(badl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
                }
            });
            //洗澡
            listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, listbathe);
            cookspinner.setAdapter(listAdapter);
            cookspinner.setSelection(0, false);

            //設定項目被選取之後的動作
            cookspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    Toast.makeText(badl.this, "你選的是" + cookspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.checktext);
                    chkItem.setChecked(true);

                }

                @Override
                public void onNothingSelected(AdapterView arg0) {

                    Toast.makeText(badl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
                }
            });

            //衛生
            listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, listhealth);
            washspinner.setAdapter(listAdapter);
            washspinner.setSelection(0, false);

            //設定項目被選取之後的動作
            washspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    Toast.makeText(badl.this, "你選的是" + washspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.checktext);
                    chkItem.setChecked(true);
                }

                @Override
                public void onNothingSelected(AdapterView arg0) {

                    Toast.makeText(badl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
                }
            });

            //穿衣
            listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, listdressing);
            medicinespinner.setAdapter(listAdapter);
            medicinespinner.setSelection(0, false);

            //設定項目被選取之後的動作
            medicinespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    Toast.makeText(badl.this, "你選的是" + medicinespinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.checktext);
                    chkItem.setChecked(true);
                }

                @Override
                public void onNothingSelected(AdapterView arg0) {

                    Toast.makeText(badl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
                }
            });

            //大便
            listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, listStool);
            moneyspinner.setAdapter(listAdapter);
            moneyspinner.setSelection(0, false);

            //設定項目被選取之後的動作
            moneyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    Toast.makeText(badl.this, "你選的是" + moneyspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.checktext);
                    chkItem.setChecked(true);
                }

                @Override
                public void onNothingSelected(AdapterView arg0) {

                    Toast.makeText(badl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
                }
            });

            //小便
            listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, listpiss);
            shoppingspinner.setAdapter(listAdapter);
            shoppingspinner.setSelection(0, false);

            //設定項目被選取之後的動作
            shoppingspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    Toast.makeText(badl.this, "你選的是" + shoppingspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.checktext);
                    chkItem.setChecked(true);
                }

                @Override
                public void onNothingSelected(AdapterView arg0) {

                    Toast.makeText(badl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
                }
            });

            //如廁
            listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, listToilets);
            homespinner.setAdapter(listAdapter);
            homespinner.setSelection(0, false);

            //設定項目被選取之後的動作
            homespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    Toast.makeText(badl.this, "你選的是" + homespinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.checktext);
                    chkItem.setChecked(true);
                }

                @Override
                public void onNothingSelected(AdapterView arg0) {

                    Toast.makeText(badl.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
                }
            });
        }
        //外出

        //提交
        caculate.setOnClickListener(new Button.OnClickListener(){

            @Override

            public void onClick(View v) {

                //傳送data

                String answer="";
                //計算進食
                eatstr = String.valueOf(phonespinner.getSelectedItem());
                if(eatstr=="自主獨立"){
                    s=s+3;
                    eatscore=3;}
                else if(eatstr=="需要協助"){
                    s=s+2;
                    eatscore=2;}
                else if(eatstr=="完全依賴") {
                    s = s + 1;
                    eatscore=1;
                }
                answer+=eatstr+"\n";
                //計算洗澡
                bathestr = String.valueOf(cookspinner.getSelectedItem());
                if(bathestr=="不須幫忙"){
                    s=s+3;
                    bathescore=3;}
                else if(bathestr=="需要協助"){
                    s=s+1;
                    bathescore=1;}
                answer+=bathestr+"\n";
                //計算衛生
                healthstr = String.valueOf(washspinner.getSelectedItem());
                if(healthstr=="不須幫忙") {
                    s = s + 3;
                    healthscore=3;}
                else if(healthstr=="需要協助"){
                    s=s+1;
                    healthscore=1;}
                answer+=healthstr+"\n";

                //計算穿衣
                dressingstr = String.valueOf(medicinespinner.getSelectedItem());
                if(dressingstr=="自主獨立"){
                    s=s+3;
                    dressingscore=3;}
                else if(dressingstr=="需要協助但自己可以做一半"){
                    s=s+2;
                    dressingscore=2;}
                else if(dressingstr=="完全依賴"){
                    s=s+1;
                    dressingscore=1;}
                answer+=dressingstr+"\n";

                //計算大便控制
                Stoolstr = String.valueOf(moneyspinner.getSelectedItem());
                if(Stoolstr=="能完全自我控制"){
                    s=s+3;
                    Stoolscore=3;}
                else if(Stoolstr=="偶而失控"){
                    s=s+2;
                    Stoolscore=2;}
                else if(Stoolstr=="失禁或需灌腸劑"){
                    s=s+1;
                    Stoolscore=1;}
                answer+=Stoolstr+"\n";

                //計算小便控制
                pissstr = String.valueOf(shoppingspinner.getSelectedItem());
                if(pissstr=="能完全自我控制"){
                    s=s+3;
                    pissscroe=3;}
                else if(pissstr=="需要協助但某些可自己做")
                {   s=s+2;
                    pissscroe=2;}
                else if(pissstr=="完全依賴"){
                    s=s+1;
                    pissscroe=1;}
                answer+=pissstr+"\n";

                //計算如廁
                Toiletsstr = String.valueOf(homespinner.getSelectedItem());
                if(Toiletsstr=="自主獨立"){
                    s=s+3;
                    Toiletsscore=3;}
                else if(Toiletsstr=="偶而失控"){
                    s=s+2;
                    Toiletsscore=2;}
                else if(Toiletsstr=="失禁或需灌腸劑"){
                    s=s+1;
                    Toiletsscore=1;}
                answer+=pissstr;

                score.setText("分數：" + s);
                score.setTextSize(40);
                score.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                Toast.makeText(badl.this, "您的分數是"+s, Toast.LENGTH_SHORT).show();

                score.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.badl, 0);
                if(lost>=3||s<15)
                {
                    score.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.adl_3, 0);

                    score.setTextColor(Color.RED);
                }
                else if(s<=18 && s>=16)
                {
                    score.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.adl_2, 0);
                    score.setTextColor(Color.BLUE);
                }
                else if(s>18)
                {
                    score.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.adl_1, 0);
                    score.setTextColor(Color.GREEN);
                }

                totalscore=s;
                txtdata+="S"+totalscore+"\n"+answer+"\n";
                caculate.setVisibility(View.INVISIBLE);
                writetxtdata();
                s=0;
                Thread t=new Thread(new sendPostRunnable());
                t.start();
            }

        });




        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
//============================= writedata =====================================================================================
private void writetxtdata() {
    try {
        File mSDFile = Environment.getExternalStorageDirectory();

        Log.v("filewriter ", txtdata);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH_mm");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        String filename;
        filename = "BADL問卷"+formatter.format(curDate);


        File mFile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/");
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

    }
    catch (Exception e)
    {
        Log.v("filewriter error",e.toString());
    }

}
    private void readdata(String filename)
    {

        try
        {
            //取得SD卡儲存路徑
            File mSDFile = Environment.getExternalStorageDirectory();
            //讀取文件檔路徑
            File file = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + filename + ".txt");
            if(file.exists()) {
                Log.v("file", "exist");

                FileReader mFileReader = new FileReader(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + filename + ".txt");


                BufferedReader mBufferedReader = new BufferedReader(mFileReader);
                String mReadText = "";
                String mTextLine = mBufferedReader.readLine();


                //一行一行取出文字字串裝入String裡，直到沒有下一行文字停止跳出

                //============================================================
                // r , t , w , l , s , p , x
                // viewdata_index=0;
                // Queue peakqueue = new LinkedList(),peakqueu2 = new LinkedList();
                String[] adapterstring=new String[1];
                int index=1;
                while (mTextLine != null) {
                    //  Log.v("mtextline",mTextLine);
                    //  Log.v("readline",mTextLine);
                    Log.v("mTextLine "+index,mTextLine);

                    switch (mTextLine.charAt(0)) {
                        case 'S':
                            totalscore=Integer.valueOf(mTextLine.substring(1));
                            break;
                        default:
                            adapterstring=new String[]{mTextLine};
                            switch (index)
                             {
                                 case 1:
                                     listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, adapterstring);
                                     phonespinner.setAdapter(listAdapter);
                                     break;
                                 case 2:
                                     listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, adapterstring);
                                     cookspinner.setAdapter(listAdapter);
                                     break;
                                 case 3:
                                     listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, adapterstring);
                                     medicinespinner.setAdapter(listAdapter);
                                     break;
                                 case 4:
                                     listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, adapterstring);
                                     moneyspinner.setAdapter(listAdapter);
                                     break;
                                 case 5:
                                     listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, adapterstring);
                                     washspinner.setAdapter(listAdapter);
                                     break;
                                 case 6:
                                     listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, adapterstring);
                                     homespinner.setAdapter(listAdapter);
                                     break;
                                 case 7:
                                     listAdapter = new ArrayAdapter<String>(this,  R.layout.myspinner, adapterstring);
                                     shoppingspinner.setAdapter(listAdapter);
                                     CheckedTextView chkItem =(CheckedTextView)shoppingspinner.getSelectedView().findViewById(R.id.checktext);
                                     chkItem.setChecked(true);
                                     break;
                             }
                            index++;
                            break;
                    }
                    mTextLine = mBufferedReader.readLine();
                }

                //  Log.v("mspindex",mspindex1+" "+mspindex2);
                // Log.v("viewdata_index",viewdata_index+" ratio : "+ratio);
            }

        }
        catch(Exception e)
        {
            Log.v("readdata error",e.fillInStackTrace().toString()+e.getMessage()+e.getLocalizedMessage());

        }
        caculate.setVisibility(View.INVISIBLE);
        score.setText(" 分數：" + totalscore);
        score.setTextSize(40);
        score.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        Toast.makeText(badl.this, "您的分數是"+totalscore, Toast.LENGTH_SHORT).show();


        if(lost>=3||totalscore<15)
        {
            score.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.adl_3, 0);
            score.setTextColor(Color.RED);
        }
        else if(totalscore<=18 && totalscore>=16)
        {
            score.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.adl_2, 0);
            score.setTextColor(Color.BLUE);
        }
        else if(totalscore>18)
        {
            score.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.adl_1, 0);
            score.setTextColor(Color.GREEN);
        }
    }
    //==============================================================================================================
    private String uriAPI = "http://192.168.1.227/test/BADL1.php";
    protected static final int REFRESH_DATA = 0x00000001;
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 顯示網路上抓取的資料
                case REFRESH_DATA:
                    String result = null;
                    if (msg.obj instanceof String)
                        result = (String) msg.obj;
                    if (result != null)
                        // 印出網路回傳的文字
                        //Toast.makeText(badl.this, result, Toast.LENGTH_LONG).show();
                        break;
            }
        }
    };

    private String sendPostDataToInternet()
    {
      /* 建立HTTP Post連線 */
        HttpPost httpRequest = new HttpPost(uriAPI);
      /*
       * Post運作傳送變數必須用NameValuePair[]陣列儲存
       */
        List<NameValuePair> params = new ArrayList<NameValuePair>();
      /*  List<HashMap<String, Float>> params = new ArrayList<HashMap<String, Float>>();
        HashMap<String, Float> map = new HashMap<String, Float>();*/

        params.add(new BasicNameValuePair("username",surface.username));
        params.add(new BasicNameValuePair("Sec", String.valueOf(eatscore)));
        params.add(new BasicNameValuePair("Uptime", String.valueOf(bathescore)));
        params.add(new BasicNameValuePair("Walktime", String.valueOf(healthscore)));
        params.add(new BasicNameValuePair("Turnaroundtime", String.valueOf(dressingscore)));
        params.add(new BasicNameValuePair("Walkbacktime", String.valueOf(Stoolscore)));
        params.add(new BasicNameValuePair("Step", String.valueOf(pissscroe)));
        params.add(new BasicNameValuePair("accdata", String.valueOf(Toiletsscore)));
        params.add(new BasicNameValuePair("score", String.valueOf(totalscore)));


        Log.v("params", params.toString());

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

            e.printStackTrace();

        }

        return null;

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

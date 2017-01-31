package com.example.user.surface;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by zhengwei on 2016/11/27.
 */
public class ADL_activity extends AppCompatActivity {

    //======================= main ======================================
    private TextView maintext;
    //======================= Print ======================================
    private String viewfilename="";
    private int totalscore=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adl_layout);
        Log.v("inadl", "");
        maintext=(TextView)findViewById(R.id.textView10);
//========================= Bundle =============================================
        Bundle bundle= this.getIntent().getExtras();
        viewfilename=bundle.getString("filename");
        setTitle(viewfilename + "結果");
        readdata(viewfilename);
        maintext.setText(maintext.getText() + "\n\n\n總分數 : " + totalscore);
        Log.v("maintext",maintext.getText().toString());
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
                int index = 0,mspindex1=0,mspindex2=0,pointindex=0,peakindex=0,peakindex1=0;
               // viewdata_index=0;
               // Queue peakqueue = new LinkedList(),peakqueu2 = new LinkedList();

                while (mTextLine != null) {
                  //  Log.v("mtextline",mTextLine);
                    //  Log.v("readline",mTextLine);
                    switch (mTextLine.charAt(0)) {
                        case 'S':
                            totalscore=Integer.valueOf(mTextLine.substring(1));
                            break;
                        default:
                            maintext.setText(mTextLine);
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
            Log.v("readdata error",e.toString());

        }
    }
}

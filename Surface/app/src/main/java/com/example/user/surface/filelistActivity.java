package com.example.user.surface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengwei on 2016/9/25.
 */
public class filelistActivity extends AppCompatActivity {
    private ListView mainlist;
    private Button btn_delete;
    private int mode=0;

    private List<Integer> deletefilelist;
    private List<String> filelist= new ArrayList<>();
    private boolean nodata=true;
    private MenuItem de;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist_activity);
        setTitle("測驗紀錄");
        //========= main =======================
        mainlist=(ListView)findViewById(R.id.listView);
        btn_delete=(Button)findViewById(R.id.button);
        btn_delete.setVisibility(View.INVISIBLE);
        listtestLog();
        //======================================
      /*  final ListAdapter adapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1 ,MainActivity.filelist.toArray(new String[MainActivity.filelist.size()])){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);

                TextView textview = (TextView) view.findViewById(android.R.id.text1);
                Log.v("in","..");
                //Set your Font Size Here.
                textview.setTextSize(50);
                textview.setBackgroundColor(Color.BLUE);
                return view;
            }
        };*/

      //  mainlist.setAdapter(adapter);
        mainlist.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mode) {
                    case 0:
                        pointview(filelist.get(position));
                        break;
                    case 1:
                    case 2:
                        Log.v("position", position + "");
                        Log.v("position", mainlist.isItemChecked(position) + "");
                        if (mainlist.isItemChecked(position)) {
                            deletefilelist.add(position);
                            Log.v("deletelist", deletefilelist.indexOf(position) + "");
                        } else {
                            Log.v("deletelist", deletefilelist.indexOf(position) + "");

                            deletefilelist.remove(deletefilelist.indexOf(position));

                            //   Log.v("deletelist delete", deletefilelist.indexOf(position) + "");

                        }
                        break;
                }
            }
        });
        btn_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(filelistActivity.this)
                        .setTitle("測驗紀錄")
                                //.setMessage("選擇一裝置進行配對")
                        .setMessage("共刪除 " + deletefilelist.size() + " 筆資料" + "\n\n確定刪除?")
                        .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String deletefilename;
                                for (int i = 0; i < deletefilelist.size(); i++) {
                                    try {
                                        deletefilename = mainlist.getItemAtPosition(deletefilelist.get(i)).toString();
                                        Log.v("deletefilename", deletefilename);
                                        File mSDFile = Environment.getExternalStorageDirectory();
                                        //讀取文件檔路徑
                                        File deletefile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + deletefilename + ".txt");
                                        deletefile.delete();
                                        if (!deletefile.exists()) {
                                            Toast.makeText(filelistActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();

                                        }
                                       // MainActivity.filelist.remove(deletefilename);
                                        filelist.remove(deletefilename);
                                        // Toast.makeText(TestActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();


                                    } catch (Exception x) {
                                        Log.v("delete exception", x.toString());
                                    }
                                }
                                final ListAdapter adapter = new ArrayAdapter<String>(filelistActivity.this , android.R.layout.simple_list_item_1 ,filelist.toArray(new String[filelist.size()])){
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent){

                                        View view = super.getView(position, convertView, parent);

                                        TextView textview = (TextView) view.findViewById(android.R.id.text1);
                                        //Set your Font Size Here.
                                        textview.setTextSize(25);
                                        return view;
                                    }
                                };
                              //  ListAdapter nadapter = new ArrayAdapter<>(filelistActivity.this, android.R.layout.simple_list_item_1, MainActivity.filelist.toArray(new String[MainActivity.filelist.size()]));
                                mainlist.setAdapter(adapter);
                                btn_delete.setVisibility(View.INVISIBLE);
                                mode = 0;
                                de.setTitle("刪除紀錄");
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();


            }
        });


    }
    @Override
    protected void onResume(){
        super.onResume();
        listtestLog();
        final ListAdapter adapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1 ,filelist.toArray(new String[filelist.size()])){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);

                TextView textview = (TextView) view.findViewById(android.R.id.text1);
                Log.v("in","..");
                //Set your Font Size Here.
                textview.setTextSize(25);

                return view;
            }
        };       mainlist.setAdapter(adapter);
        Log.v("log", "onresume");
    }
    public  void pointview(String filename)
    {
        Log.v("filename",filename);
        Intent intent = new Intent();
        intent.setClass(filelistActivity.this, TestActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putInt("fileid", filelist.indexOf(filename));
        bundle.putBoolean("nodata",nodata);
        intent.putExtras(bundle);

        startActivity(intent);


    }
    private void listtestLog()
    {
        filelist.clear();
        File mSDFile = Environment.getExternalStorageDirectory();
        File file = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP");
        File[] files = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });
        if(files.length==0)
        {
        filelist.add("10公尺走路測驗_" +"2016年09月29日12:05:26");
        filelist.add("起立行走測驗_" +"2016年09月29日12:07:32");
            Log.v("filelist","null");
        }
       else{
            for (int i = 0; i < files.length; i++) {
                filelist.add(files[i].getName().substring(0, files[i].getName().length() - 4));
                //Log.v("files",files[i].getName().substring(0,files[i].getName().length()-4));
            }
            Log.v("filelist","not null"+files.length);
            nodata=false;
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        de=menu.getItem(0);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
       //Log.v("item id ", item.getTitle().toString());
        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_settings:
                if(mode==0) {
                    ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,filelist.toArray(new String[filelist.size()]));
                    //mainlist.setClickable(false);

                    mainlist.setAdapter(adapter);
                    mainlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    mode = 1;
                    btn_delete.setVisibility(View.VISIBLE);

                    deletefilelist = new ArrayList<>();
                   //item.setTitle("全選");
                    de.setTitle("全選");
                }
                else if(mode==1)
                {
                   // Log.v("filelist length",MainActivity.filelist.size()+"");
                    for(int i=0; i<filelist.size();i++) {

                        if(!mainlist.isItemChecked(i))
                        { deletefilelist.add(i);}
                        mainlist.setItemChecked(i, true);
                    }
                    mode=2;
                    de.setTitle("取消選取");
                }
                else if(mode==2)
                {
                    Log.v("deletefilelist length",deletefilelist.size()+"");

                            for(int i=0;i<deletefilelist.size();i++)
                            mainlist.setItemChecked(i, false);
                        deletefilelist.clear();
                    de.setTitle("全選");
                    mode=1;
                }
                break;
               /* try {
                    File mSDFile = Environment.getExternalStorageDirectory();
                    //讀取文件檔路徑
                    File deletefile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/GOGOAPP/" + viewfilename+".txt");
                    deletefile.delete();
                    if(!deletefile.exists())
                    {
                        Toast.makeText(TestActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();

                    }
                    MainActivity.filelist.remove(fileid);
                    // Toast.makeText(TestActivity.this, "已刪除此紀錄", Toast.LENGTH_SHORT).show();


                }
                catch (Exception x)
                {
                    Log.v("delete exception", x.toString());
                }
                break;
            case R.id.action_deleteall:
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
                    Toast.makeText(filelistActivity.this, "已刪除所有紀錄", Toast.LENGTH_SHORT).show();

                }
                catch (Exception x)
                {
                    Log.v("delete exception",x.toString());
                }
                break;*/
        }


        return super.onOptionsItemSelected(item);
    }
}

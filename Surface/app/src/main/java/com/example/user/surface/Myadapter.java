package com.example.user.surface;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zhengwei on 2016/7/20.
 */
public class Myadapter extends BaseAdapter {
    private ArrayList<String> mList,medicine;

    Context c;
    public Myadapter(Context context){
        mList = new ArrayList<String>();
        medicine = new ArrayList<String>();
     c=context;

    }
    public void addItem(String m,String i){
        mList.add(i);
        medicine.add(m);
    }
    public void removeItem(int index){
        mList.remove(index);
        medicine.remove(index);
    }
    public String getItemname(int index){
        return mList.get(index);
    }
    public String getmedicinename(int index){
        return medicine.get(index);
    }

    public int getTime(int index)
    {
        String time=mList.get(index);
        int hour,minute,mid;
        mid=time.indexOf(":");
        Log.v("time",time+" "+mid+"____"+time.substring(0, mid - 1)+" : "+time.substring( mid + 2,time.length()));
        hour=Integer.parseInt(time.substring(0, mid - 1));
        minute=Integer.parseInt(time.substring( mid + 2,time.length()));// Log.v("time", hour + "__" + minute);
        return hour*60+minute;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder;
        if(v == null){
            v = LayoutInflater.from(c).inflate(R.layout.listview_item, null);
            holder = new Holder();
            holder.itemText = (TextView) v.findViewById(R.id.text);
           // holder.itemcheck=(CheckBox)v.findViewById(R.id.checkBox);
            holder.medicineText = (TextView) v.findViewById(R.id.textView2);

            v.setTag(holder);
        } else{
            holder = (Holder) v.getTag();
        }
        holder.itemText.setTextSize(30);
        holder.itemText.setText(mList.get(position));
        holder.medicineText.setTextSize(40);
        holder.medicineText.setText(medicine.get(position));

        return v;
    }


}

class Holder{
    TextView itemText,medicineText;
    CheckBox itemcheck;
}

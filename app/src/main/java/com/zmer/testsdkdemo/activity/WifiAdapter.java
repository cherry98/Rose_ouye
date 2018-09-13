package com.zmer.testsdkdemo.activity;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;

import java.util.ArrayList;
import java.util.List;

public class WifiAdapter extends BaseAdapter {
    private Activity activity;
    private List<ScanResult> list;
    private ScanResult item;
    private boolean isShowWifiLevel = true;

    public boolean isShowWifiLevel() {
        return isShowWifiLevel;
    }

    public void setShowWifiLevel(boolean isShowWifiLevel) {
        this.isShowWifiLevel = isShowWifiLevel;
    }

    public WifiAdapter(Activity activity, List<ScanResult> list) {
        this.activity = activity;
        if(list == null)
            list = new ArrayList<ScanResult>();
        this.list = list;
    }
    public void updateList(List<ScanResult> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if(list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if(list != null && list.size() != 0) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        item = list.get(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.wifi_name, null);
            holder = new ViewHolder();
            holder.textName = (TextView) convertView.findViewById(R.id.text_wifi_name);
            holder.imgWifiSignal = (ImageView) convertView.findViewById(R.id.img_wifi_level);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textName.setText("" + item.SSID);
        setWifiSingle(item.level, item.capabilities, holder.imgWifiSignal);
        return convertView;
    }

    private void setWifiSingle(int level, String str, ImageView imgWifiSignal) {
        if (level <= 0 && level >= -25) {
            imgWifiSignal.setImageResource(R.drawable.zmer_wifi4_selector);
        } else if (level < -25 && level >= -50) {
            imgWifiSignal.setImageResource(R.drawable.zmer_wifi3_selector);
        } else if (level < -50 && level >= -75) {
            imgWifiSignal.setImageResource(R.drawable.zmer_wifi2_selector);
        }else if(level < -75 && level >= -100){
            imgWifiSignal.setImageResource(R.drawable.zmer_wifi1_selector);
        }
    }

    static class ViewHolder {
        TextView textName;
        ImageView imgWifiSignal;
    }
}

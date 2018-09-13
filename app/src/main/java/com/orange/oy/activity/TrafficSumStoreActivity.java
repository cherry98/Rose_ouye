package com.orange.oy.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.info.TrafficInfo;
import com.orange.oy.view.AppTitle;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 流量统计
 */
public class TrafficSumStoreActivity extends BaseActivity {
    private ArrayList<TrafficInfo> list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trafficsum);
        String project_id = getIntent().getStringExtra("project_id");
        AppTitle trafficsum_title = (AppTitle) findViewById(R.id.trafficsum_title);
        trafficsum_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        trafficsum_title.settingName("流量统计");
        ListView trafficsum_listview = (ListView) findViewById(R.id.trafficsum_listview);
        OfflineDBHelper offlineDBHelper = new OfflineDBHelper(this);
        list = offlineDBHelper.getTrafficForStore(AppInfo.getName(this), project_id);
        if (!list.isEmpty()) {
            trafficsum_listview.setAdapter(new MyAdapter());
        } else {
            Tools.showToast(this, "没有网点");
        }
    }


    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = Tools.loadLayout(TrafficSumStoreActivity.this, R.layout.item_listview_trafficsum);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.itemlistviewmytrafficsum_image);
                viewHolder.text = (TextView) convertView.findViewById(R.id.itemlistviewmytrafficsum_text);
                viewHolder.text1 = (TextView) convertView.findViewById(R.id.itemlistviewmytrafficsum_trafficsum);
                convertView.findViewById(R.id.itemlistviewmytrafficsum_right).setVisibility(View.INVISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TrafficInfo trafficInfo = list.get(position);
            viewHolder.text.setText(trafficInfo.getName());
            viewHolder.text1.setText(new BigDecimal(trafficInfo.getSize() / 1024d / 1024d).
                    setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "M");
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView text, text1;
        }
    }
}

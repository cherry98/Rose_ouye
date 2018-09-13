package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.info.MyupdataInfo;
import com.orange.oy.view.AppTitle;

import java.util.ArrayList;

/**
 * 我的上传进度页
 */
public class MyUpdataProjectActivity extends BaseActivity {
    private PullToRefreshListView myupdata_listview;
    private UpdataDBHelper updataDBHelper;
    private ArrayList<MyupdataInfo> myupdataInfoList;
    private MyAdapter myAdapter;

    private void initTitle(String name) {
        AppTitle myupdata_title = (AppTitle) findViewById(R.id.myupdata_title);
        myupdata_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        myupdata_title.settingName(name);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myupdata);
        initTitle(getIntent().getStringExtra("name"));
        updataDBHelper = new UpdataDBHelper(this);
        myupdata_listview = (PullToRefreshListView) findViewById(R.id.myupdata_listview);
        myupdataInfoList = updataDBHelper.getProjectList();
        if (myupdataInfoList != null && !myupdataInfoList.isEmpty()) {
            myAdapter = new MyAdapter();
            myupdata_listview.setAdapter(myAdapter);
            myupdata_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MyUpdataProjectActivity.this, MyUpdataStoreActivity.class);
                    intent.putExtra("projectid", myupdataInfoList.get(--position).getProjectid());
                    intent.putExtra("name", myupdataInfoList.get(position).getStorename());
                    startActivity(intent);
                }
            });
        } else {
            Tools.showToast(this, "没有项目了");
        }
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return myupdataInfoList.size();
        }

        public Object getItem(int position) {
            return myupdataInfoList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = Tools.loadLayout(MyUpdataProjectActivity.this, R.layout.item_listview_myupdata);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.itemlistviewmyupdata_image);
                viewHolder.text = (TextView) convertView.findViewById(R.id.itemlistviewmyupdata_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.text.setText(myupdataInfoList.get(position).getStorename());
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView text;
        }
    }
}

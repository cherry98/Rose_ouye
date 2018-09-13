package com.orange.oy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.info.MyupdataInfo;
import com.orange.oy.view.AppTitle;

import java.util.ArrayList;

/**
 * 我的上传进度页
 */
public class MyUpdataActivity extends BaseActivity {
    private PullToRefreshListView myupdata_listview;
    private UpdataDBHelper updataDBHelper;
    private ArrayList<MyupdataInfo> myupdataInfoList;
    private MyAdapter myAdapter;

    private void initTitle() {
        AppTitle myupdata_title = (AppTitle) findViewById(R.id.myupdata_title);
        myupdata_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        myupdata_title.settingName("上传进度");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myupdata);
        initTitle();
        updataDBHelper = new UpdataDBHelper(this);
        myupdata_listview = (PullToRefreshListView) findViewById(R.id.myupdata_listview);
        myupdataInfoList = updataDBHelper.getStoreList();
        Tools.d("-----" + myupdataInfoList.toString());
        if (myupdataInfoList != null && !myupdataInfoList.isEmpty()) {
            myAdapter = new MyAdapter();
            myupdata_listview.setAdapter(myAdapter);
            myupdata_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    position = position - 1;
                    Intent intent = new Intent(MyUpdataActivity.this, MyUpdataPackageActivity.class);
                    intent.putExtra("storeid", myupdataInfoList.get(position).getStoreid());
                    intent.putExtra("projectid", myupdataInfoList.get(position).getProjectid());
                    startActivity(intent);
                }
            });
        } else {
            Tools.showToast(this, "没有可上传的项目了");
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
                convertView = Tools.loadLayout(MyUpdataActivity.this, R.layout.item_listview_myupdata);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.itemlistviewmyupdata_image);
                viewHolder.text = (TextView) convertView.findViewById(R.id.itemlistviewmyupdata_text);
                viewHolder.itemlistviewmyupdata_name = (TextView) convertView.findViewById(R.id.itemlistviewmyupdata_name);
                viewHolder.itemlistviewmyupdata_num = (TextView) convertView.findViewById(R.id.itemlistviewmyupdata_num);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.text.setText(myupdataInfoList.get(position).getProjectname());
            viewHolder.itemlistviewmyupdata_name.setText(myupdataInfoList.get(position).getStorename());
            viewHolder.itemlistviewmyupdata_num.setText(myupdataInfoList.get(position).getCode());
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView text, itemlistviewmyupdata_name, itemlistviewmyupdata_num;
        }
    }
}

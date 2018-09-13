package com.orange.oy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.info.MyupdataPackage;
import com.orange.oy.view.AppTitle;

import java.util.ArrayList;

/**
 * 我的上传进度页
 */
public class MyUpdataPackageActivity extends BaseActivity {
    private PullToRefreshListView myupdata_listview;
    private UpdataDBHelper updataDBHelper;
    private ArrayList<MyupdataPackage> myupdataInfoList;
    private MyAdapter myAdapter;
    private String projectid, storeid;

    private void initTitle() {
        AppTitle myupdata_title = (AppTitle) findViewById(R.id.myupdata_title);
        myupdata_title.settingName("正在上传");
        myupdata_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myupdata);
        registerReceiver(this);
        initTitle();
        updataDBHelper = new UpdataDBHelper(this);
        myupdata_listview = (PullToRefreshListView) findViewById(R.id.myupdata_listview);
        Intent data = getIntent();
        projectid = data.getStringExtra("projectid");
        storeid = data.getStringExtra("storeid");
        myupdataInfoList = updataDBHelper.getPackageList(projectid, storeid);
        if (myupdataInfoList != null && !myupdataInfoList.isEmpty()) {
            myAdapter = new MyAdapter();
            myupdata_listview.setAdapter(myAdapter);
            myupdata_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyupdataPackage myupdataPackage = myupdataInfoList.get(--position);
                    if (myupdataPackage.isPackage() && !"0".equals(myupdataPackage.getTasktype())) {
                        Intent intent = new Intent(MyUpdataPackageActivity.this, MyUpdataTaskActivity.class);
                        intent.putExtra("projectid", projectid);
                        intent.putExtra("storeid", storeid);
                        intent.putExtra("name", myupdataPackage.getName());
                        intent.putExtra("packageid", myupdataPackage.getId());
                        startActivity(intent);
                    }
                }
            });
        } else {
            Tools.showToast(this, "没有任务了");
        }
    }

    private BroadcastReceiver UpProgressbarBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(AppInfo.BroadcastReceiver_TAKEPHOTO)) {
                String str = intent.getStringExtra("uniquelynum");
                int size = intent.getIntExtra("size", 0);
                if (myupdataInfoList != null) {
                    for (MyupdataPackage myupdataPackage : myupdataInfoList) {
                        ProgressBar progressBar = myupdataPackage.getView();
                        if (progressBar == null || myupdataPackage.isPackage()) {
                            Tools.d("Task----handlerMessage:continue");
                            continue;
                        }
                        if (myupdataPackage.getUniquelyNum().equals(progressBar.getTag()) &&
                                myupdataPackage.getUniquelyNum().equals(str)) {
                            Tools.d("Task----handlerMessage:" + size);
                            progressBar.setProgress(size);
                        }
                    }
                }
            }
        }
    };

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BroadcastReceiver_TAKEPHOTO);
        context.registerReceiver(UpProgressbarBroadcast, filter);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(UpProgressbarBroadcast);
    }


    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this);
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
                convertView = Tools.loadLayout(MyUpdataPackageActivity.this, R.layout.item_listview_myupdata_progress);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.itemlistviewmyupdata_image);
                viewHolder.text = (TextView) convertView.findViewById(R.id.itemlistviewmyupdata_text);
//                viewHolder.itemlistviewmyupdata_right = convertView.findViewById(R.id.itemlistviewmyupdata_right);
                viewHolder.progressbar = (ProgressBar) convertView.findViewById(R.id.progressbar);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            MyupdataPackage myupdataPackage = myupdataInfoList.get(position);
            if (myupdataPackage.isPackage()) {
                viewHolder.image.setImageResource(R.mipmap.task_package);
                if ("01".equals(myupdataPackage.getTasktype()) || "02".equals(myupdataPackage.getTasktype()) || "03".equals
                        (myupdataPackage.getTasktype())) {
                    myupdataPackage.setName("(已关闭)" + myupdataPackage.getName());
                    viewHolder.progressbar.setTag(myupdataPackage.getUniquelyNum());
                    myupdataPackage.setView(viewHolder.progressbar);
                    viewHolder.progressbar.setVisibility(View.VISIBLE);
//                    viewHolder.itemlistviewmyupdata_right.setVisibility(View.GONE);
                } else {
                    viewHolder.progressbar.setTag(null);
                    viewHolder.progressbar.setVisibility(View.GONE);
//                    viewHolder.itemlistviewmyupdata_right.setVisibility(View.VISIBLE);
                }
            } else {
                if ("1".equals(myupdataPackage.getTasktype())) {
                    viewHolder.image.setImageResource(R.mipmap.take_photo);
                } else if ("2".equals(myupdataPackage.getTasktype())) {
                    viewHolder.image.setImageResource(R.mipmap.take_viedo);
                } else if ("3".equals(myupdataPackage.getTasktype())) {
                    viewHolder.image.setImageResource(R.mipmap.take_record);
                } else if ("4".equals(myupdataPackage.getTasktype())) {
                    viewHolder.image.setImageResource(R.mipmap.take_location);
                } else if ("5".equals(myupdataPackage.getTasktype())) {
                    viewHolder.image.setImageResource(R.mipmap.take_tape);
                } else if ("8".equals(myupdataPackage.getTasktype())) {
                    viewHolder.image.setImageResource(R.mipmap.take_photo);
                } else {
                    viewHolder.image.setImageResource(R.mipmap.task_package);
                }
                viewHolder.progressbar.setTag(myupdataPackage.getUniquelyNum());
                myupdataPackage.setView(viewHolder.progressbar);
                viewHolder.progressbar.setVisibility(View.VISIBLE);
//                viewHolder.itemlistviewmyupdata_right.setVisibility(View.GONE);
            }
            viewHolder.text.setText(myupdataPackage.getName());
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView text;
            ProgressBar progressbar;
//            View itemlistviewmyupdata_right;
        }
    }
}

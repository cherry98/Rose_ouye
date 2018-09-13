package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.SlideTouchEventListener;
import com.orange.oy.activity.OfflinePackageActivity;
import com.orange.oy.activity.StoreDescActivity;
import com.orange.oy.allinterface.OfflineStoreClickViewListener;
import com.orange.oy.allinterface.PullToRefreshDeleteListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.view.OfflineStoreView;

import java.util.ArrayList;

public class OfflineStoreRightAdapter extends BaseAdapter implements OfflineStoreClickViewListener, PullToRefreshDeleteListener {
    private Context context;
    private ArrayList<TaskDetailLeftInfo> list;
    private String username;
    private PullToRefreshListView listView;
    private OfflineDBHelper offlineDBHelper;

    public OfflineStoreRightAdapter(Context context, PullToRefreshListView listView, ArrayList<TaskDetailLeftInfo> list) {
        this.listView = listView;
        this.context = context;
        this.list = list;
        username = AppInfo.getName(context);
        offlineDBHelper = new OfflineDBHelper(context);
    }

    public void resetList(ArrayList<TaskDetailLeftInfo> list) {
        this.list = list;
    }

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
        OfflineStoreView offlineStoreView = null;
        if (convertView == null) {
            offlineStoreView = new OfflineStoreView(context);
            offlineStoreView.setOfflineStoreClickViewListener(this);
            offlineStoreView.setPullToRefreshDeleteListener(this);
        } else {
            if (convertView instanceof OfflineStoreView)
                offlineStoreView = (OfflineStoreView) convertView;
        }
        if (offlineStoreView == null) {
            return convertView;
        }
        TaskDetailLeftInfo taskDetailLeftInfo = list.get(position);
        if (taskDetailLeftInfo.getIsCompleted() == 1) {
            offlineStoreView.setVisibilityButton1(View.GONE);
        } else {
            offlineStoreView.setVisibilityButton1(View.VISIBLE);
            offlineStoreView.setButton1Text("执行");
        }
        offlineStoreView.setTime(taskDetailLeftInfo.getTimedetail());
        offlineStoreView.setName(taskDetailLeftInfo.getName());
        offlineStoreView.setCode(taskDetailLeftInfo.getCode());
        offlineStoreView.setCity(taskDetailLeftInfo.getCity3());
        offlineStoreView.setNumber(username);
        offlineStoreView.setTag(position);
        return offlineStoreView;
    }

    public void select(View view) {
        if (view instanceof SlideTouchEventListener)
            listView.setSlideTouchEventListener((SlideTouchEventListener) view);
    }

    public void delete(Object object) {
        if (object != null) {
            TaskDetailLeftInfo taskDetailLeftInfo = list.get((int) object);
            offlineDBHelper.deleteStore(username, taskDetailLeftInfo.getProjectid(), taskDetailLeftInfo.getId());
            list.remove((int) object);
            notifyDataSetChanged();
        }
    }

    public void click(Object object) {
        if (object != null) {
            TaskDetailLeftInfo taskDetailLeftInfo = list.get((int) object);
            if (TextUtils.isEmpty(taskDetailLeftInfo.getOutletnote())) {
                Intent intent = new Intent(context, OfflinePackageActivity.class);
                intent.putExtra("id", taskDetailLeftInfo.getId());
                intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                intent.putExtra("province", taskDetailLeftInfo.getCity());
                intent.putExtra("city", taskDetailLeftInfo.getCity2());
                intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark());
                intent.putExtra("code", taskDetailLeftInfo.getCodeStr());
                intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, StoreDescActivity.class);
                intent.putExtra("id", taskDetailLeftInfo.getId());
                intent.putExtra("projectname", taskDetailLeftInfo.getProjectname());
                intent.putExtra("store_name", taskDetailLeftInfo.getName());
                intent.putExtra("store_num", taskDetailLeftInfo.getCode());
                intent.putExtra("province", taskDetailLeftInfo.getCity());
                intent.putExtra("city", taskDetailLeftInfo.getCity2());
                intent.putExtra("project_id", taskDetailLeftInfo.getProjectid());
                intent.putExtra("photo_compression", taskDetailLeftInfo.getPhoto_compression());
                intent.putExtra("is_record", taskDetailLeftInfo.getIs_record());
                intent.putExtra("is_watermark", taskDetailLeftInfo.getIs_watermark());
                intent.putExtra("code", taskDetailLeftInfo.getCodeStr());
                intent.putExtra("brand", taskDetailLeftInfo.getBrand());
                intent.putExtra("isOffline", true);
                intent.putExtra("outletnote", taskDetailLeftInfo.getOutletnote());
                intent.putExtra("is_takephoto", taskDetailLeftInfo.getIs_taskphoto());
                context.startActivity(intent);
            }
        }
    }
}

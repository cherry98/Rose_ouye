package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.SlideTouchEventListener;
import com.orange.oy.allinterface.NewOnItemClickListener;
import com.orange.oy.allinterface.OfflineStoreClickViewListener;
import com.orange.oy.allinterface.OnRightClickListener;
import com.orange.oy.allinterface.PullToRefreshDeleteListener;
import com.orange.oy.info.TaskitemListInfo;
import com.orange.oy.view.TaskitemDetail_12View;

import java.util.ArrayList;

/**
 * 任务列表适配器 //TODO NEW
 */
public class TaskitemListAdapter_12 extends BaseAdapter implements OfflineStoreClickViewListener, PullToRefreshDeleteListener {
    private ArrayList<TaskitemListInfo> list;
    private Context context;
    private PullToRefreshListView listView;
    private boolean isShowProgressbar = false;//是否显示进度条

    public void isShowProgressbar(boolean showProgressbar) {
        isShowProgressbar = showProgressbar;
    }

    public TaskitemListAdapter_12(Context context, PullToRefreshListView listView, ArrayList<TaskitemListInfo> list) {
        this.listView = listView;
        this.list = list;
        this.context = context;
    }

    public void resetList(ArrayList<TaskitemListInfo> list) {
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

    private String rightText;

    public void settingRightText(String rightText) {
        this.rightText = rightText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TaskitemDetail_12View taskitemDetail_12View = null;
        if (convertView == null) {
            taskitemDetail_12View = new TaskitemDetail_12View(context);
            taskitemDetail_12View.settingRightText(rightText + "");
            taskitemDetail_12View.setOfflineStoreClickViewListener(this);
            taskitemDetail_12View.setPullToRefreshDeleteListener(this);
        } else {
            taskitemDetail_12View = (TaskitemDetail_12View) convertView;
        }
        TaskitemListInfo taskitemListInfo = list.get(position);
        taskitemDetail_12View.settingForTask(taskitemListInfo);
        if (isShowProgressbar) {
            taskitemDetail_12View.isShowProgressbar(true, taskitemListInfo.is_Record());
            if (taskitemListInfo.getState().equals("2")) {
                taskitemDetail_12View.settingProgressbar(100);
            } else {
                taskitemDetail_12View.settingProgressbar(taskitemListInfo.getProgress());
                if (taskitemListInfo.getProgress() >= 100) {
                    taskitemListInfo.setState("2");
                }
            }
            taskitemListInfo.setTaskitemDetail_12View(taskitemDetail_12View);
        }
        return taskitemDetail_12View;
    }


    public void select(View view) {
        if (view instanceof SlideTouchEventListener)
            listView.setSlideTouchEventListener((SlideTouchEventListener) view);
    }

    public void delete(Object object) {
        if (onRightClickListener != null) {
            onRightClickListener.onRightClick(object);
        }
    }

    public void click(Object object) {
        if (newOnItemClickListener != null) {
            newOnItemClickListener.onItemClick(object);
        }
    }

    private NewOnItemClickListener newOnItemClickListener;
    private OnRightClickListener onRightClickListener;

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    public void setNewOnItemClickListener(NewOnItemClickListener newOnItemClickListener) {
        this.newOnItemClickListener = newOnItemClickListener;
    }
}

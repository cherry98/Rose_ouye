package com.orange.oy.view;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.TaskitemRecodillustrateActivity;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.base.BaseView;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskFinishInfo;
import com.orange.oy.reord.AudioManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class FinishrecodeView extends LinearLayout implements View.OnClickListener, FinishTaskProgressRefresh, BaseView, RecodePlayView.OnRecodePlayerListener {
    public FinishrecodeView(Context context, String name, TaskFinishInfo taskFinishInfo, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_recode);
        init(isAgain);
        viewfdt_recode_name.setText(name);
        this.taskFinishInfo = taskFinishInfo;
    }

    private TextView viewfdt_recode_name;
    private View viewfdt_recode_note_layout;
    private LinearLayout viewfdt_recode_value;
    private TaskFinishInfo taskFinishInfo;
    private TextView viewfdt_recode_note;
    private TextView viewfdt_recode_progressvalue;
    private ProgressBar viewfdt_recode_progress;

    private void init(boolean isAgain) {
        recList = new ArrayList<>();
        viewfdt_recode_progressvalue = (TextView) findViewById(R.id.viewfdt_recode_progressvalue);
        viewfdt_recode_progress = (ProgressBar) findViewById(R.id.viewfdt_recode_progress);
        viewfdt_recode_value = (LinearLayout) findViewById(R.id.viewfdt_recode_value);
        viewfdt_recode_name = (TextView) findViewById(R.id.viewfdt_recode_name);
        viewfdt_recode_note = (TextView) findViewById(R.id.viewfdt_recode_note);
        viewfdt_recode_note_layout = findViewById(R.id.viewfdt_recode_note_layout);
        if (isAgain)
            findViewById(R.id.viewfdt_recode_reset).setOnClickListener(this);
        else
            findViewById(R.id.viewfdt_recode_reset).setVisibility(GONE);
        findViewById(R.id.viewfdt_recode_layout).setOnClickListener(this);
    }

    private boolean isProgress;

    public void setIsProgress(boolean isshow) {
        isProgress = isshow;
        if (isProgress) {
            viewfdt_recode_progressvalue.setVisibility(VISIBLE);
            viewfdt_recode_progress.setVisibility(VISIBLE);
            findViewById(R.id.viewfdt_recode_layout).setOnClickListener(null);
        }
    }

    public void settingNote(String note) {
        if (TextUtils.isEmpty(note) || note.equals("null")) return;
        try {
            viewfdt_recode_note.setText(URLDecoder.decode(note.replaceAll("\\[\"", "").replaceAll("\"]", ""), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    ArrayList<RecodePlayView> recList;

    public void settingRecs(Activity activitie, String[] soundStrs) {
        for (String soundStr : soundStrs) {
            if (TextUtils.isEmpty(soundStr)) continue;
            RecodePlayView recodePlayView = new RecodePlayView(activitie);
            recodePlayView.settingREC(soundStr);
            recodePlayView.setOnRecodePlayerListener(this);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = Tools.dipToPx(activitie, 15);
            viewfdt_recode_value.addView(recodePlayView, layoutParams);
            recList.add(recodePlayView);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.viewfdt_recode_play: {
//                Intent it = new Intent(Intent.ACTION_VIEW);
//                String soundStr = viewfdt_recode_play.getTag().toString();
//                if (soundStr.startsWith("http://")) {
//                    it.setDataAndType(Uri.parse(soundStr), "audio/MP3");
//                } else {
//                    it.setDataAndType(Uri.parse("file://" + soundStr), "audio/MP3");
//                }
//                try {
//                    getContext().startActivity(it);
//                } catch (ActivityNotFoundException exception) {
//                    exception.printStackTrace();
//                    Tools.showToast(getContext(), "没有可用播放程序");
//                }
//            }
//            break;
            case R.id.viewfdt_recode_reset: {
                if (taskFinishInfo == null) return;
                Intent intent = new Intent(getContext(), TaskitemRecodillustrateActivity.class);
                intent.putExtra("task_pack_id", taskFinishInfo.getPid());
                intent.putExtra("task_id", taskFinishInfo.getTaskid());
                intent.putExtra("store_id", taskFinishInfo.getStoreid());
                intent.putExtra("category1", taskFinishInfo.getCategory1());
                intent.putExtra("category2", taskFinishInfo.getCategory2());
                intent.putExtra("category3", taskFinishInfo.getCategory3());
                intent.putExtra("project_id", taskFinishInfo.getProjectid());
                intent.putExtra("project_name", taskFinishInfo.getProjectname());
                intent.putExtra("task_pack_name", taskFinishInfo.getPackage_name());
                intent.putExtra("task_name", taskFinishInfo.getName());
                intent.putExtra("store_num", taskFinishInfo.getStorenum());
                intent.putExtra("store_name", taskFinishInfo.getStorename());
                intent.putExtra("outlet_batch", taskFinishInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskFinishInfo.getP_batch());
                getContext().startActivity(intent);
            }
            break;
            case R.id.viewfdt_recode_layout: {
                if (viewfdt_recode_value.getVisibility() == VISIBLE) {
                    viewfdt_recode_value.setVisibility(GONE);
                    viewfdt_recode_note_layout.setVisibility(GONE);
                } else {
                    viewfdt_recode_value.setVisibility(VISIBLE);
                    viewfdt_recode_note_layout.setVisibility(VISIBLE);
                }
            }
            break;
        }
    }

    @Override
    public void setProgress(int progress) {
        if (isProgress) {
            viewfdt_recode_progress.setProgress(progress);
            if (progress < 100) {
                viewfdt_recode_progressvalue.setText(progress + "%");
            } else {
                viewfdt_recode_progress.setVisibility(GONE);
                viewfdt_recode_progressvalue.setVisibility(GONE);
            }
        }
    }

    @Override
    public Object getInfo() {
        return taskFinishInfo;
    }

    @Override
    public void onResume(Object object) {
    }

    @Override
    public void onPause(Object object) {
        if (recList != null) {
            for (RecodePlayView recodePlayView : recList) {
                recodePlayView.stopPlaying();
            }
        }
    }

    @Override
    public void onStop(Object object) {
        if (recList != null) {
            for (RecodePlayView recodePlayView : recList) {
                recodePlayView.stopPlaying();
            }
        }
    }

    @Override
    public void onDestory(Object object) {
        if (recList != null) {
            for (RecodePlayView recodePlayView : recList) {
                recodePlayView.onFinishView();
            }
        }
    }

    @Override
    public Object getBaseData() {
        return taskFinishInfo;
    }

    @Override
    public void play(RecodePlayView recodePlayView) {
        AudioManager.stopPlaying();
        MyVideoView.closeAllMyVideoView();
        RecodePlayView.closeAllRecodeplay(recodePlayView.hashCode());
    }

    @Override
    public void stop(RecodePlayView recodePlayView) {

    }
}

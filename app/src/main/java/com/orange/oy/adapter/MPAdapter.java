package com.orange.oy.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.Mp3Model;
import com.orange.oy.util.Player;
import com.orange.oy.view.RecodePlayView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/12/1.
 */

public class MPAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<Mp3Model> datas = new ArrayList<>();
    public String concerns_name;
    private Handler handler;
    private int currentPos = 0;
    private boolean Visibility1 = false;

    public MPAdapter(Context context, List object) {
        this.context = context;
        this.datas = object;
        this.layoutInflater = LayoutInflater.from(context);
        this.handler = new Handler();
    }

    public void onPauseState() {
        for (int i = 0; i < datas.size(); i++) {
            if (null != datas.get(i).getPlayer()) {
                datas.get(i).getPlayer().pause();
                datas.get(i).setCurrentState(Mp3Model.STATE_PAUSE);
            }
        }
        notifyDataSetChanged();
    }

    public void setVisibility(boolean v) {
        this.Visibility1 = v;
    }

    public boolean isShow() {
        return Visibility1;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_lista2, parent, false);
            holder = new ViewHolder();
            holder.MusicSeekBar = (SeekBar) convertView.findViewById(R.id.MusicSeekBar);
            holder.iv_recodeplayview = (RecodePlayView) convertView.findViewById(R.id.iv_recodeplayview);
            holder.iv_recodeplayview.setOnRecodePlayerListener(onRecodePlayerListener);
            holder.iv_control = (ImageView) convertView.findViewById(R.id.iv_control);
            holder.MusicTime = (TextView) convertView.findViewById(R.id.MusicTime);
            holder.itemmp_del = (TextView) convertView.findViewById(R.id.itemmp_del);
            holder.itemmp_reply = (TextView) convertView.findViewById(R.id.itemmp_reply);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Mp3Model mp3Model = datas.get(position).setRecodePlayView(holder.iv_recodeplayview);
        holder.iv_recodeplayview.settingREC(mp3Model.getPath());

        if (Visibility1) {
            holder.itemmp_del.setVisibility(View.VISIBLE);
            holder.itemmp_reply.setVisibility(View.VISIBLE);
        } else {
            holder.itemmp_del.setVisibility(View.GONE);
            holder.itemmp_reply.setVisibility(View.GONE);
        }
        holder.itemmp_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
                mPdelInterface.Delonclick(position, mp3Model.getPath());
            }
        });

        holder.itemmp_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重做
                mPdelInterface.Replyonclick(position, mp3Model.getPath());
            }
        });

        return convertView;
    }


    private boolean checkUrl(String path) {
        if (!path.endsWith(".amr")) return true;
        if (!path.contains("http:")) return true;
        // String[] paths = path.split("http:");
        // DebugLog.e("tag", "paths========》》》" + paths.length);
        // if (paths.length != 2) return true;
        return false;
    }


    private class ViewHolder {
        RecodePlayView iv_recodeplayview;
        SeekBar MusicSeekBar;
        Button BtnStop, BtnPlayorPause;
        TextView MusicTime;
        ImageView iv_control;
        TextView itemmp_del, itemmp_reply;
    }

    private RecodePlayView.OnRecodePlayerListener onRecodePlayerListener = new RecodePlayView.OnRecodePlayerListener() {
        @Override
        public void play(RecodePlayView recodePlayView) {
            RecodePlayView.closeAllRecodeplay(recodePlayView.hashCode());
        }

        @Override
        public void stop(RecodePlayView recodePlayView) {

        }
    };

    private MPdelInterface mPdelInterface;

    public interface MPdelInterface {

        void Delonclick(int position, String path);

        void Replyonclick(int position, String path);

    }

    public void setAbandonButtonListener(MPdelInterface mPdelInterface) {
        this.mPdelInterface = mPdelInterface;
    }

}


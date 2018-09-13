package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.NewmessageInfo;
import com.orange.oy.network.Urls;

import java.util.ArrayList;

/**
 * V3.12
 */

/**
 * 消息列表适配器
 */
public class NewMessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NewmessageInfo> list;

    public NewMessageAdapter(Context context, ArrayList<NewmessageInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_new_message);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.tv_times = (TextView) convertView.findViewById(R.id.tv_times);
            viewHolder.iv_like = (ImageView) convertView.findViewById(R.id.iv_like);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final NewmessageInfo newmessageInfo = list.get(position);

        Glide.with(context).load(Urls.ImgIp + newmessageInfo.photo_url).into(viewHolder.iv_pic);

        viewHolder.tv_times.setText(newmessageInfo.begin_date + "~" + newmessageInfo.end_data + " 可执行");

        //*****   "state": 0,//状态，0为为评价，1为喜欢，2为不喜欢    **************/
        if (newmessageInfo.state.equals("0")) {
            viewHolder.iv_like.setImageResource(R.mipmap.xiaoxi_button_xhb);
        } else if (newmessageInfo.state.equals("1")) {
            viewHolder.iv_like.setImageResource(R.mipmap.xiaoxi_button_xhh);
        } else if (newmessageInfo.state.equals("2")) {
            viewHolder.iv_like.setImageResource(R.mipmap.xiaoxi_button_xhb);
        }


        viewHolder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orlikeClickListener.onlike(position);
                if (newmessageInfo.state.equals("0")) {
                    newmessageInfo.setState("1");
                    viewHolder.iv_like.setImageResource(R.mipmap.xiaoxi_button_xhh);
                } else if (newmessageInfo.state.equals("1")) {
                    newmessageInfo.setState("0");
                    viewHolder.iv_like.setImageResource(R.mipmap.xiaoxi_button_xhb);
                } else if (newmessageInfo.state.equals("2")) {
                    newmessageInfo.setState("1");
                    viewHolder.iv_like.setImageResource(R.mipmap.xiaoxi_button_xhh);
                }
            }
        });
        this.notifyDataSetChanged();
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_pic, iv_like;
        private TextView tv_times;
    }


    private OrlikeClickListener orlikeClickListener;

    public interface OrlikeClickListener {
        void onlike(int pos);

        void ondislike(int pos);
    }

    public void setOnOrlikeClickListener(OrlikeClickListener orlikeClickListener) {
        this.orlikeClickListener = orlikeClickListener;
    }
}
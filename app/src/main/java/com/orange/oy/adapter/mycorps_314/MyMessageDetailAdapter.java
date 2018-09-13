package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orange.oy.R;
import com.orange.oy.adapter.NewMessageAdapter;
import com.orange.oy.base.Tools;
import com.orange.oy.info.NewmessageInfo;
import com.orange.oy.network.Urls;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/23.
 * 我的消息详情 V3.20（偶业小秘）
 */

public class MyMessageDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NewmessageInfo> list;

    public MyMessageDetailAdapter(Context context, ArrayList<NewmessageInfo> list) {
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

            //V3.20
            viewHolder.itemmessage_ly1 = convertView.findViewById(R.id.itemmessage_ly1);
            viewHolder.itemmessage_ly2 = convertView.findViewById(R.id.itemmessage_ly2);
            viewHolder.itemmessage_time = (TextView) convertView.findViewById(R.id.itemmessage_time);
            viewHolder.itemmessage_name = (TextView) convertView.findViewById(R.id.itemmessage_name);
            viewHolder.itemmessage_content = (TextView) convertView.findViewById(R.id.itemmessage_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final NewmessageInfo newmessageInfo = list.get(position);

        if ("1".equals(newmessageInfo.getType())) {//带图片的消息
            viewHolder.itemmessage_ly1.setVisibility(View.VISIBLE);
            viewHolder.itemmessage_ly2.setVisibility(View.GONE);
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
        } else {//纯文本消息
            viewHolder.itemmessage_ly1.setVisibility(View.GONE);
            viewHolder.itemmessage_ly2.setVisibility(View.VISIBLE);
            viewHolder.itemmessage_name.setText(newmessageInfo.getTitle());
            viewHolder.itemmessage_content.setText(newmessageInfo.getContent());
        }
        viewHolder.itemmessage_time.setText(newmessageInfo.getCreate_time());
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_pic, iv_like;
        private TextView tv_times;

        //V3.20
        private TextView itemmessage_time, itemmessage_name, itemmessage_content;
        private View itemmessage_ly1, itemmessage_ly2;
    }


    private NewMessageAdapter.OrlikeClickListener orlikeClickListener;

    public interface OrlikeClickListener {
        void onlike(int pos);

        void ondislike(int pos);
    }

    public void setOnOrlikeClickListener(NewMessageAdapter.OrlikeClickListener orlikeClickListener) {
        this.orlikeClickListener = orlikeClickListener;
    }
}

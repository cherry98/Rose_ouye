package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CheckNewMemberInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.MyListView;

import java.util.ArrayList;


/**
 * 审核新队员
 */

public class CheckNewMemberAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CheckNewMemberInfo> checkNewMemberList;
    private ImageLoader imageLoader;


    public CheckNewMemberAdapter(Context context, ArrayList<CheckNewMemberInfo> list) {
        this.context = context;
        this.checkNewMemberList = list;
        imageLoader = new ImageLoader(context);
    }


    @Override
    public int getCount() {
        return checkNewMemberList.size();
    }

    @Override
    public Object getItem(int position) {
        return checkNewMemberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.item_checknewmember);
            viewHolder = new ViewHolder();
            viewHolder.tv_refuse = (TextView) convertView.findViewById(R.id.tv_refuse);
            viewHolder.tv_pass = (TextView) convertView.findViewById(R.id.tv_pass);
            viewHolder.item_pic = (CircularImageView) convertView.findViewById(R.id.item_pic);
            viewHolder.listview = (MyListView) convertView.findViewById(R.id.listview);

            viewHolder.tv_talk = (TextView) convertView.findViewById(R.id.tv_talk);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            viewHolder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
            viewHolder.tv_tasknum = (TextView) convertView.findViewById(R.id.tv_tasknum);
            viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            viewHolder.tv_btn_reply = (TextView) convertView.findViewById(R.id.tv_btn_reply);
            viewHolder.tv_say = (TextView) convertView.findViewById(R.id.tv_say);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CheckNewMemberInfo checkNewMemberInfo = checkNewMemberList.get(position);

        if (TextUtils.isEmpty(checkNewMemberInfo.getUser_img()) || "null".equals(checkNewMemberInfo.getUser_img())) {
            viewHolder.item_pic.setImageResource(R.mipmap.grxx_icon_mrtx);
        } else {
            imageLoader.DisplayImage(Urls.ImgIp + checkNewMemberInfo.getUser_img(), viewHolder.item_pic, R.mipmap.grxx_icon_mrtx);
        }
        viewHolder.tv_name.setText(checkNewMemberInfo.getUser_name());
        viewHolder.tv_date.setText(checkNewMemberInfo.getCreate_time());
        if (TextUtils.isEmpty(checkNewMemberInfo.getCity()) || "null".equals(checkNewMemberInfo.getCity())) {
            viewHolder.tv_address.setText("");
        } else {
            viewHolder.tv_address.setText(checkNewMemberInfo.getCity());
        }
        viewHolder.tv_tel.setText(checkNewMemberInfo.getMobile());
        viewHolder.tv_tasknum.setText("共执行 " + checkNewMemberInfo.getTask_num() + "个任务");


        if ("null".equals(checkNewMemberInfo.getInviter()) || TextUtils.isEmpty(checkNewMemberInfo.getInviter())) {
            viewHolder.tv_say.setText("\"" + checkNewMemberInfo.getUser_name() + "\"" + "申请加入你的战队");
        } else {
            viewHolder.tv_say.setText("\"" + checkNewMemberInfo.getInviter() + "\"" + "邀请" + "\"" +
                    checkNewMemberInfo.getUser_name() + "\"" + "加入你的战队");
        }
        if (checkNewMemberInfo.getReply() != null) {
            if (!checkNewMemberInfo.getReply().isEmpty()) {
                TalklistAdapter talklistAdapter = new TalklistAdapter(context, checkNewMemberInfo.getReply());
                talklistAdapter.setHe(checkNewMemberInfo.getUser_name());
                talklistAdapter.setIndex(position);
                callback.getCheckNewMemberAdapter(talklistAdapter);
                viewHolder.listview.setAdapter(talklistAdapter);
                viewHolder.tv_btn_reply.setVisibility(View.GONE);
            } else {
                viewHolder.tv_btn_reply.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.tv_btn_reply.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_talk.setText(checkNewMemberInfo.getReason());


        viewHolder.tv_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.refuse(position, checkNewMemberInfo.getApplicant());
            }
        });
        viewHolder.tv_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.pass(position, checkNewMemberInfo.getApplicant());
            }
        });

        viewHolder.tv_btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onReplyclick(position);
            }
        });
        return convertView;
    }


    class ViewHolder {
        private TextView tv_refuse, tv_pass, tv_name, tv_date, tv_tel, tv_address, tv_tasknum, tv_say, tv_talk, tv_btn_reply;
        private MyListView listview;
        private CircularImageView item_pic;
    }

    private CheckNewMemberAdapterCallback callback;

    public void setCallback(CheckNewMemberAdapterCallback callback) {
        this.callback = callback;
    }

    public interface CheckNewMemberAdapterCallback {
        void getCheckNewMemberAdapter(TalklistAdapter adapter);

        void refuse(int pos, String applicant);

        void pass(int pos, String applicant);

        void onReplyclick(int position);
    }
}

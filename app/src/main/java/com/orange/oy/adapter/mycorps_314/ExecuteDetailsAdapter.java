package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CheckNewMemberInfo;
import com.orange.oy.info.mycorps.TeamExecuteDetailsInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.MyListView;

import java.util.ArrayList;

import static com.orange.oy.R.id.tv_tasknum;
import static com.orange.oy.R.id.webView;

/**
 * Created by Administrator on 2018/5/22.
 */

public class ExecuteDetailsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TeamExecuteDetailsInfo> detailsInfoArrayList;
    private ImageLoader imageLoader;
//    private int redcolor, blackColor, greyColor;


    public ExecuteDetailsAdapter(Context context, ArrayList<TeamExecuteDetailsInfo> detailsInfoArrayList) {
        this.context = context;
        this.detailsInfoArrayList = detailsInfoArrayList;
        imageLoader = new ImageLoader(context);
//        redcolor = Color.parseColor("#F65D57");
//        blackColor = Color.parseColor("#231916");
//        greyColor = Color.parseColor("#A0A0A0");
    }


    @Override
    public int getCount() {
        return detailsInfoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return detailsInfoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.item_execute_details);
            viewHolder = new ViewHolder();
            viewHolder.iv_identity = (ImageView) convertView.findViewById(R.id.iv_identity);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_mob = (TextView) convertView.findViewById(R.id.tv_mob);
            viewHolder.item_pic = (CircularImageView) convertView.findViewById(R.id.circularImageView);
            viewHolder.iv_sex = (ImageView) convertView.findViewById(R.id.iv_sex);
            viewHolder.tv_get_outlet = (TextView) convertView.findViewById(R.id.tv_get_outlet);  //已领取网点数
            viewHolder.tv_wait_exe_outlet = (TextView) convertView.findViewById(R.id.tv_wait_exe_outlet); //待执行网点数
            viewHolder.tv_check_outlet = (TextView) convertView.findViewById(R.id.tv_check_outlet); //审核中网点数
            viewHolder.tv_unpass_outlet = (TextView) convertView.findViewById(R.id.tv_unpass_outlet); //未通过网点数
            viewHolder.tv_pass_outlet = (TextView) convertView.findViewById(R.id.tv_pass_outlet); //已通过网点数
            viewHolder.tv_call = (TextView) convertView.findViewById(R.id.tv_call);
            viewHolder.tv_1 = (TextView) convertView.findViewById(R.id.tv_1);
            viewHolder.tv_2 = (TextView) convertView.findViewById(R.id.tv_2);
            viewHolder.tv_3 = (TextView) convertView.findViewById(R.id.tv_3);
            viewHolder.tv_4 = (TextView) convertView.findViewById(R.id.tv_4);
            viewHolder.tv_5 = (TextView) convertView.findViewById(R.id.tv_5);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final TeamExecuteDetailsInfo detailsInfo = detailsInfoArrayList.get(position);

        viewHolder.tv_get_outlet.setText(detailsInfo.getGet_outlet());
        viewHolder.tv_wait_exe_outlet.setText(detailsInfo.getWait_exe_outlet());
        viewHolder.tv_check_outlet.setText(detailsInfo.getCheck_outlet());
        viewHolder.tv_unpass_outlet.setText(detailsInfo.getUnpass_outlet());
        viewHolder.tv_pass_outlet.setText(detailsInfo.getPass_outlet());
        if (TextUtils.isEmpty(detailsInfo.getUser_img()) || "null".equals(detailsInfo.getUser_img())) {
            viewHolder.item_pic.setImageResource(R.mipmap.grxx_icon_mrtx);
        } else {
            imageLoader.DisplayImage(Urls.ImgIp + detailsInfo.getUser_img(), viewHolder.item_pic, R.mipmap.grxx_icon_mrtx);
        }
        viewHolder.tv_name.setText(detailsInfo.getUser_name());

        if ("男".equals(detailsInfo.getUser_sex())) { //   "user_sex": "性别（1为女，0为男）"
            viewHolder.iv_sex.setImageResource(R.mipmap.sex_man);
        } else {
            viewHolder.iv_sex.setImageResource(R.mipmap.sex_woman);
        }
        viewHolder.tv_mob.setText(detailsInfo.getMobile());

        // "identity":"对于该项目的身份，1 队长 2队副 3普通成员",
        if (!TextUtils.isEmpty(detailsInfo.getIdentity())) {
            if (detailsInfo.getIdentity().equals("2")) {
                viewHolder.iv_identity.setVisibility(View.VISIBLE);
            }
        }

        if (!TextUtils.isEmpty(detailsInfo.getGet_outlet()) && !TextUtils.isEmpty(detailsInfo.getWait_exe_outlet())) {
            if (detailsInfo.getGet_outlet().equals(detailsInfo.getWait_exe_outlet())) {
                viewHolder.tv_get_outlet.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_wait_exe_outlet.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_check_outlet.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_unpass_outlet.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_pass_outlet.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_1.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_2.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_3.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_4.setTextColor(context.getResources().getColor(R.color.homepage_select));
                viewHolder.tv_5.setTextColor(context.getResources().getColor(R.color.homepage_select));
            } else {
                viewHolder.tv_get_outlet.setTextColor(context.getResources().getColor(R.color.homepage_city));
                viewHolder.tv_wait_exe_outlet.setTextColor(context.getResources().getColor(R.color.homepage_city));
                viewHolder.tv_check_outlet.setTextColor(context.getResources().getColor(R.color.homepage_city));
                viewHolder.tv_unpass_outlet.setTextColor(context.getResources().getColor(R.color.homepage_city));
                viewHolder.tv_pass_outlet.setTextColor(context.getResources().getColor(R.color.homepage_city));
                viewHolder.tv_1.setTextColor(context.getResources().getColor(R.color.app_textcolor2));
                viewHolder.tv_2.setTextColor(context.getResources().getColor(R.color.app_textcolor2));
                viewHolder.tv_3.setTextColor(context.getResources().getColor(R.color.app_textcolor2));
                viewHolder.tv_4.setTextColor(context.getResources().getColor(R.color.app_textcolor2));
                viewHolder.tv_5.setTextColor(context.getResources().getColor(R.color.app_textcolor2));
            }
        }

        viewHolder.tv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拨打电话
                callback.callPhone(position, detailsInfo.getMobile());
            }
        });
        return convertView;
    }


    class ViewHolder {
        private TextView tv_get_outlet, tv_wait_exe_outlet, tv_check_outlet, tv_unpass_outlet, tv_pass_outlet, tv_name,
                tv_tasknum, tv_mob;
        private TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_call;
        private CircularImageView item_pic;
        private ImageView iv_sex, iv_identity;
    }

    private ExecuteDetailsAdapterCallback callback;

    public void setCallback(ExecuteDetailsAdapterCallback callback) {
        this.callback = callback;
    }

    public interface ExecuteDetailsAdapterCallback {
        void callPhone(int pos, String tel);
    }
}
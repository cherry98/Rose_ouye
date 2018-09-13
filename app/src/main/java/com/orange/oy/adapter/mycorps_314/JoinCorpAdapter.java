package com.orange.oy.adapter.mycorps_314;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.ChatInfo;
import com.orange.oy.info.mycorps.JoinCorpInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.FlowLayoutView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/5/11.
 * 加入战队页面adapter
 */

public class JoinCorpAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<JoinCorpInfo> list = new ArrayList<>();
    private ImageLoader imageLoader;
    private boolean isClick = false;

    public JoinCorpAdapter(Context context, ArrayList<JoinCorpInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    public boolean isClick() {
        return isClick;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_joincorp);
            viewHolder.itemjoincorp_img = (CircularImageView) convertView.findViewById(R.id.itemjoincorp_img);
            viewHolder.itemjoincorp_name = (TextView) convertView.findViewById(R.id.itemjoincorp_name);
            viewHolder.itemjoincorp_id = (TextView) convertView.findViewById(R.id.itemjoincorp_id);
            viewHolder.itemjoincorp_info = (TextView) convertView.findViewById(R.id.itemjoincorp_info);
            viewHolder.itemjoincorp_city = (TextView) convertView.findViewById(R.id.itemjoincorp_city);
            viewHolder.itemjoincorp_task = (TextView) convertView.findViewById(R.id.itemjoincorp_task);
            viewHolder.itemjoincorp_state = (TextView) convertView.findViewById(R.id.itemjoincorp_state);
            viewHolder.itemjoincorp_refuse = (TextView) convertView.findViewById(R.id.itemjoincorp_refuse);
            viewHolder.itemjoincorp_status = (TextView) convertView.findViewById(R.id.itemjoincorp_status);
            viewHolder.itemjoincorp_special = (FlowLayoutView) convertView.findViewById(R.id.itemjoincorp_special);
            viewHolder.itemjoincorp_listview = (ListView) convertView.findViewById(R.id.itemjoincorp_listview);
            viewHolder.itemjoincorp_apply = convertView.findViewById(R.id.itemjoincorp_apply);
            viewHolder.itemjoincorp_join_layout = convertView.findViewById(R.id.itemjoincorp_join_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        JoinCorpInfo joinCorpInfo = list.get(position);
        if (TextUtils.isEmpty(joinCorpInfo.getTeam_img()) || "null".equals(joinCorpInfo.getTeam_img())) {
            viewHolder.itemjoincorp_img.setImageResource(R.mipmap.grxx_icon_mrtx);
        } else {
            imageLoader.DisplayImage(Urls.ImgIp + joinCorpInfo.getTeam_img(), viewHolder.itemjoincorp_img, R.mipmap.grxx_icon_mrtx);
        }
        try {
            JSONArray jsonArray = joinCorpInfo.getSpeciality();
            viewHolder.itemjoincorp_special.removeAllViews();
            for (int i = 0; i < jsonArray.length(); i++) {
                final TextView textView = new TextView(context);
                textView.setText(jsonArray.getString(i));
                textView.setPadding(10, 5, 10, 10);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(12);
                textView.setTextColor(context.getResources().getColor(R.color.homepage_select));
                textView.setBackgroundResource(R.drawable.flowlayout_shape);
                viewHolder.itemjoincorp_special.addView(textView);
            }
            ArrayList<ChatInfo> reply_list = new ArrayList<>();
            JSONArray reply = joinCorpInfo.getChatInfo();
            for (int i = 0; i < reply.length(); i++) {
                ChatInfo chatInfo = new ChatInfo();
                JSONObject object = reply.optJSONObject(i);
                chatInfo.setText(object.getString("text"));
                chatInfo.setType(object.getString("type"));
                chatInfo.setReceiver(object.getString("receiver"));
                chatInfo.setSender(object.getString("sender"));
                chatInfo.setApply_id(joinCorpInfo.getApply_id());
                chatInfo.setTeam_id(joinCorpInfo.getTeam_id());
                reply_list.add(chatInfo);
            }
            int size = reply_list.size();
            if (size > 0) {
                viewHolder.itemjoincorp_listview.setVisibility(View.VISIBLE);
                int height = Tools.dipToPx((Activity) context, (20 + 7) * size + 10);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.itemjoincorp_listview.getLayoutParams();
                layoutParams.height = height;
                viewHolder.itemjoincorp_listview.setLayoutParams(layoutParams);
                JoinChatAdapter joinChatAdapter = new JoinChatAdapter(context, reply_list);
                viewHolder.itemjoincorp_listview.setAdapter(joinChatAdapter);
                joinChatAdapter.setOnRefreshListener(new JoinChatAdapter.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        onRefreshListener.onRefresh();
                    }
                });
            } else {
                viewHolder.itemjoincorp_listview.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.itemjoincorp_name.setText(joinCorpInfo.getTeam_name());
        viewHolder.itemjoincorp_id.setText(joinCorpInfo.getTeam_code());
//        viewHolder.itemjoincorp_info.setText("队长：" + joinCorpInfo.getCaptain() + "   " + joinCorpInfo.getMobile());
        viewHolder.itemjoincorp_info.setText("队长：" + joinCorpInfo.getCaptain());
        viewHolder.itemjoincorp_city.setText(joinCorpInfo.getProvince());
        viewHolder.itemjoincorp_task.setText("已执行任务" + joinCorpInfo.getTask_num() + "个   共" + joinCorpInfo.getUser_num() + "人");
        String state = joinCorpInfo.getState();
        if ("0".equals(state)) {
            viewHolder.itemjoincorp_state.setVisibility(View.VISIBLE);
            viewHolder.itemjoincorp_apply.setVisibility(View.GONE);
            viewHolder.itemjoincorp_join_layout.setOnTouchListener(null);
        } else if ("-1".equals(state)) {
            viewHolder.itemjoincorp_state.setVisibility(View.GONE);
            viewHolder.itemjoincorp_apply.setVisibility(View.VISIBLE);
            viewHolder.itemjoincorp_join_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isClick = true;
                    return false;
                }
            });
        }
        if ("1".equals(joinCorpInfo.getAuth_status())) {
            viewHolder.itemjoincorp_status.setText("已认证");
        } else {
            viewHolder.itemjoincorp_status.setText("未认证");
        }
        if (Tools.StringToInt(joinCorpInfo.getRefuse_num()) > 0) {
            viewHolder.itemjoincorp_refuse.setVisibility(View.VISIBLE);
            viewHolder.itemjoincorp_refuse.setText("被拒绝" + joinCorpInfo.getRefuse_num() + "次");
        } else {
            viewHolder.itemjoincorp_refuse.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private CircularImageView itemjoincorp_img;
        private TextView itemjoincorp_name, itemjoincorp_id, itemjoincorp_info, itemjoincorp_city,
                itemjoincorp_task, itemjoincorp_state, itemjoincorp_refuse, itemjoincorp_status;
        private FlowLayoutView itemjoincorp_special;
        private ListView itemjoincorp_listview;
        private View itemjoincorp_apply, itemjoincorp_join_layout;
    }

    private OnRefreshListener onRefreshListener;

    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }
}

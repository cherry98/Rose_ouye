package com.orange.oy.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.TaskillustratesActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.Mp3Model;
import com.orange.oy.info.ProjectRewardInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.RecodePlayView;

import java.util.ArrayList;
import java.util.List;

import static com.orange.oy.R.id.iv_pic2;


/**
 *
 */

public class TaskillustratesAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<ProjectRewardInfo> list;
    private ImageLoader imageLoader;

    public TaskillustratesAdapter(Context context, ArrayList<ProjectRewardInfo> object) {
        this.context = context;
        this.list = object;
        this.layoutInflater = LayoutInflater.from(context);
        imageLoader = new ImageLoader(context);
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
            convertView = layoutInflater.inflate(R.layout.item_task_illsutrate, parent, false);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_storeName = (TextView) convertView.findViewById(R.id.tv_storeName);
            viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            viewHolder.lin_money = (LinearLayout) convertView.findViewById(R.id.lin_money);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.iv_pic2 = (ImageView) convertView.findViewById(R.id.iv_pic2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ProjectRewardInfo projectRewardInfo = list.get(position);

        //"reward_type":"奖励类型，1为现金，2为礼品",
        viewHolder.tv_money.setText(projectRewardInfo.getMoney());

        if (!Tools.isEmpty(projectRewardInfo.getMerchant())) {
            viewHolder.tv_storeName.setText("商家 ：" + projectRewardInfo.getMerchant());
        }

        if (!Tools.isEmpty(projectRewardInfo.getReward_type())) {

            String url = projectRewardInfo.getGift_url();
            //  1为现金，2为礼品
            if ("2".equals(projectRewardInfo.getReward_type())) {
                viewHolder.tv_name.setText(projectRewardInfo.getGift_name());
                viewHolder.lin_money.setVisibility(View.GONE);
                viewHolder.iv_pic.setVisibility(View.VISIBLE);
                viewHolder.iv_pic2.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(projectRewardInfo.getGift_url())) {
                    if (projectRewardInfo.getGift_url().startsWith("http")) {
                        url = url + "?x-oss-process=image/resize,m_fill,h_100,w_100";
                    } else {
                        url = Urls.Endpoint3 + url + "?x-oss-process=image/resize,m_fill,h_100,w_100";
                    }
                    imageLoader.DisplayImage(url, viewHolder.iv_pic);
                }
            } else {
                viewHolder.iv_pic.setVisibility(View.GONE);
                viewHolder.iv_pic2.setVisibility(View.VISIBLE);
                viewHolder.iv_pic2.setImageResource(R.mipmap.chai_button_ling);
                viewHolder.tv_name.setText("现金红包");
                viewHolder.lin_money.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        private TextView tv_name, tv_storeName, tv_money;
        private ImageView iv_pic, iv_pic2;
        private LinearLayout lin_money;
    }

}


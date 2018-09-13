package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.OpenRedpackageActivity;
import com.orange.oy.base.Tools;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/7/19.
 */

public class OpenRedpackageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<OpenRedpackageActivity.RedPackageInfo> list;
    private boolean isOutlet;

    public OpenRedpackageAdapter(Context context, ArrayList<OpenRedpackageActivity.RedPackageInfo> list, boolean isOutlet) {
        this.context = context;
        this.list = list;
        this.isOutlet = isOutlet;
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
            convertView = Tools.loadLayout(context, R.layout.item_openredpackage);
            viewHolder.itemopenpackage_price = (TextView) convertView.findViewById(R.id.itemopenpackage_price);
            viewHolder.itemopenpackage_seller = (TextView) convertView.findViewById(R.id.itemopenpackage_seller);
            viewHolder.itemopenpackage_type1 = (TextView) convertView.findViewById(R.id.itemopenpackage_type1);
            viewHolder.itemopenpackage_type2 = (TextView) convertView.findViewById(R.id.itemopenpackage_type2);
            viewHolder.itemopenpackage_ly = convertView.findViewById(R.id.itemopenpackage_ly);
            viewHolder.itemopenpackage_ly2 = convertView.findViewById(R.id.itemopenpackage_ly2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            if (isOutlet) {//到店
                viewHolder.itemopenpackage_ly.setVisibility(View.VISIBLE);
                viewHolder.itemopenpackage_type1.setText("额外奖励");
                viewHolder.itemopenpackage_type2.setText("已收入您的个人任务中");
            } else {
                viewHolder.itemopenpackage_ly.setVisibility(View.VISIBLE);
                viewHolder.itemopenpackage_type1.setText("现金红包");
                viewHolder.itemopenpackage_type2.setText("已收入到您的偶业账户");
            }
        } else {
            viewHolder.itemopenpackage_ly.setVisibility(View.GONE);
        }

        OpenRedpackageActivity.RedPackageInfo redPackageInfo = list.get(position);

        String open_status = redPackageInfo.getOpen_status();
        if ("1".equals(open_status)) {
            viewHolder.itemopenpackage_ly2.setBackgroundResource(R.drawable.bg_r_4_stroke_33000000);
            viewHolder.itemopenpackage_price.setBackgroundResource(R.mipmap.get_redpacked);
            viewHolder.itemopenpackage_price.setText("");
        } else {
            viewHolder.itemopenpackage_ly2.setBackgroundResource(R.drawable.bg_r_4_stroke_ffffffff);
            if (isOutlet) {
                viewHolder.itemopenpackage_price.setText("¥" + redPackageInfo.getGift_money());
            } else {
                viewHolder.itemopenpackage_price.setText("¥" + redPackageInfo.getMoney());
            }
            viewHolder.itemopenpackage_price.setBackgroundResource(R.mipmap.get_redpack);
        }
        if (isOutlet) {
            viewHolder.itemopenpackage_seller.setText(redPackageInfo.getGift_name());
        } else {
            viewHolder.itemopenpackage_seller.setText(redPackageInfo.getSponsor_name());
        }

        return convertView;
    }

    class ViewHolder {
        private TextView itemopenpackage_seller, itemopenpackage_price;
        private View itemopenpackage_ly, itemopenpackage_ly2;
        private TextView itemopenpackage_type1, itemopenpackage_type2;
    }
}

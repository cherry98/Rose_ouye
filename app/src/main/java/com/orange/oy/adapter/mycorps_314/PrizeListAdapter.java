package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.PrizeListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/15.
 * 礼品奖励列表 V3.19
 */

public class PrizeListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PrizeListInfo> list;
    private ImageLoader imageLoader;

    public PrizeListAdapter(Context context, ArrayList<PrizeListInfo> list) {
        this.context = context;
        this.list = list;
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
            convertView = Tools.loadLayout(context, R.layout.item_prizelist);
            viewHolder.itemprizelist_pname = (TextView) convertView.findViewById(R.id.itemprizelist_pname);
            viewHolder.itemprizelist_name = (TextView) convertView.findViewById(R.id.itemprizelist_name);
            viewHolder.itemprizelist_code = (TextView) convertView.findViewById(R.id.itemprizelist_code);
            viewHolder.itemprizelist_state = (TextView) convertView.findViewById(R.id.itemprizelist_state);
            viewHolder.itemprizelist_prize = (TextView) convertView.findViewById(R.id.itemprizelist_prize);
            viewHolder.itemprizelist_seller = (TextView) convertView.findViewById(R.id.itemprizelist_seller);
            viewHolder.itemprizelist_draw = (TextView) convertView.findViewById(R.id.itemprizelist_draw);
            viewHolder.itemprizelist_img = (ImageView) convertView.findViewById(R.id.itemprizelist_img);
            viewHolder.itemprizelist_overtime = (ImageView) convertView.findViewById(R.id.itemprizelist_overtime);
            viewHolder.itemprizelist_ly = convertView.findViewById(R.id.itemprizelist_ly);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PrizeListInfo prizeListInfo = list.get(position);
        viewHolder.itemprizelist_pname.setText(prizeListInfo.getProject_name());
        viewHolder.itemprizelist_name.setText(prizeListInfo.getOutlet_name());
        viewHolder.itemprizelist_code.setText(prizeListInfo.getOutlet_id());
        imageLoader.setShowWH(200).DisplayImage(Urls.Endpoint3 + prizeListInfo.getGift_url(), viewHolder.itemprizelist_img);
        viewHolder.itemprizelist_prize.setText(prizeListInfo.getGift_name());
        viewHolder.itemprizelist_seller.setText("商家：" + prizeListInfo.getMerchant());
        String state = prizeListInfo.getExpired();
        if ("1".equals(state)) {//已过期
            viewHolder.itemprizelist_draw.setVisibility(View.GONE);
            viewHolder.itemprizelist_overtime.setVisibility(View.VISIBLE);
            viewHolder.itemprizelist_ly.setBackgroundResource(R.drawable.unify_input);
        } else {
            viewHolder.itemprizelist_draw.setVisibility(View.VISIBLE);
            viewHolder.itemprizelist_overtime.setVisibility(View.GONE);
            viewHolder.itemprizelist_ly.setBackgroundResource(R.drawable.itemalltask_background);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemprizelist_pname, itemprizelist_name, itemprizelist_code, itemprizelist_state,
                itemprizelist_prize, itemprizelist_seller, itemprizelist_draw;
        private ImageView itemprizelist_img, itemprizelist_overtime;
        private View itemprizelist_ly;
    }
}

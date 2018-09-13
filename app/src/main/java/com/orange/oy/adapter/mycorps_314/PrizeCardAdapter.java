package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.PrizeCardInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/8/23.
 * 礼品卡券 V3.20
 */

public class PrizeCardAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PrizeCardInfo> list;
    private ImageLoader imageLoader;

    public PrizeCardAdapter(Context context, ArrayList<PrizeCardInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_prizecard);
            viewHolder.itemprizecard_img = (SimpleDraweeView) convertView.findViewById(R.id.itemprizecard_img);
            viewHolder.itemprizecard_name = (TextView) convertView.findViewById(R.id.itemprizecard_name);
            viewHolder.itemprizecard_price = (TextView) convertView.findViewById(R.id.itemprizecard_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PrizeCardInfo prizeCardInfo = list.get(position);
        Uri uri = Uri.parse(Urls.Endpoint3 + prizeCardInfo.getImg_url());
        viewHolder.itemprizecard_img.setImageURI(uri);
        viewHolder.itemprizecard_name.setText("价值" + prizeCardInfo.getGift_money() + "元" + prizeCardInfo.getGift_name());
        viewHolder.itemprizecard_price.setText("商家：" + prizeCardInfo.getMerchant());
        return convertView;
    }

    class ViewHolder {
        private TextView itemprizecard_name, itemprizecard_price;
        private SimpleDraweeView itemprizecard_img;
    }
}

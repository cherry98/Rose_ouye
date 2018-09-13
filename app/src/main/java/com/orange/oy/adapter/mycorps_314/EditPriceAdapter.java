package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.mycorps.CorpGrabDetailInfo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/5/29.
 * 调整价格页面 V3.15
 */

public class EditPriceAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CorpGrabDetailInfo> list;
    private boolean isClick1;
    private boolean isClick2;
    private boolean isCurrent;

    public EditPriceAdapter(Context context, ArrayList<CorpGrabDetailInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean isClick1() {
        return isClick1;
    }

    public boolean isClick2() {
        return isClick2;
    }

    public void clearClick() {
        isClick1 = false;
        isClick2 = false;
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
            convertView = Tools.loadLayout(context, R.layout.item_editprice);
            viewHolder.itemeditprice_name = (TextView) convertView.findViewById(R.id.itemeditprice_name);
            viewHolder.itemeditprice_money = (TextView) convertView.findViewById(R.id.itemeditprice_money);
            viewHolder.itemeditprice_code = (TextView) convertView.findViewById(R.id.itemeditprice_code);
            viewHolder.itemeditprice_addr = (TextView) convertView.findViewById(R.id.itemeditprice_addr);
            viewHolder.itemeditprice_carrytime = (TextView) convertView.findViewById(R.id.itemeditprice_carrytime);
            viewHolder.itemeditprice_plus = (ImageView) convertView.findViewById(R.id.itemeditprice_plus);
            viewHolder.itemeditprice_minus = (ImageView) convertView.findViewById(R.id.itemeditprice_minus);
            viewHolder.itemeditprice_ly = convertView.findViewById(R.id.itemeditprice_ly);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CorpGrabDetailInfo corpGrabDetailInfo = list.get(position);
        if (isCurrent) {
            viewHolder.itemeditprice_money.setText(corpGrabDetailInfo.getCurrent());
        } else {
            viewHolder.itemeditprice_money.setText(corpGrabDetailInfo.getPrimary());
        }
        viewHolder.itemeditprice_plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick1 = true;
                return false;
            }
        });
        viewHolder.itemeditprice_minus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isClick2 = true;
                return false;
            }
        });
        if (corpGrabDetailInfo.isMin()) {
            viewHolder.itemeditprice_ly.setBackgroundResource(R.drawable.itemalltask_background2);
            viewHolder.itemeditprice_minus.setImageResource(R.mipmap.price_minus2);
        } else {
            viewHolder.itemeditprice_ly.setBackgroundResource(R.drawable.itemalltask_background);
            viewHolder.itemeditprice_minus.setImageResource(R.mipmap.price_minus);
        }
        if (corpGrabDetailInfo.isMax()) {
            viewHolder.itemeditprice_plus.setImageResource(R.mipmap.price_plus2);
        } else {
            viewHolder.itemeditprice_plus.setImageResource(R.mipmap.price_plus);
        }
        viewHolder.itemeditprice_name.setText(corpGrabDetailInfo.getOutlet_name());
        viewHolder.itemeditprice_code.setText(corpGrabDetailInfo.getOutlet_num());
        viewHolder.itemeditprice_addr.setText(corpGrabDetailInfo.getOutlet_address());
        viewHolder.itemeditprice_carrytime.setText(corpGrabDetailInfo.getTimeDetail());
        return convertView;
    }

    class ViewHolder {
        private TextView itemeditprice_name, itemeditprice_money, itemeditprice_code, itemeditprice_addr, itemeditprice_carrytime;
        private ImageView itemeditprice_plus, itemeditprice_minus;
        private View itemeditprice_ly;
    }
}

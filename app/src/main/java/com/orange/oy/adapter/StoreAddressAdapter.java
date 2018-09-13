package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.GiftInfo;
import com.orange.oy.info.StoreAddressInfo;

import java.util.ArrayList;


/**
 * Created by Administrator on 2018/8/17.
 */

public class StoreAddressAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<StoreAddressInfo> list;
    private AbandonButton abandonButton;
    private boolean isSwif;  //等于 1是滑动
    private int delWidth = 132;

    public StoreAddressAdapter(Context context, ArrayList<StoreAddressInfo> list) {
        this.context = context;
        this.list = list;
        delWidth = (int) context.getResources().getDimension(R.dimen.task_del_width);
    }

    public void setDelet(boolean isSwif1) {
        this.isSwif = isSwif1;
        notifyDataSetChanged();
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
            convertView = Tools.loadLayout(context, R.layout.item_store_address);
            viewHolder = new ViewHolder();
            viewHolder.main = (LinearLayout) convertView.findViewById(R.id.main);
            viewHolder.swipemenulib = (LinearLayout) convertView.findViewById(R.id.swipemenulib);
            viewHolder.btnDelete = (TextView) convertView.findViewById(R.id.btnDelete);
            viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.swipemenulib.scrollTo(0, 0);


        final StoreAddressInfo addressInfo = list.get(position);

        viewHolder.tv_name.setText(addressInfo.getOutlet_name() + "(" + addressInfo.getOutlet_num() + ")");
        viewHolder.tv_address.setText(addressInfo.getOutlet_address());


        if (isSwif) {
            viewHolder.btnDelete.setVisibility(View.VISIBLE);
            viewHolder.swipemenulib.scrollTo(delWidth, 0);
            viewHolder.main.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
        } else {
            viewHolder.swipemenulib.scrollTo(0, 0);
            viewHolder.main.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
        }

        viewHolder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abandonButton.onitemclick(position);
            }
        });
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                abandonButton.onclick(position);
            }
        });
        return convertView;
    }


    class ViewHolder {
        private TextView tv_name, tv_address;
        private TextView btnDelete;
        private LinearLayout main, itemapplyone_lay1, itemapplyone_time_ly;
        private LinearLayout swipemenulib;
        private ImageView iv_pic;
    }

    public interface AbandonButton {
        void onclick(int position);

        void onitemclick(int position);

    }

    public void setAbandonButtonListener(AbandonButton abandonButton) {
        this.abandonButton = abandonButton;
    }

}

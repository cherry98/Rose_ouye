package com.orange.oy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

public class NewFriendsAdapter extends BaseAdapter implements View.OnTouchListener {
    private ArrayList<MyteamNewfdInfo> list;
    private ImageLoader imageLoader;
    private Context context;

    public NewFriendsAdapter(Context context, ArrayList<MyteamNewfdInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    public void upList(ArrayList<MyteamNewfdInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_newfriends);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_newfds_name);
            viewHolder.button = (TextView) convertView.findViewById(R.id.item_newfds_button);
            viewHolder.img = (CircularImageView) convertView.findViewById(R.id.item_newfds_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyteamNewfdInfo myteamNewfdInfo = list.get(position);
        viewHolder.name.setText(myteamNewfdInfo.getName());
        imageLoader.DisplayImage(myteamNewfdInfo.getImg(), viewHolder.img, R.mipmap.my_img_de);
        switch (myteamNewfdInfo.getState()) {
            case 0: {
                viewHolder.button.setBackgroundResource(R.color.colorPrimaryDark);
                viewHolder.button.setTextColor(Color.WHITE);
                viewHolder.button.setText(context.getResources().getString(R.string.newfriends_item_state1));
                viewHolder.button.setOnTouchListener(this);
            }
            break;
            case 1: {
                viewHolder.button.setBackgroundResource(android.R.color.transparent);
                viewHolder.button.setTextColor(context.getResources().getColor(R.color.app_textcolor));
                viewHolder.button.setText(context.getResources().getString(R.string.newfriends_item_state2));
                viewHolder.button.setOnTouchListener(null);
            }
            break;
        }
        return convertView;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void clearSelect() {
        isSelect = false;
    }

    private boolean isSelect;

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isSelect = true;
        }
        return false;
    }

    class ViewHolder {
        public TextView name, button;
        public CircularImageView img;
    }
}

package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.AddPhoneActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.ItemaddPhoneInfo;
import com.orange.oy.info.LableMerInfo;

import java.util.List;

/**
 * Created by beibei
 */
public class AsPhoneSelectAdapter extends BaseAdapter {

    private List<LableMerInfo> mDatas;
    private Context mContext;


    public AsPhoneSelectAdapter(Context mContext, List<LableMerInfo> mData) {
        this.mContext = mContext;
        this.mDatas = mData;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);

    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_addphoneselect, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LableMerInfo itemBean = mDatas.get(position);
        holder.tv_name.setText(itemBean.getLabel_name());

        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneEdit.itemclick(position);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private TextView tv_name;
        private LinearLayout remove;

        public ViewHolder(View convertView) {
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            remove = (LinearLayout) convertView.findViewById(R.id.remove);

        }
    }


    private PhoneEdit phoneEdit;

    public void setPhoneEditListener(PhoneEdit phoneEditListener) {
        this.phoneEdit = phoneEditListener;
    }

    public interface PhoneEdit {
        void itemclick(int pos);

    }
}

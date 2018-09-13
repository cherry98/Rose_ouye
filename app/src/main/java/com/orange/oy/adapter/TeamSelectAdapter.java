package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.info.LableMerInfo;
import com.orange.oy.info.mycorps.MyCorpsInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.List;

/**
 * Created by beibei
 */
public class TeamSelectAdapter extends BaseAdapter {

    private List<MyCorpsInfo> mDatas;
    private Context mContext;
    private ImageLoader imageLoader;


    public TeamSelectAdapter(Context mContext, List<MyCorpsInfo> mData) {
        this.mContext = mContext;
        this.mDatas = mData;
        imageLoader = new ImageLoader(mContext);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_team_select, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MyCorpsInfo itemBean = mDatas.get(position);
        holder.tv_name.setText(itemBean.getTeam_name());

        String url = itemBean.getTeam_img();
        if (!TextUtils.isEmpty(url) && !"null".equals(url)) {
            imageLoader.DisplayImage(Urls.ImgIp + url, holder.iv_pic, R.mipmap.grxx_icon_mrtx);
        } else {
            holder.iv_pic.setImageResource(R.mipmap.grxx_icon_mrtx);
        }


        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    itemBean.setSelect(true);
                } else {
                    itemBean.setSelect(false);
                }
            }
        });

        if (itemBean.isSelect()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView tv_name;
        private LinearLayout remove;
        private CircularImageView iv_pic;
        private CheckBox checkbox;

        public ViewHolder(View convertView) {
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            remove = (LinearLayout) convertView.findViewById(R.id.remove);
            iv_pic = (CircularImageView) convertView.findViewById(R.id.iv_pic);
            checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

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

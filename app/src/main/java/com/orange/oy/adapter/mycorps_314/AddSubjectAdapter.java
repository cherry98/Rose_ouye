package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.OptionsListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/9/4.
 * 添加多选单选 V323.21
 */

public class AddSubjectAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<OptionsListInfo> list;
    private ImageLoader imageLoader;
    private boolean isEdit;//编辑状态

    public AddSubjectAdapter(Context context, ArrayList<OptionsListInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_addsubject);
            viewHolder.itemsubject_number = (TextView) convertView.findViewById(R.id.itemsubject_number);
            viewHolder.itemsubject_name = (EditText) convertView.findViewById(R.id.itemsubject_name);
            viewHolder.itemsubject_delete = (ImageView) convertView.findViewById(R.id.itemsubject_delete);
            viewHolder.itemsubject_img = (ImageView) convertView.findViewById(R.id.itemsubject_img);
            viewHolder.itemsubject_deleteimg = (ImageView) convertView.findViewById(R.id.itemsubject_deleteimg);
            viewHolder.itemsubject_ly1 = convertView.findViewById(R.id.itemsubject_ly1);
            viewHolder.itemsubject_ly2 = convertView.findViewById(R.id.itemsubject_ly2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OptionsListInfo optionsListInfo = list.get(position);

        if (viewHolder.itemsubject_name.getTag() instanceof TextWatcher) {//移除EditText监听
            viewHolder.itemsubject_name.removeTextChangedListener((TextWatcher) viewHolder.itemsubject_name.getTag());
        }

        if ("-1".equals(optionsListInfo.getOption_id())) {//添加选项
            viewHolder.itemsubject_ly1.setVisibility(View.GONE);
            viewHolder.itemsubject_ly2.setVisibility(View.VISIBLE);
            viewHolder.itemsubject_ly2.setOnClickListener(new MyOnClickListener(1));//添加选项
        } else {//选项内容填写
            viewHolder.itemsubject_ly1.setVisibility(View.VISIBLE);
            viewHolder.itemsubject_ly2.setVisibility(View.GONE);
            if (!Tools.isEmpty(optionsListInfo.getOption_num())) {
                viewHolder.itemsubject_number.setText(optionsListInfo.getOption_num());
            }
            viewHolder.itemsubject_delete.setOnClickListener(new MyOnClickListener(2, position));//删除选项
            //图片
            viewHolder.itemsubject_img.setOnClickListener(new MyOnClickListener(3, position));//添加图片
            String path = optionsListInfo.getPath();
            String url = optionsListInfo.getPhoto_url();
            if (!Tools.isEmpty(path)) {
                imageLoader.setShowWH(200).DisplayImage(path, viewHolder.itemsubject_img, -2);
            } else if (!Tools.isEmpty(url)) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    imageLoader.setShowWH(200).DisplayImage(url, viewHolder.itemsubject_img, -2);
                } else {
                    imageLoader.setShowWH(200).DisplayImage(Urls.ImgIp + url, viewHolder.itemsubject_img, -2);
                }
            } else {
                viewHolder.itemsubject_img.setImageResource(R.mipmap.pzp_button_tjzp);
            }
            if (isEdit && !Tools.isEmpty(optionsListInfo.getPhoto_url())) {//回显数据才可可删除图片
                viewHolder.itemsubject_deleteimg.setVisibility(View.VISIBLE);
                viewHolder.itemsubject_deleteimg.setOnClickListener(new MyOnClickListener(4, position));
            } else {
                viewHolder.itemsubject_deleteimg.setVisibility(View.GONE);
            }
            String name = optionsListInfo.getOption_name();
            if (!Tools.isEmpty(name)) {
                viewHolder.itemsubject_name.setText(name);
            } else {
                viewHolder.itemsubject_name.setText("");
            }

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s)) {
                        list.get(position).setOption_name("");
                    } else {
                        list.get(position).setOption_name(s.toString());
                    }

                }
            };
            viewHolder.itemsubject_name.addTextChangedListener(textWatcher);
            viewHolder.itemsubject_name.setTag(textWatcher);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemsubject_number;
        private EditText itemsubject_name;
        private ImageView itemsubject_delete, itemsubject_img, itemsubject_deleteimg;
        private View itemsubject_ly1, itemsubject_ly2;
    }

    private OnSubjectListener onSubjectListener;

    public void setOnSubjectListener(OnSubjectListener onSubjectListener) {
        this.onSubjectListener = onSubjectListener;
    }

    public interface OnSubjectListener {
        void addItem();//1

        void deleteItem(int position);//2

        void addImg(int position);//3

        void deleteImg(int positon);//4
    }

    private class MyOnClickListener implements View.OnClickListener {
        int type;
        int postion;

        public MyOnClickListener(int type) {
            this.type = type;
        }

        public MyOnClickListener(int type, int postion) {
            this.type = type;
            this.postion = postion;
        }

        @Override
        public void onClick(View v) {
            if (type == 1) {
                onSubjectListener.addItem();
            } else if (type == 2) {
                onSubjectListener.deleteItem(postion);
            } else if (type == 3) {
                onSubjectListener.addImg(postion);
            } else if (type == 4) {
                onSubjectListener.deleteImg(postion);
            }
        }
    }
}

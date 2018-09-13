package com.orange.oy.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TaskQuestionInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/10/17.
 * TaskRadioView的adapter
 */

public class MyListViewAdapter extends BaseAdapter implements View.OnTouchListener {
    private Context context;
    private ArrayList<TaskQuestionInfo> list;
    private ImageLoader imageLoader;
    private int selectPosition = -1;
    private boolean isReset;

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public MyListViewAdapter(Context context, ArrayList<TaskQuestionInfo> list, boolean isReset) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
        selectPosition = -1;
        this.isReset = isReset;
    }

    public void setReset(boolean isReset) {
        this.isReset = isReset;
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
            convertView = Tools.loadLayout(context, R.layout.view_task_question_radiobutton);
            viewHolder.radiobutton_layout = (LinearLayout) convertView.findViewById(R.id.radiobutton_layout);
            viewHolder.radiobutton_img = (ImageView) convertView.findViewById(R.id.radiobutton_img);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            viewHolder.editText = (EditText) convertView.findViewById(R.id.edittext);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final TaskQuestionInfo taskQuestionInfo = list.get(position);
        if (isReset) {
            if (taskQuestionInfo.isClick()) {
                selectPosition = position;
                viewHolder.radiobutton_img.setImageResource(R.mipmap.single_selected);
                viewHolder.radiobutton_layout.setBackgroundResource(R.drawable.questionradio_s_bg);
            } else {
                viewHolder.radiobutton_img.setImageResource(R.mipmap.single_notselect);
                viewHolder.radiobutton_layout.setBackgroundResource(R.drawable.questionradio_nos_bg);
            }
        } else {
            if (selectPosition == position) {
                viewHolder.radiobutton_img.setImageResource(R.mipmap.single_selected);
                viewHolder.radiobutton_layout.setBackgroundResource(R.drawable.questionradio_s_bg);
            } else {
                viewHolder.radiobutton_img.setImageResource(R.mipmap.single_notselect);
                viewHolder.radiobutton_layout.setBackgroundResource(R.drawable.questionradio_nos_bg);
            }
        }
        if (isReset) {
            if (taskQuestionInfo.isShowEdit()) {
                viewHolder.editText.setVisibility(View.VISIBLE);
                viewHolder.editText.setFocusable(false);
                viewHolder.editText.setFocusableInTouchMode(false);
                if (taskQuestionInfo.isClick()) {
                    viewHolder.editText.setText(taskQuestionInfo.getNote());
                } else {
                    viewHolder.editText.setText("");
                }
            } else {
                viewHolder.editText.setVisibility(View.GONE);
            }
        } else {
            if (taskQuestionInfo.isShowEdit()) {
                viewHolder.editText.setVisibility(View.VISIBLE);
                viewHolder.editText.setFocusable(true);
                viewHolder.editText.setFocusableInTouchMode(true);
                if ("1".equals(taskQuestionInfo.getIsforcedfill())) {
                    viewHolder.editText.setHint("请填写备注（必填）");
                } else {
                    viewHolder.editText.setHint("请填写备注");
                }
            } else {
                viewHolder.editText.setVisibility(View.GONE);
            }
        }
        String url = taskQuestionInfo.getPhoto_url();
        if (url == null || url.equals("null") || TextUtils.isEmpty(url)) {
            viewHolder.img.setVisibility(View.GONE);
        } else {
            viewHolder.img.setVisibility(View.VISIBLE);
            url = url.replaceAll("\"", "").replaceAll("\\\\", "");
            if (!url.startsWith("http")) {
                if (url.startsWith("GZB/")) {
                    url = Urls.Endpoint3 + url + "?x-oss-process=image/resize,l_350";
                } else {
                    url = Urls.ImgIp + url;
                }
            }
//            url = Urls.ImgIp + url.replaceAll("\"", "").replaceAll("\\\\", "");
            imageLoader.DisplayImage(url, viewHolder.img);
        }
        convertView.setOnTouchListener(this);
        if (taskQuestionInfo.getName() == null || "".equals(taskQuestionInfo.getName())) {
            viewHolder.text.setText("");
        } else {
            viewHolder.text.setText(taskQuestionInfo.getName());
        }
        return convertView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof EditText) {
            EditText et = (EditText) v;
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
        } else {
            ViewHolder holder = (ViewHolder) v.getTag();
            holder.editText.setFocusable(false);
            holder.editText.setFocusableInTouchMode(false);
        }
        return false;

    }

    class ViewHolder {
        private ImageView radiobutton_img;
        private TextView text;
        private ImageView img;
        private EditText editText;
        private LinearLayout radiobutton_layout;
    }
}

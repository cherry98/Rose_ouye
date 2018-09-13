package com.orange.oy.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orange.oy.R;
import com.orange.oy.info.ItemaddPhoneInfo;

import java.util.List;

/**
 * Created by beibei
 */
public class AddPhoneAdapter extends BaseAdapter {

    private List<ItemaddPhoneInfo> mData;
    private Context mContext;
    private int index = -1;
    //定义一个HashMap，用来存放EditText的值，Key是position
    //  HashMap<Integer, String> hashMap = new HashMap<Integer, String>();

    public AddPhoneAdapter(Context mContext, List<ItemaddPhoneInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    public ItemaddPhoneInfo itemBean;

    public void setmData(ItemaddPhoneInfo itemBean) {
        this.itemBean = itemBean;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_edittext, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (holder.editText.getTag() instanceof TextWatcher) {
            holder.editText.removeTextChangedListener((TextWatcher) holder.editText.getTag());
        }
        ItemaddPhoneInfo itemBean = mData.get(position);
        holder.tv_num.setText((position + 1) + ". " + "手机号");
        if (null != itemBean.getText()) {
            holder.editText.setText(itemBean.getText());
        }

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.size() == 1) {
                    Toast.makeText(mContext, "不可以再添删除啦，亲!", Toast.LENGTH_LONG).show();
                } else {
                    phoneEdit.delete(position);
                }

            }
        });
        holder.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.size() == 1) {
                    Toast.makeText(mContext, "不可以再添删除啦，亲!", Toast.LENGTH_LONG).show();
                } else {
                    phoneEdit.delete(position);
                }
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mData.get(position).setText(s.toString());
            }
        };

        holder.editText.addTextChangedListener(watcher);
        holder.editText.setTag(watcher);
        holder.editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.edit_text:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_UP:
                                index = position;
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }
                }
                return false;
            }
        });


        holder.editText.clearFocus();
        if (index != -1 && index == position) {
            //强制加上焦点
            holder.editText.requestFocus();
            //设置光标显示到编辑框尾部
            holder.editText.setSelection(holder.editText.getText().length());
            //重置
            index = -1;
        }
        return convertView;
    }

    private class ViewHolder {
        private EditText editText;
        private TextView tv_num;
        private LinearLayout remove;
        private Button iv_remove;

        public ViewHolder(View convertView) {
            editText = (EditText) convertView.findViewById(R.id.edit_text);
            tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            remove = (LinearLayout) convertView.findViewById(R.id.remove);
            iv_remove = (Button) convertView.findViewById(R.id.iv_remove);

        }
    }


    private PhoneEdit phoneEdit;

    public void setPhoneEditListener(PhoneEdit phoneEditListener) {
        this.phoneEdit = phoneEditListener;
    }

    public interface PhoneEdit {

        void delete(int pos);

        //void add(int pos);

        //void tongxunlu(int pos);
    }
}

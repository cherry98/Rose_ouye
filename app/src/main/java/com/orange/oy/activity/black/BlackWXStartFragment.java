package com.orange.oy.activity.black;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.allinterface.BlackworkCloseListener;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 暗访任务仿微信第一页
 */
public class BlackWXStartFragment extends BaseFragment implements View.OnClickListener {
    private ListView blackwxstart_listview;
    private ArrayList<String> list = new ArrayList<>();
    private MyAdapter adapter;
    private View mView;
    private TextView blackwxstart_time;
    private TextView blackwxstart_button1, blackwxstart_button2;
    private BlackworkCloseListener blackworkCloseListener;
    private BlackWXSelectListener blackWXSelectListener;
    private boolean isFirst = false;

    public interface BlackWXSelectListener {
        void selectLeft();

        void selectRight();

        void lastPage();
    }

    public void setBlackWXSelectListener(BlackWXSelectListener blackWXSelectListener) {
        this.blackWXSelectListener = blackWXSelectListener;
    }

    public void setBlackworkCloseListener(BlackworkCloseListener blackworkCloseListener) {
        this.blackworkCloseListener = blackworkCloseListener;
    }

    public void showFirst(String[] message) {
        isFirst = true;
        Collections.addAll(list, message);
    }

    public void showFirst2(String[] message) {
        isFirst = true;
        blackwxstart_time.setText(Tools.getTimeByPattern("HH:mm"));
        list.clear();
        Collections.addAll(list, message);
        blackwxstart_button2.setVisibility(View.GONE);
        blackwxstart_button1.setText("开始进店");
        blackwxstart_button1.setOnClickListener(this);
        adapter.notifyDataSetChanged();
    }

    public void showLast() {
        list.clear();
        list.add("没问题了，你出来了嘛？");
        adapter.notifyDataSetChanged();
        blackwxstart_button2.setVisibility(View.GONE);
        blackwxstart_button1.setText("出来了");
        blackwxstart_button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (blackWXSelectListener != null)
                    blackWXSelectListener.lastPage();
            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }

    public void showQuestion(String[] message, String left, String right) {
        isFirst = false;
        blackwxstart_time.setText(Tools.getTimeByPattern("HH:mm"));
        list.clear();
        Collections.addAll(list, message);
        blackwxstart_button2.setVisibility(View.VISIBLE);
        blackwxstart_button1.setText(left);
        blackwxstart_button2.setText(right);
        adapter.notifyDataSetChanged();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_blackwxstart, container, false);
        return mView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackwxstart_time = (TextView) mView.findViewById(R.id.blackwxstart_time);
        blackwxstart_time.setText(Tools.getTimeByPattern("HH:mm"));
        blackwxstart_button1 = (TextView) mView.findViewById(R.id.blackwxstart_button1);
        blackwxstart_button2 = (TextView) mView.findViewById(R.id.blackwxstart_button2);
        blackwxstart_listview = (ListView) mView.findViewById(R.id.blackwxstart_listview);
        adapter = new MyAdapter();
        blackwxstart_listview.setAdapter(adapter);
        blackwxstart_button1.setOnClickListener(this);
        blackwxstart_button2.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blackwxstart_button1: {
                if (blackworkCloseListener != null && isFirst) {
                    blackworkCloseListener.know();
                } else if (!isFirst) {
                    if (blackWXSelectListener != null) {
                        blackWXSelectListener.selectLeft();
                    }
                }
            }
            break;
            case R.id.blackwxstart_button2: {
                if (blackWXSelectListener != null) {
                    blackWXSelectListener.selectRight();
                }
            }
            break;
        }
    }

    class MyAdapter extends BaseAdapter {

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
            TextView textView = null;
            if (convertView == null) {
                convertView = Tools.loadLayout(getContext(), R.layout.view_blackchat);
                textView = (TextView) convertView.findViewById(R.id.blackchat_content);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }
            textView.setText(list.get(position));
            return convertView;
        }
    }
}

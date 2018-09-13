package com.orange.oy.base;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.adapter.CalendarSelectorAdapter;
import com.orange.oy.dialog.MyDialog;
import com.orange.oy.info.CalendarSelectInfo;

import java.util.ArrayList;

/**
 * 选择器总类
 */
public class SelecterDialog extends LinearLayout implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static AlertDialog dialog;
    private static OnSelecterClickListener onSelecterClickListener;
    private TextView left, right, title;
    private View selecter_right2;
    private RelativeLayout selecter_title_layout;
    private ListView listView;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selecter_left: {
                if (onSelecterClickListener != null) onSelecterClickListener.onClickLeft();
            }
            break;
            case R.id.selecter_right2:
            case R.id.selecter_right: {
                if (onSelecterClickListener != null) onSelecterClickListener.onClickRight();
            }
            break;
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onSelecterClickListener != null) onSelecterClickListener.onItemClick(position);
    }

    public interface OnSelecterClickListener {
        void onClickLeft();

        void onClickRight();

        void onItemClick(int position);
    }

    protected SelecterDialog(Context context, OnSelecterClickListener listener) {
        super(context);
        onSelecterClickListener = listener;
        Tools.loadLayout(this, R.layout.dialog_bottom_selecter);
        selecter_title_layout = (RelativeLayout) findViewById(R.id.selecter_title_layout);
        left = (TextView) findViewById(R.id.selecter_left);
        right = (TextView) findViewById(R.id.selecter_right);
        title = (TextView) findViewById(R.id.selecter_title);
        selecter_right2 = findViewById(R.id.selecter_right2);
        listView = (ListView) findViewById(R.id.selecter_listview);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        selecter_right2.setOnClickListener(this);
        title.setOnClickListener(this);
    }

    protected void settingTitleColor() {
        selecter_title_layout.setBackgroundColor(Color.parseColor("#ffe3e2e2"));
        left.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    protected void settingLeft(String left) {
        this.left.setText(left);
    }

    protected void settingRight(String right) {
        this.right.setText(right);
    }

    protected void buttonForCalendar() {
        this.right.setVisibility(View.GONE);
        this.selecter_right2.setVisibility(View.VISIBLE);
    }

    protected void settingTitle(String title) {
        this.title.setText(title);
    }

    protected void settingData(Context context, ArrayList<String> list) {
        listView.setOnItemClickListener(this);
        SelecterAdapter adapter = new SelecterAdapter(context, list);
        listView.setAdapter(adapter);
    }

    protected void settingDataForCalendar(Context context, ArrayList<CalendarSelectInfo> list) {
        listView.setOnItemClickListener(this);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        CalendarSelectorAdapter adapter = new CalendarSelectorAdapter(context, list);
        listView.setAdapter(adapter);
    }

    public static AlertDialog showSelecter(Context context, ArrayList<String> list, OnSelecterClickListener listener) {
        return showSelecter(context, null, null, null, list, true, listener);
    }

    public static AlertDialog showSelecter(Context context, String title, ArrayList<String> list,
                                           OnSelecterClickListener listener) {
        return showSelecter(context, title, null, null, list, true, listener);
    }

    public static MyDialog myDialog;

    public static MyDialog showSelecterForCalendar(Context context, String title, String left,
                                                   ArrayList<CalendarSelectInfo> list, boolean cancelable,
                                                   OnSelecterClickListener listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        SelecterDialog view = new SelecterDialog(context, listener);
        view.settingTitleColor();
        view.buttonForCalendar();
        if (left != null) {
            view.settingLeft(left);
        }
        if (title != null) {
            view.settingTitle(title);
        }
        view.settingDataForCalendar(context, list);
        myDialog = new MyDialog((BaseActivity) context, view, true);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        return myDialog;
    }
//    public static AlertDialog showSelecterForCalendar(Context context, String title, String left,
//                                                      ArrayList<CalendarSelectInfo> list, boolean cancelable,
//                                                      OnSelecterClickListener listener) {
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//        SelecterDialog view = new SelecterDialog(context, listener);
//        view.settingTitleColor();
//        view.buttonForCalendar();
//        if (left != null) {
//            view.settingLeft(left);
//        }
//        if (title != null) {
//            view.settingTitle(title);
//        }
//        view.settingDataForCalendar(context, list);
//        dialog = new AlertDialog.Builder(context).setCancelable(cancelable).create();
//        dialog.setCanceledOnTouchOutside(cancelable);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        window.setWindowAnimations(R.style.selecterStyle);
//        dialog.show();
//        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        dialog.addContentView(view, params);
//        int[] screes = Tools.getScreeInfo(context);
//        dialog.getWindow().setLayout(screes[0], (int) context.getResources().getDimension(R.dimen
//                .dialog_selecter_height));
//        return dialog;
//    }

    public static AlertDialog showSelecter(Context context, String title, String left, String right,
                                           ArrayList<String> list, boolean cancelable, OnSelecterClickListener
                                                   listener) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        SelecterDialog view = new SelecterDialog(context, listener);
        if (left != null) {
            view.settingLeft(left);
        }
        if (right != null) {
            view.settingRight(right);
        }
        if (title != null) {
            view.settingTitle(title);
        }
        view.settingData(context, list);
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(cancelable)
                .create();
        dialog.setCanceledOnTouchOutside(cancelable);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.selecterStyle);
        dialog.show();
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        dialog.addContentView(view, params);
        int[] screes = Tools.getScreeInfo(context);
        dialog.getWindow().setLayout(screes[0],
                (int) context.getResources().getDimension(R.dimen.dialog_selecter_height));
        return dialog;
    }

    public static MyDialog showPhotoSelecter(Context context, OnClickListener item1Listener, OnClickListener
            item2Listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_photoselecter, null);
        view.findViewById(R.id.dialog_photosel_item1).setOnClickListener(item1Listener);
        view.findViewById(R.id.dialog_photosel_item2).setOnClickListener(item2Listener);
        myDialog = new MyDialog((BaseActivity) context, view, true);
        myDialog.showAtLocation((view.findViewById(R.id.main)), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        return myDialog;
    }
    public static AlertDialog showView(Context context, View view) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(true)
                .create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialog.addContentView(view, params);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.getWindow().setLayout(Tools.getScreeInfoWidth(context),
                Tools.getScreeInfoHeight(context));
        return dialog;
    }

    public static void dismiss() {
        if (dialog != null)
            dialog.dismiss();
        if (myDialog != null)
            myDialog.dismiss();
    }

    class SelecterAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> list;

        public SelecterAdapter(Context context, ArrayList<String> list) {
            this.context = context;
            this.list = list;
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
            TextView textView = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_selecter, null);
                textView = (TextView) convertView.findViewById(R.id.selecter_name);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }
            String name = list.get(position);
            if (!TextUtils.isEmpty(name))
                textView.setText(name);
            else
                textView.setText("");
            return convertView;
        }
    }
}

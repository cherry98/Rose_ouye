package com.orange.oy.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * 更改店铺
 */
public class ChangeshopDialog extends LinearLayout implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView;
    private ArrayList<Map<String, String>> list;
    private MyAdapter adapter;
    private OnItemClickListener onItemClickListener;
    //    private static AlertDialog dialog;
    private static MyDialog myDialog;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closetask_close: {
//                dialog.dismiss();
                myDialog.dismiss();
            }
            break;
        }
    }

    public interface OnItemClickListener {
        void itemClickForChangeshop(int position, Object object);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public ChangeshopDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_changeshop);
        listView = (ListView) findViewById(R.id.changeshop_listview);
        findViewById(R.id.closetask_close).setOnClickListener(this);
    }

    public void setData(ArrayList<Map<String, String>> list) {
        this.list = list;
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public static MyDialog showDialog(Context context, ArrayList<Map<String, String>> list, OnItemClickListener
            listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        ChangeshopDialog changeshopDialog = new ChangeshopDialog(context);
        changeshopDialog.setData(list);
        changeshopDialog.setOnItemClickListener(listener);
        myDialog = new MyDialog((BaseActivity) context, changeshopDialog, true);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        return myDialog;
    }
//    public static AlertDialog showDialog(Context context, ArrayList<Map<String, String>> list, OnItemClickListener
//            listener) {
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//        ChangeshopDialog changeshopDialog = new ChangeshopDialog(context);
//        changeshopDialog.setData(list);
//        changeshopDialog.setOnItemClickListener(listener);
//        dialog = new AlertDialog.Builder(context).setCancelable(true).create();
//        dialog.setCanceledOnTouchOutside(true);
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        window.setWindowAnimations(R.style.selecterStyle);
//        dialog.show();
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        dialog.addContentView(changeshopDialog, params);
//        int[] screes = Tools.getScreeInfo(context);
//        dialog.getWindow().setLayout(screes[0], (int) context.getResources().getDimension(R.dimen
//                .dialog_closetask_height));
//        return dialog;
//    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onItemClickListener != null) {
            onItemClickListener.itemClickForChangeshop(position, null);
            myDialog.dismiss();
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
            ViewHolde viewHolde;
            if (convertView == null) {
                viewHolde = new ViewHolde();
                convertView = Tools.loadLayout(getContext(), R.layout.item_changeshop);
                viewHolde.right = (TextView) convertView.findViewById(R.id.item_changeshop_right);
                viewHolde.left = (TextView) convertView.findViewById(R.id.item_changeshop_left);
                convertView.setTag(viewHolde);
            } else {
                viewHolde = (ViewHolde) convertView.getTag();
            }
            Map<String, String> map = list.get(position);
            Iterator<String> iterator = map.keySet().iterator();
            String key = null, name = null;
            if (iterator.hasNext()) {
                key = iterator.next();
                name = map.get(key);
            }
            viewHolde.left.setText(key + "");
            viewHolde.right.setText(name + "");
            return convertView;
        }

        class ViewHolde {
            TextView right, left;
        }
    }
}
